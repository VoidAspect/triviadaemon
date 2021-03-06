package com.voidaspect.triviadaemon.handler.apiai;

import com.voidaspect.triviadaemon.handler.MockContext;
import com.voidaspect.triviadaemon.handler.apiai.data.IncompleteResult;
import com.voidaspect.triviadaemon.handler.apiai.data.WebhookRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author mikhail.h
 */
@Slf4j
public class TriviaWebhookRequestHandlerTest {

    private final TriviaWebhookRequestHandler handler =
            new TriviaWebhookRequestHandler();

    @Test
    public void testQuestionRequest() throws Exception {
        Map<String, String> meta = new HashMap<>();
        meta.put("intentName", "question.request");
        Map<String, String> params = new HashMap<>();
        params.put("type", "true or false");
        val result = new IncompleteResult();
        result.setMetadata(meta);
        result.setParameters(params);
        val webhookRequest = new WebhookRequest();
        webhookRequest.setResult(result);

        val webhookResponse = handler.handleRequest(webhookRequest, new MockContext());

        assertNotNull(webhookResponse.getSpeech());
        assertNotNull(webhookResponse.getDisplayText());
        assertEquals("opentdb.com", webhookResponse.getSource());
        assertFalse(webhookResponse.getContextOut().isEmpty());
    }

}