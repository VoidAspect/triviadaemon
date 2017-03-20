package com.voidaspect.triviadaemon.service;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;

/**
 * @author mikhail.h
 */
public final class TriviaStrategy {

    public TriviaIntent getIntentByName(String name) {
        return Arrays.stream(TriviaIntent.values())
                .filter(triviaIntent -> triviaIntent.names.contains(name))
                .findAny()
                .orElse(TriviaIntent.HELP);
    }

    public enum TriviaIntent implements Function<TriviaRequest, TriviaResponse> {

        HELP("AMAZON.HelpIntent", request -> null),

        STOP(names("AMAZON.StopIntent", "stop"), request -> null),

        CANCEL("AMAZON.CancelIntent", STOP.function),

        QUESTION(names("QuestionIntent", "question.request"), request -> null);

        private final Set<String> names;

        private final Function<TriviaRequest, TriviaResponse> function;

        TriviaIntent(String name, Function<TriviaRequest, TriviaResponse> function) {
            this.names = unmodifiableSet(singleton(name));
            this.function = function;
        }

        TriviaIntent(Set<String> names, Function<TriviaRequest, TriviaResponse> function) {
            this.names = names;
            this.function = function;
        }

        @Override
        public TriviaResponse apply(TriviaRequest request) {
            return function.apply(request);
        }

        private static Set<String> names(String... names) {
            return unmodifiableSet(Arrays.stream(names)
                    .distinct()
                    .collect(Collectors.toSet()));
        }

    }
}
