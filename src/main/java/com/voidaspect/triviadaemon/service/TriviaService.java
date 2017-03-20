package com.voidaspect.triviadaemon.service;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mikhail.h
 */
public final class TriviaService {

    public TriviaIntent getIntentByName(String name) {
        return Arrays.stream(TriviaIntent.values())
                .filter(triviaIntent -> triviaIntent.names.contains(name))
                .findAny()
                .orElse(TriviaIntent.HELP);
    }

    enum TriviaIntent implements Function<TriviaRequest, TriviaResponse> {

        HELP("AMAZON.HelpIntent"),

        STOP(names("Amazon.StopIntent", "stop")),

        QUESTION("QuestionIntent");

        private final Set<String> names;

        TriviaIntent(String name) {
            this.names = Collections.singleton(name);
        }

        TriviaIntent(Set<String> names) {
            this.names = names;
        }

        @Override
        public TriviaResponse apply(TriviaRequest request) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        private static Set<String> names(String... names) {
            return Arrays.stream(names)
                    .distinct()
                    .collect(Collectors.toSet());
        }

    }
}
