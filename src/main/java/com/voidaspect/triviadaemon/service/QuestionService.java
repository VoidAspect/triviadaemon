package com.voidaspect.triviadaemon.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.voidaspect.triviadaemon.dialog.ASKTitle;
import com.voidaspect.triviadaemon.dialog.Phrase;
import com.voidaspect.triviadaemon.service.data.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mikhail.h
 */
@Slf4j
public final class QuestionService implements Function<QuestionRequest, TriviaResponse> {

    private static final String QUESTION_LIMIT = String.valueOf(1);

    private static final String SCHEME = "https";

    private static final String HOST = "opentdb.com";

    private static final String PATH = "api.php";

    private static final String REQUEST_ENCODING = "url3986";

    private static final Format INFO_FORMAT =
            new MessageFormat("Category: {0}, difficulty: {1}, type: {2}.\n");

    private static final Format ANSWER_FORMAT =
            new MessageFormat("Correct answer is {0}.");

    private static final Format OPENTDB_RESPONSE_ERROR =
            new MessageFormat("opentdb.com api returned error: code={0}, response={1}");

    private final OkHttpClient httpClient;

    private final ObjectMapper objectMapper;

    public QuestionService() {
        httpClient = new OkHttpClient();
        httpClient.setReadTimeout(3, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(3, TimeUnit.SECONDS);
        objectMapper = new ObjectMapper();
    }

    @Override
    public TriviaResponse apply(QuestionRequest request) {
        val httpRequest = createHttpRequest(request);

        val responseBuilder = TriviaResponse.builder()
                .isTerminal(false);

        final TriviaResponse triviaResponse;
        try {
            val httpResponse = httpClient.newCall(httpRequest).execute();

            log.debug("http status: {}", httpResponse.message());

            val responseTree = objectMapper.readTree(httpResponse.body().charStream());

            log.debug("response tree: {}", responseTree);

            int responseCode = responseTree.get("response_code").asInt();
            if (responseCode != 0) {
                val message = OPENTDB_RESPONSE_ERROR.format(new Object[]{responseCode, responseTree});
                throw new IOException(message);
            }

            val result = responseTree.get("results").get(0);
            val questionType = QuestionType.getByName(decode(result.get("type")))
                    .orElseThrow(() -> new UnsupportedOperationException("Unsupported question type"));
            val question = decode(result.get("question"));
            val answer = decode(result.get("correct_answer"));
            val category = decode(result.get("category"));
            val difficulty = decode(result.get("difficulty"));
            val info = INFO_FORMAT.format(new Object[]{category, difficulty, questionType.getDescription()});

            final String speech;
            final String text;
            final String answerText;
            final String answerPlain;
            if (questionType == QuestionType.BOOLEAN) {
                answerText = answer.toLowerCase();
                answerPlain = answerText;
                speech = question + ' ' + Phrase.TRUE_OR_FALSE.get();
                text = info + speech;
            } else {
                answerText = answer;

                val answerList = new ArrayList<String>();
                answerList.add(answer);
                for (val node : result.get("incorrect_answers")) {
                    answerList.add(decode(node));
                }
                Collections.shuffle(answerList, new Random());

                answerPlain = String.valueOf(answerList.indexOf(answer) + 1);

                answerList.replaceAll(choice ->
                        (answerList.indexOf(choice) + 1) + ": " + choice);

                speech = question + answerList.stream()
                        .collect(Collectors.joining(", ", " ", "."));

                text = info + question + answerList.stream()
                        .collect(Collectors.joining("\n", "\n", ""));
            }
            val correctAnswerDescription = ANSWER_FORMAT.format(new Object[]{answerText});

            val correctAnswer = new CorrectAnswer(correctAnswerDescription, answerPlain);

            responseBuilder
                    .title(ASKTitle.NEW_QUESTION.get())
                    .speech(speech)
                    .text(text)
                    .correctAnswer(correctAnswer);
        } catch (Exception e) {
            log.error("Exception during Trivia request", e);
            responseBuilder
                    .speech(Phrase.SERVICE_ERROR.get())
                    .title(ASKTitle.NO_RESPONSE.get());
        } finally {
            triviaResponse = responseBuilder.build();
        }
        return triviaResponse;
    }

    /**
     * Create HTTP Request for opentdb.com/api.php
     *
     * @param request data for request
     * @return http request
     */
    private Request createHttpRequest(QuestionRequest request) {
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
        return new Request.Builder()
                .url(httpUrl)
                .get()
                .build();
    }

    private static String decode(JsonNode node) {
        if (!node.isTextual()) {
            throw new IllegalArgumentException("Attempt to decode non-textual node");
        }
        try {
            return URLDecoder.decode(node.textValue(), StandardCharsets.UTF_8.displayName());
        } catch (UnsupportedEncodingException e) {
            throw new UncheckedIOException(e);
        }
    }

}
