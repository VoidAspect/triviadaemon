package com.voidaspect.triviadaemon.service;

import com.voidaspect.triviadaemon.dialog.ASKTitle;
import lombok.val;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.voidaspect.triviadaemon.dialog.Phrase.*;
import static com.voidaspect.triviadaemon.service.TriviaRequestContext.ContextParam.*;
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

        QUESTION(names("QuestionIntent", "question.request"), new QuestionService()),

        ANSWER(names("AnswerIntent", "question.answer"), request ->
                TriviaResponse.builder()
                        .isTerminal(false)
                        .isQuestion(false)
                        .title(ASKTitle.CORRECT_ANSWER.get())
                        .speech(getContextParam(request, CORRECT_ANSWER))
                        .build()),

        HELP("AMAZON.HelpIntent", request ->
                TriviaResponse.builder()
                        .isTerminal(true)
                        .isQuestion(false)
                        .title(ASKTitle.HELP.get())
                        .speech(HELP_MESSAGE.get())
                        .build()),

        REPEAT(names("AMAZON.RepeatIntent", "question.repeat"), request ->
                TriviaResponse.builder()
                        .isTerminal(false)
                        .isQuestion(false)
                        .speech(getContextParam(request, QUESTION_SPEECH))
                        .text(getContextParam(request, QUESTION_TEXT))
                        .title(ASKTitle.PREVIOUS_QUESTION.get())
                        .build()),

        STOP("AMAZON.StopIntent", request ->
                TriviaResponse.builder()
                        .isTerminal(true)
                        .isQuestion(false)
                        .speech(GOODBYE.get())
                        .title(ASKTitle.EXIT.get())
                        .build()),

        CANCEL("AMAZON.CancelIntent", STOP.function);

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

        private static String
        getContextParam(TriviaRequest request, TriviaRequestContext.ContextParam cp) {
            val param = request.getRequestContext().getContextParams().get(cp);
            return Optional.ofNullable(param)
                    .orElse(NO_QUESTION.get());
        }

    }
}
