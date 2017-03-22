package com.voidaspect.triviadaemon.dialog;

import lombok.RequiredArgsConstructor;

/**
 * @author mikhail.h
 */
@RequiredArgsConstructor
public enum ASKTitle {

    WELCOME("Welcome!"),

    HELP("Help"),

    NEW_QUESTION("Trivia: New Question"),

    CORRECT("Trivia: Correct Guess"),

    INCORRECT("Trivia: Incorrect Guess"),

    NO_QUESTION_FOUND("Trivia: no question"),

    CORRECT_ANSWER("Trivia: Correct Answer"),

    NO_RESPONSE("No response from Trivia"),

    PREVIOUS_QUESTION("Trivia: Previous Question"),

    EXIT("Exiting...");

    private final String text;

    public String get() {
        return text;
    }
}
