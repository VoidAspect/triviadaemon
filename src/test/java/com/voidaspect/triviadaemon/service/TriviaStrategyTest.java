package com.voidaspect.triviadaemon.service;

import org.junit.Test;

import static com.voidaspect.triviadaemon.service.TriviaStrategy.TriviaIntent.*;
import static org.junit.Assert.*;

/**
 * @author miwag.
 */
public class TriviaStrategyTest {

    private final TriviaStrategy triviaStrategy = new TriviaStrategy();

    @Test
    public void testGetIntentByName() throws Exception {
        assertIntentHasName(QUESTION, "QuestionIntent");
        assertIntentHasName(QUESTION, "question.request");

        assertIntentHasName(ANSWER, "AnswerIntent");
        assertIntentHasName(ANSWER, "question.answer");

        assertIntentHasName(REPEAT, "AMAZON.RepeatIntent");
        assertIntentHasName(REPEAT, "question.repeat");

        assertIntentHasName(GUESS, "GuessIntent");
        assertIntentHasName(GUESS, "question.guess");

        assertIntentHasName(HELP, "AMAZON.HelpIntent");
        assertIntentHasName(STOP, "AMAZON.StopIntent");
        assertIntentHasName(CANCEL, "AMAZON.CancelIntent");
    }

    private void assertIntentHasName(TriviaStrategy.TriviaIntent intent, String name) {
        assertSame("Intent doesn't support the given name: " + name,
                intent, triviaStrategy.getIntentByName(name));
    }

}