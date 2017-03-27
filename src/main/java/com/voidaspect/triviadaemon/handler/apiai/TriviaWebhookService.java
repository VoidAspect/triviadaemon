package com.voidaspect.triviadaemon.handler.apiai;

import com.voidaspect.triviadaemon.handler.apiai.data.IncompleteResult;
import com.voidaspect.triviadaemon.handler.apiai.data.RequestContext;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookRequest;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookResponse;
import com.voidaspect.triviadaemon.service.*;
import com.voidaspect.triviadaemon.service.data.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.*;
import java.util.function.Function;

import static com.voidaspect.triviadaemon.service.data.TriviaRequestContext.ContextParam.*;

/**
 * Validates the webhook request.
 * <br>Converts {@link WebhookRequest}
 * => {@link TriviaRequest}
 * => {@link TriviaResponse}
 * => {@link WebhookResponse}.
 *
 * @author mikhail.h
 */
@Slf4j
final class TriviaWebhookService implements Function<WebhookRequest, WebhookResponse> {

    /**
     * Key for the name of the api.ai intent in {@link IncompleteResult#parameters} map.
     */
    private static final String INTENT_NAME_KEY = "intentName";

    /**
     * Name of the {@link RequestContext} with information about current quiz.
     */
    private static final String RECENT_QUESTION_CONTEXT_NAME = "recent-question";

    /**
     * {@link TriviaService} instance with lazy getter.
     */
    @Getter(value = AccessLevel.PRIVATE, lazy = true)
    private final TriviaService triviaService = new TriviaService(new QuestionService());

    /**
     * {@link WebhookResponseFactory} instance.
     */
    private final WebhookResponseFactory webhookResponseFactory = new WebhookResponseFactory();

    /**
     * Converts {@link WebhookRequest} => {@link WebhookResponse}
     *
     * @param webhookRequest {@code WebhookRequest} bean
     * @return {@code WebhookResponse} bean
     */
    @Override
    public WebhookResponse apply(WebhookRequest webhookRequest) {

        Objects.requireNonNull(webhookRequest,
                "Invalid format for webhook request: request object is null");

        val result = webhookRequest.getResult();

        Objects.requireNonNull(result, "Invalid format for webhook request: " +
                "partial result object is null. Request: " + webhookRequest);

        val intentName = result.getMetadata().get(INTENT_NAME_KEY);

        val triviaRequest = createTriviaRequest(result);

        log.debug("TriviaRequest: {}", triviaRequest);

        val triviaResponse = getTriviaService()
                .getIntentByName(intentName)
                .apply(triviaRequest);

        return createWebhookResponse(triviaResponse);

    }

    /**
     * Converts {@link IncompleteResult} => {@link TriviaRequest}.
     *
     * @param requestData {@link IncompleteResult} bean.
     * @return {@link TriviaRequest} value-object.
     */
    private TriviaRequest createTriviaRequest(IncompleteResult requestData) {
        val requestContext = new TriviaRequestContext();
        val contextParams = requestContext.getContextParams();
        Optional.ofNullable(requestData.getContexts())
                .orElseGet(Collections::emptySet)
                .stream()
                .filter(context -> context.getName().equals(RECENT_QUESTION_CONTEXT_NAME))
                .findAny()
                .map(RequestContext::getParameters)
                .ifPresent(params -> {
                    contextParams.put(CORRECT_ANSWER, params.get(CORRECT_ANSWER.name()));
                    contextParams.put(CORRECT_ANSWER_PLAIN, params.get(CORRECT_ANSWER_PLAIN.name()));
                    contextParams.put(QUESTION_SPEECH, params.get(QUESTION_SPEECH.name()));
                    contextParams.put(QUESTION_TEXT, params.get(QUESTION_TEXT.name()));
                });

        val questionRequestBuilder = QuestionRequest.builder();
        getRequestParam(requestData, ApiAiParam.DIFFICULTY)
                .flatMap(Difficulty::getByName)
                .ifPresent(questionRequestBuilder::difficulty);
        getRequestParam(requestData, ApiAiParam.TYPE)
                .flatMap(QuestionType::getByDescription)
                .ifPresent(questionRequestBuilder::type);

        Set<String> userInput = new HashSet<>();
        getRequestParam(requestData, ApiAiParam.BOOLEAN)
                .ifPresent(userInput::add);
        getRequestParam(requestData, ApiAiParam.NUMBER)
                .ifPresent(userInput::add);

        return TriviaRequest.builder()
                .question(questionRequestBuilder.build())
                .requestContext(requestContext)
                .guessRequest(new GuessRequest(userInput))
                .build();
    }

    /**
     * Converts {@link TriviaResponse} => {@link WebhookResponse}.
     *
     * @param triviaResponse {@link TriviaResponse} value-object
     * @return {@link WebhookResponse} bean.
     */
    private WebhookResponse createWebhookResponse(TriviaResponse triviaResponse) {
        val text = triviaResponse.getText();
        val speech = triviaResponse.getSpeech();

        val correctAnswer = triviaResponse.getCorrectAnswer();
        final Set<RequestContext> requestContexts;
        if (correctAnswer != null) {
            Map<String, String> contextOutParams = new HashMap<>();
            contextOutParams.put(QUESTION_TEXT.name(), text);
            contextOutParams.put(QUESTION_SPEECH.name(), speech);
            contextOutParams.put(CORRECT_ANSWER.name(), correctAnswer.getAnswerDescription());
            contextOutParams.put(CORRECT_ANSWER_PLAIN.name(), correctAnswer.getAnswerPlain());

            RequestContext contextOut = new RequestContext();
            contextOut.setName(RECENT_QUESTION_CONTEXT_NAME);
            contextOut.setParameters(contextOutParams);
            contextOut.setLifespan(5);

            requestContexts = Collections.singleton(contextOut);
        } else {
            requestContexts = Collections.emptySet();
        }

        return webhookResponseFactory.newWebhookResponse(speech, text, requestContexts);
    }

    /**
     * Utility method for null-safe retrieval of request parameters.
     *
     * @param requestData dto with request data.
     * @param param       parameter to retrieve.
     * @return optional-wrapped parameter value.
     */
    private Optional<String> getRequestParam(IncompleteResult requestData, ApiAiParam param) {
        return Optional.ofNullable(requestData.getParameters())
                .map(params -> params.get(param.getParamName()));
    }
}
