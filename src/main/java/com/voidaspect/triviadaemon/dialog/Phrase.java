package com.voidaspect.triviadaemon.dialog;

import lombok.RequiredArgsConstructor;

/**
 * @author mikhail.h
 */
@RequiredArgsConstructor
public enum Phrase {

    HELP_MESSAGE("You can ask me to generate a question. " +
            "For example, say: Give me an easy question with multiple choice."),

    /**
     * Reprompt message for the start of the session
     */
    WELCOME_PROMPT("For instructions on what you can say, please say help me."),

    /**
     * Welcome message at the start of the interaction.
     */
    WELCOME_MESSAGE("Welcome to Trivia Daemon! " + HELP_MESSAGE.get()),

    REPROMPT("What else can I help you with?"),

    SERVICE_ERROR("Sorry, it seems the Trivia service is unavailable at the moment."),

    NO_QUESTION("Sorry, I don't remember last question."),

    TRUE_OR_FALSE("Is this true or false?"),

    /**
     * Goodbye message.
     */
    GOODBYE("Goodbye...");

    private final String text;

    public String get() {
        return text;
    }
}
