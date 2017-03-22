package com.voidaspect.triviadaemon.service;

import com.voidaspect.triviadaemon.service.data.QuestionRequest;
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

        val question = QuestionRequest.builder()
                .difficulty(Difficulty.MEDIUM)
                .type(QuestionType.MULTIPLE)
                .build();

        val triviaResponse = questionService.apply(question);

        log.debug("{}", triviaResponse);

        assertEquals("Trivia: New Question", triviaResponse.getTitle());
        assertFalse(triviaResponse.isTerminal());
        assertNotNull(triviaResponse.getText());
        assertNotNull(triviaResponse.getSpeech());
        assertTrue(triviaResponse.getText().contains("difficulty: medium, type: Multiple choice."));
        assertTrue(triviaResponse.getCorrectAnswer().getAnswerDescription().startsWith("Correct answer is "));

    }

}