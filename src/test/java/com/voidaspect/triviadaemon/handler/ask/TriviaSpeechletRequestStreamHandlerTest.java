package com.voidaspect.triviadaemon.handler.ask;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author mikhail.h
 */
@Slf4j
public class TriviaSpeechletRequestStreamHandlerTest {

    @Test
    public void testSupportedIds() throws Exception {
        val ids = TriviaSpeechletRequestStreamHandler.SUPPORTED_APPLICATION_IDS;
        assertFalse(ids.isEmpty());
        assertTrue(ids.stream().noneMatch(String::isEmpty));
        log.debug("supported app ids: {}", ids);
    }

}