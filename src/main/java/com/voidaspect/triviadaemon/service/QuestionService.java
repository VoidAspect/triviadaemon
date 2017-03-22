package com.voidaspect.triviadaemon.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.voidaspect.triviadaemon.dialog.ASKTitle;
import com.voidaspect.triviadaemon.dialog.Phrase;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.Format;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mikhail.h
 */
@Slf4j
final class QuestionService implements Function<TriviaRequest, TriviaResponse> {

    private static final String QUESTION_LIMIT = String.valueOf(1);

    private static final String SCHEME = "https";

    private static final String HOST = "opentdb.com";

    private static final String PATH = "api.php";

    private static final String REQUEST_ENCODING = "url3986";

    private static final Format INFO_FORMAT =
            new MessageFormat("Category: {0}, difficulty: {1}, type: {2}.\n");

    private static final Format ANSWER_FORMAT =
            new MessageFormat("Correct answer is {0}.");

    private static final String RESPONSE_ENCODING = "UTF-8";

    private final OkHttpClient httpClient;

    private final ObjectMapper objectMapper;

    QuestionService() {
        httpClient = new OkHttpClient();
        httpClient.setReadTimeout(3, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(3, TimeUnit.SECONDS);
        objectMapper = new ObjectMapper();
    }

    @Override
    public TriviaResponse apply(TriviaRequest request) {

        /* Create HTTP Request */

        /* defaults */
        val httpUrlBuilder = new HttpUrl.Builder()
                .scheme(SCHEME)
                .host(HOST)
                .addPathSegment(PATH)
                .addQueryParameter("amount", QUESTION_LIMIT)
                .addQueryParameter("encode", REQUEST_ENCODING);

        /* difficulty */
        Optional.ofNullable(request.getDifficulty())
                .map(Difficulty::getName)
                .ifPresent(difficulty -> httpUrlBuilder
                        .addQueryParameter("difficulty", difficulty));

        /* type */
        Optional.ofNullable(request.getType())
                .map(QuestionType::getName)
                .ifPresent(type -> httpUrlBuilder
                        .addQueryParameter("type", type));

        val httpUrl = httpUrlBuilder.build();

        /* build the GET request */
        val httpRequest = new Request.Builder()
                .url(httpUrl)
                .get()
                .build();

        /* Response builder is populated by default by error messages */
        val responseBuilder = TriviaResponse.builder()
                .isTerminal(false)
                .isQuestion(true)
                .speech(Phrase.SERVICE_ERROR.get())
                .title(ASKTitle.NO_RESPONSE.get());

        final TriviaResponse triviaResponse;
        try {
            val httpResponse = httpClient.newCall(httpRequest).execute();

            log.debug("http status: {}", httpResponse.message());

            val responseWrapper = objectMapper.readValue(
                    httpResponse.body().byteStream(),
                    HTTPResponseWrapper.class);

            decodeResponse(responseWrapper);

            val results = responseWrapper.getResults();
            if (results != null && results.length > 0) {
                val result = results[0];
                val questionType = QuestionType.getByName(result.getType())
                        .orElseThrow(() -> new UnsupportedOperationException("Unsupported question type"));

                val question = result.getQuestion();
                val answerPlain = result.getCorrectAnswer();

                val info = INFO_FORMAT.format(new Object[]{
                        result.getCategory(),
                        result.getDifficulty(),
                        questionType.getDescription()
                });

                final String speech;
                final String answerText;
                final String text;
                if (questionType == QuestionType.BOOLEAN) {
                    answerText = answerPlain.toLowerCase();
                    speech = question + ' ' + Phrase.TRUE_OR_FALSE.get();
                    text = info + speech;
                } else {
                    answerText = answerPlain;

                    val answerList = result.getIncorrectAnswers();
                    answerList.add(answerPlain);
                    Collections.shuffle(answerList, new Random());
                    answerList.replaceAll(answer ->
                            (answerList.indexOf(answer) + 1) + ": " + answer);

                    speech = question + answerList.stream()
                            .collect(Collectors.joining(", ", " ", "."));

                    text = info + question + answerList.stream()
                            .collect(Collectors.joining("\n", "\n", ""));
                }
                val correctAnswer = ANSWER_FORMAT.format(new Object[]{answerText});

                //Populate response builder with data
                responseBuilder
                        .title(ASKTitle.NEW_QUESTION.get())
                        .speech(speech)
                        .text(text)
                        .correctAnswerPlain(answerPlain)
                        .correctAnswer(correctAnswer);
            }
        } catch (Exception e) {
            log.error("Exception during Trivia request: ", e);
        } finally {
            triviaResponse = responseBuilder.build();
        }
        return triviaResponse;
    }

    private static void decodeResponse(HTTPResponseWrapper wrapper) {
        for (val question : wrapper.getResults()) {
            question.setDifficulty(decode(question.getDifficulty()));
            question.setCategory(decode(question.getCategory()));
            question.setType(decode(question.getType()));
            question.setQuestion(decode(question.getQuestion()));
            question.setCorrectAnswer(decode(question.getCorrectAnswer()));
            question.getIncorrectAnswers()
                    .replaceAll(QuestionService::decode);
        }
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    private static String decode(String s) {
        return URLDecoder.decode(s, RESPONSE_ENCODING);
    }

    @Data
    private static final class HTTPResponseWrapper {

        @JsonProperty(value = "response_code")
        private int responseCode;

        @JsonProperty(value = "results")
        private Question[] results;

    }

    @Data
    private static final class Question {

        @JsonProperty(value = "category")
        private String category;

        @JsonProperty(value = "type")
        private String type;

        @JsonProperty(value = "difficulty")
        private String difficulty;

        @JsonProperty(value = "question")
        private String question;

        @JsonProperty(value = "correct_answer")
        private String correctAnswer;

        @JsonProperty(value = "incorrect_answers")
        private List<String> incorrectAnswers;

    }

}
