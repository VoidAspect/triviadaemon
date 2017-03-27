package com.voidaspect.triviadaemon.service;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;

/**
 * @author mikhail.h
 */
enum TriviaIntent {

    QUESTION("QuestionIntent", "question.request"),

    ANSWER("AnswerIntent"),

    GUESS("GuessIntent", "question.guess"),

    HELP("AMAZON.HelpIntent"),

    REPEAT("AMAZON.RepeatIntent"),

    STOP("AMAZON.StopIntent"),

    CANCEL("AMAZON.CancelIntent");

    @Getter
    private final Set<String> names;

    TriviaIntent(String name) {
        this.names = unmodifiableSet(singleton(name));
    }

    TriviaIntent(String... names) {
        this.names = unmodifiableSet(Arrays.stream(names).collect(Collectors.toSet()));
    }

    static TriviaIntent getByName(String name) {
        return Arrays.stream(values())
                .filter(triviaIntent -> triviaIntent.getNames().contains(name))
                .findAny()
                .orElse(TriviaIntent.HELP);
    }
}
