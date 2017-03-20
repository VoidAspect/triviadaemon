package com.voidaspect.triviadaemon.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author mikhail.h
 */
@Slf4j
public class QuestionServiceTest {

    @Test
    public void testRequest() throws Exception {

        val questionService = new QuestionService();

        val request = TriviaRequest.builder()
                .difficulty(Difficulty.MEDIUM)
                .type(QuestionType.BOOLEAN)
                .build();

        val triviaResponse = questionService.apply(request);

        log.debug("{}", triviaResponse);

        assertEquals("Trivia: New Question", triviaResponse.getTitle());
        assertNotNull(triviaResponse.getText());
        assertNotNull(triviaResponse.getSpeech());

    }

}