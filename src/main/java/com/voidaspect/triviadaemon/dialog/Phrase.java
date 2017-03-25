package com.voidaspect.triviadaemon.dialog;

import java.util.Random;

/**
 * Represents a set of phrases used by Trivia Daemon device to interact with user.
 * <br>Covers questions, prompts, help messages etc.
 * Some phrases can have several synonymous snippets of which one is chosen randomly
 *
 * @author mikhail.h
 */
public enum Phrase {

    /**
     * Help message.
     */
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

    /**
     * Reprompt message for active session.
     * If Echo device is in listening mode and waits for interaction for some time,
     * this message will be spoken to user.
     */
    REPROMPT("What else can I help you with?"),

    /**
     * Phrase used if request to opentdb.com api failed.
     *
     * @see com.voidaspect.triviadaemon.service.QuestionService
     */
    SERVICE_ERROR("Sorry, it seems the Trivia service is unavailable at the moment."),

    /**
     * Phrase used when question is not yet held in conversation context.
     */
    NO_QUESTION("Sorry, I don't remember last question.", "Please, ask me to give you a question first."),

    /**
     * Phrase used to mark true-or-false questions.
     */
    TRUE_OR_FALSE("Is this true or false?"),

    /**
     * Correct answer response.
     */
    CORRECT_GUESS("Correct!", "Yes, that's correct.", "You are right.", "You've guessed it!"),

    /**
     * Wrong answer response.
     */
    INCORRECT_GUESS("Wrong!", "It's incorrect.", "Try once again.", "No, that's not correct."),

    /**
     * Goodbye message.
     */
    GOODBYE("Goodbye...", "Till next time!", "Thank you for using this skill.");

    /**
     * Synonymous text snippets appropriate for this phrase.
     */
    private final String[] textSnippets;

    /**
     * Constructor for {@link Phrase}.
     *
     * @param textSnippets {@link #textSnippets}
     */
    Phrase(String... textSnippets) {
        this.textSnippets = textSnippets;
    }

    /**
     * Gets random phrase from the {@link #textSnippets}
     *
     * @return phrase string
     */
    public String get() {
        return textSnippets[new Random().nextInt(textSnippets.length)];
    }
}
