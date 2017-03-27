package com.voidaspect.triviadaemon.service;

import org.junit.Test;

import static com.voidaspect.triviadaemon.service.TriviaService.TriviaIntent.*;
import static org.junit.Assert.*;

/**
 * @author miwag.
 */
public class TriviaServiceTest {

//    private final TriviaService triviaService = new TriviaService(new QuestionService());

    @Test
    public void testGetIntentByName() throws Exception {
        assertIntentHasName(QUESTION, "QuestionIntent");
        assertIntentHasName(QUESTION, "question.request");

        assertIntentHasName(GUESS, "GuessIntent");
        assertIntentHasName(GUESS, "question.guess");

        assertIntentHasName(ANSWER, "AnswerIntent");
        assertIntentHasName(REPEAT, "AMAZON.RepeatIntent");
        assertIntentHasName(HELP, "AMAZON.HelpIntent");
        assertIntentHasName(STOP, "AMAZON.StopIntent");
        assertIntentHasName(CANCEL, "AMAZON.CancelIntent");
    }

    private void assertIntentHasName(TriviaService.TriviaIntent intent, String name) {
        assertTrue("Intent doesn't support the given name: " + name,
                intent.getNames().contains(name));
    }

}