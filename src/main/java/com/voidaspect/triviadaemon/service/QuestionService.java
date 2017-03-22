package com.voidaspect.triviadaemon.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.voidaspect.triviadaemon.dialog.ASKTitle;
import com.voidaspect.triviadaemon.dialog.Phrase;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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
            new MessageFormat("Category: {0}, difficulty: {1}, type: {2}.");

    private static final Format ANSWER_FORMAT =
            new MessageFormat("Correct answer is {0}.");

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

            val results = responseWrapper.getResults();
            if (results != null && results.length > 0) {
                val result = results[0];
                val questionType = QuestionType.getByName(result.getType());

                val question = decodeResult(result.getQuestion());
                val answerPlain = decodeResult(result.getCorrectAnswer());
                final String speech;
                final String answerText;
                if (questionType == QuestionType.BOOLEAN) {
                    answerText = answerPlain.toLowerCase();
                    speech = question + ' ' + Phrase.TRUE_OR_FALSE.get();
                } else {
                    answerText = answerPlain;
                    speech = question;
                }
                val correctAnswer = ANSWER_FORMAT.format(new Object[]{answerText});

                val params = new Object[]{
                        result.getCategory(),
                        result.getDifficulty(),
                        questionType.getDescription()
                };
                val text = decodeResult(INFO_FORMAT.format(params)) + '\n' + speech;

                //Populate response builder with data
                responseBuilder
                        .title(ASKTitle.NEW_QUESTION.get())
                        .speech(speech)
                        .text(text)
                        .correctAnswerPlain(answerPlain)
                        .correctAnswer(correctAnswer);
            }
        } catch (IOException e) {
            log.error("IOException during Trivia request: ", e);
        } finally {
            triviaResponse = responseBuilder.build();
        }
        return triviaResponse;
    }

    private String decodeResult(String string) throws UnsupportedEncodingException {
        return URLDecoder.decode(string, "UTF-8").trim();
    }

    @Data
    private static final class HTTPResponseWrapper {

        @JsonProperty(value = "response_code")
        private int responseCode;

        @JsonProperty(value = "results")
        private Question[] results;

    }

    @Data
    @JsonIgnoreProperties(value = {"incorrect_answers"})
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

    }

}
