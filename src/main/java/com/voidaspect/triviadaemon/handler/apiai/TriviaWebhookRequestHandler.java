package com.voidaspect.triviadaemon.handler.apiai;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookRequest;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookResponse;
import com.voidaspect.triviadaemon.service.QuestionService;
import com.voidaspect.triviadaemon.service.TriviaService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.function.Function;

/**
 * AWS Lambda handler for webhook used by api.ai agent.
 *
 * @author mikhail.h
 */
@Slf4j
public final class TriviaWebhookRequestHandler implements RequestHandler<WebhookRequest, WebhookResponse> {

    /**
     * Functional service responsible for converting requests into responses.
     * @see TriviaWebhookService
     */
    private final Function<WebhookRequest, WebhookResponse> webhookFunction =
            new TriviaWebhookService(new TriviaService(new QuestionService()));

    /**
     * {@inheritDoc}
     */
    @Override
    public WebhookResponse handleRequest(WebhookRequest input, Context context) {
        log.info("api.ai webhook request: {}", input);

        val output = webhookFunction.apply(input);

        log.info("api.ai webhook response: {}", output);

        return output;
    }

}
