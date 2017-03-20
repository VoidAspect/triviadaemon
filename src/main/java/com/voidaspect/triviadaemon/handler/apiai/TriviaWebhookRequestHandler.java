package com.voidaspect.triviadaemon.handler.apiai;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookRequest;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookResponse;

/**
 * @author mikhail.h
 */
public final class TriviaWebhookRequestHandler implements RequestHandler<WebhookRequest, WebhookResponse> {

    @Override
    public WebhookResponse handleRequest(WebhookRequest input, Context context) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
