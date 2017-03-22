package com.voidaspect.triviadaemon.handler.apiai;

import com.voidaspect.triviadaemon.handler.apiai.data.IncompleteResult;
import com.voidaspect.triviadaemon.handler.apiai.data.RequestContext;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookRequest;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookResponse;
import com.voidaspect.triviadaemon.service.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;

import java.util.*;
import java.util.function.Function;

import static com.voidaspect.triviadaemon.service.TriviaRequestContext.ContextParam.*;

/**
 * @author mikhail.h
 */
final class TriviaWebhookService implements Function<WebhookRequest, WebhookResponse> {

    /**
     * Key for the name of the api.ai intent in {@link IncompleteResult#parameters} map.
     */
    private static final String INTENT_NAME_KEY = "intentName";

    private static final String RECENT_QUESTION_CONTEXT_NAME = "RecentQuestion";

    @Getter(value = AccessLevel.PRIVATE, lazy = true)
    private final TriviaStrategy triviaStrategy = new TriviaStrategy();

    private final WebhookResponseFactory webhookResponseFactory = new WebhookResponseFactory();

    @Override
    public WebhookResponse apply(WebhookRequest webhookRequest) {

        Objects.requireNonNull(webhookRequest,
                "Invalid format for webhook request: request object is null");

        val result = webhookRequest.getResult();

        Objects.requireNonNull(result, "Invalid format for webhook request: " +
                "partial result object is null. Request: " + webhookRequest);

        val intentName = result.getMetadata().get(INTENT_NAME_KEY);

        val triviaRequest = createTriviaRequest(result);

        val triviaResponse = getTriviaStrategy().getIntentByName(intentName).apply(triviaRequest);

        return webhookResponseFactory.newWebhookResponse(
                triviaResponse.getSpeech(),
                triviaResponse.getText(),
                createContextOut(triviaResponse));

    }

    private TriviaRequest createTriviaRequest(IncompleteResult requestData) {

        val requestContext = new TriviaRequestContext();
        val contextParams = requestContext.getContextParams();
        Optional.ofNullable(requestData.getContexts())
                .orElseGet(Collections::emptySet)
                .stream()
                .findFirst()
                .map(RequestContext::getParameters)
                .ifPresent(params -> {
                    contextParams.put(CORRECT_ANSWER, params.get(CORRECT_ANSWER.name()));
                    contextParams.put(QUESTION_SPEECH, params.get(QUESTION_SPEECH.name()));
                    contextParams.put(QUESTION_TEXT, params.get(QUESTION_TEXT.name()));
                });

        val requestBuilder = TriviaRequest.builder()
                .requestContext(requestContext);
        getRequestParam(requestData, ApiAiParam.DIFFICULTY)
                .flatMap(Difficulty::getByName)
                .ifPresent(requestBuilder::difficulty);
        getRequestParam(requestData, ApiAiParam.TYPE)
                .flatMap(QuestionType::getByDescription)
                .ifPresent(requestBuilder::type);
        return requestBuilder.build();
    }

    private Set<RequestContext> createContextOut(TriviaResponse response) {
        final Set<RequestContext> requestContexts;
        if (response.isQuestion()) {
            Map<String, String> contextOutParams = new HashMap<>();
            contextOutParams.put(QUESTION_TEXT.name(), response.getText());
            contextOutParams.put(QUESTION_SPEECH.name(), response.getSpeech());
            contextOutParams.put(CORRECT_ANSWER.name(), response.getCorrectAnswer());

            RequestContext contextOut = new RequestContext();
            contextOut.setName(RECENT_QUESTION_CONTEXT_NAME);
            contextOut.setParameters(contextOutParams);

            requestContexts = Collections.singleton(contextOut);
        } else {
            requestContexts = Collections.emptySet();
        }
        return Collections.unmodifiableSet(requestContexts);
    }

    private Optional<String> getRequestParam(IncompleteResult requestData, ApiAiParam param) {
        return Optional.ofNullable(requestData.getParameters().get(param.getParamName()));
    }
}
