package com.voidaspect.triviadaemon.handler.apiai;

import com.voidaspect.triviadaemon.handler.apiai.data.IncompleteResult;
import com.voidaspect.triviadaemon.handler.apiai.data.RequestContext;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookRequest;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookResponse;
import com.voidaspect.triviadaemon.service.*;
import com.voidaspect.triviadaemon.service.data.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.*;
import java.util.function.Function;

import static com.voidaspect.triviadaemon.handler.apiai.ApiAiParam.*;
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
     * Lifespan of an output context
     */
    private static final int CONTEXT_LIFESPAN = 5;

    /**
     * {@link ServiceProducer} instance.
     */
    private final ServiceProducer intentService;

    /**
     * {@link WebhookResponseFactory} instance.
     */
    private final WebhookResponseFactory webhookResponseFactory;

    TriviaWebhookService(ServiceProducer intentService) {
        this.intentService = intentService;
        webhookResponseFactory = new WebhookResponseFactory();
    }

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

        val triviaResponse = intentService
                .getFunctionByIntentName(intentName)
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
                .map(RequestContext::getParameters)
                .findAny()
                .ifPresent(params -> {
                    contextParams.put(CORRECT_ANSWER, params.get(CORRECT_ANSWER.name()));
                    contextParams.put(CORRECT_ANSWER_PLAIN, params.get(CORRECT_ANSWER_PLAIN.name()));
                    contextParams.put(QUESTION_SPEECH, params.get(QUESTION_SPEECH.name()));
                    contextParams.put(QUESTION_TEXT, params.get(QUESTION_TEXT.name()));
                });

        val questionRequestBuilder = QuestionRequest.builder();

        val parameters = Optional.ofNullable(requestData.getParameters());
        parameters.map(params -> params.get(DIFFICULTY.getParamName()))
                .flatMap(Difficulty::getByName)
                .ifPresent(questionRequestBuilder::difficulty);
        parameters.map(params -> params.get(TYPE.getParamName()))
                .flatMap(QuestionType::getByDescription)
                .ifPresent(questionRequestBuilder::type);

        Set<String> userInput = new HashSet<>();
        parameters.map(params -> params.get(BOOLEAN.getParamName()))
                .ifPresent(userInput::add);
        parameters.map(params -> params.get(NUMBER.getParamName()))
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
            contextOut.setLifespan(CONTEXT_LIFESPAN);

            requestContexts = Collections.singleton(contextOut);
        } else {
            requestContexts = Collections.emptySet();
        }

        return webhookResponseFactory.newWebhookResponse(speech, text, requestContexts);
    }

}
