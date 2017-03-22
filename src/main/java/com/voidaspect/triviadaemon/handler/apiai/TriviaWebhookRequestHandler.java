package com.voidaspect.triviadaemon.handler.apiai;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookRequest;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.function.Function;

/**
 * @author mikhail.h
 */
@Slf4j
public final class TriviaWebhookRequestHandler implements RequestHandler<WebhookRequest, WebhookResponse> {

    private final Function<WebhookRequest, WebhookResponse> webhookFunction =
            new TriviaWebhookService();

    @Override
    public WebhookResponse handleRequest(WebhookRequest input, Context context) {
        log.info("api.ai webhook request: {}", input);

        val output = webhookFunction.apply(input);

        log.info("api.ai webhook response: {}", output);

        return output;
    }

}
