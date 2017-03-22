package com.voidaspect.triviadaemon.handler.apiai;

import com.voidaspect.triviadaemon.handler.apiai.data.RequestContext;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookResponse;

import java.util.Set;

/**
 * Factory for speechlet responses (api.ai webhook response format).
 *
 * @author mikhail.h
 */
final class WebhookResponseFactory {

    private static final String SOURCE = "opentdb.com";

    /**
     * Method for creating the webhook response with text and speech
     *
     * @param speech         the output to be spoken
     * @param text           the text to be displayed
     * @param contextObjects dialog context created by webhook
     * @return SpeechletResponse the speechlet response
     */
    WebhookResponse newWebhookResponse(String speech, String text, Set<RequestContext> contextObjects) {
        WebhookResponse webhookResponse = new WebhookResponse();

        webhookResponse.setSpeech(speech);
        webhookResponse.setDisplayText(text);
        webhookResponse.setSource(SOURCE);
        webhookResponse.setContextOut(contextObjects);

        return webhookResponse;
    }
}
