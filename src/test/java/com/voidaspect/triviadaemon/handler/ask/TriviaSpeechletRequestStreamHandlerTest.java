package com.voidaspect.triviadaemon.handler.ask;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voidaspect.triviadaemon.handler.MockContext;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * @author mikhail.h
 */
@Slf4j
public class TriviaSpeechletRequestStreamHandlerTest {

    private final SpeechletRequestStreamHandler handler =
            new TriviaSpeechletRequestStreamHandler();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSupportedIds() throws Exception {
        val ids = TriviaSpeechletRequestStreamHandler.SUPPORTED_APPLICATION_IDS;
        assertFalse(ids.isEmpty());
        assertTrue(ids.stream().noneMatch(String::isEmpty));
        log.debug("supported app ids: {}", ids);
    }

    @Test
    public void testHelpIntent() throws Exception {
        compareResponse("/sample/helpRequest.json", "/sample/helpResponse.json");
    }




    private void compareResponse(String requestUrl, String responseUrl) throws IOException, URISyntaxException {
        val thisClass = getClass();
        try(val requestStream = thisClass.getResourceAsStream(requestUrl);
            val responseStream = thisClass.getResourceAsStream(responseUrl);
            val outputStream = new ByteArrayOutputStream()) {

            handler.handleRequest(requestStream, outputStream, new MockContext());
            val actual = objectMapper.readTree(outputStream.toByteArray());
            val expected = objectMapper.readTree(responseStream);
            assertEquals(expected, actual);
        }
    }
}