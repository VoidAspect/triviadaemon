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
                .type(QuestionType.MULTIPLE)
                .requestContext(new TriviaRequestContext())
                .build();

        val triviaResponse = questionService.apply(request);

        log.debug("{}", triviaResponse);

        assertEquals("Trivia: New Question", triviaResponse.getTitle());
        assertTrue(triviaResponse.isQuestion());
        assertFalse(triviaResponse.isTerminal());
        assertNotNull(triviaResponse.getText());
        assertNotNull(triviaResponse.getSpeech());
        assertTrue(triviaResponse.getText().contains("difficulty: medium, type: Multiple choice."));
        assertTrue(triviaResponse.getCorrectAnswer().startsWith("Correct answer is "));

    }

}