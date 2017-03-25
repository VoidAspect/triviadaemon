package com.voidaspect.triviadaemon.service;

import com.voidaspect.triviadaemon.dialog.ASKTitle;
import com.voidaspect.triviadaemon.dialog.Phrase;
import com.voidaspect.triviadaemon.service.data.TriviaRequest;
import com.voidaspect.triviadaemon.service.data.TriviaRequestContext;
import com.voidaspect.triviadaemon.service.data.TriviaResponse;
import lombok.val;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.voidaspect.triviadaemon.dialog.Phrase.*;
import static com.voidaspect.triviadaemon.service.data.TriviaRequestContext.ContextParam.*;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;

/**
 * @author mikhail.h
 */
public final class TriviaStrategy {

    public Function<TriviaRequest, TriviaResponse> getIntentByName(String name) {
        return Arrays.stream(TriviaIntent.values())
                .filter(triviaIntent -> triviaIntent.names.contains(name))
                .findAny()
                .orElse(TriviaIntent.HELP);
    }

    public enum TriviaIntent implements Function<TriviaRequest, TriviaResponse> {

        QUESTION(names("QuestionIntent", "question.request"),
                new QuestionService().compose(TriviaRequest::getQuestion)),

        ANSWER("AnswerIntent", request -> TriviaResponse.builder()
                .isTerminal(false)
                .title(ASKTitle.CORRECT_ANSWER.get())
                .speech(getContextParam(request, CORRECT_ANSWER))
                .build()),

        GUESS(names("GuessIntent", "question.guess"), request -> {
            val userGuess = request.getGuessRequest();
            val correctAnswer = request.getRequestContext()
                    .getContextParams()
                    .get(CORRECT_ANSWER_PLAIN);

            final ASKTitle title;
            final Phrase speech;
            if (correctAnswer == null || correctAnswer.isEmpty()) {
                title = ASKTitle.NO_QUESTION_FOUND;
                speech = NO_QUESTION;
            } else if (userGuess.test(correctAnswer)) {
                title = ASKTitle.CORRECT;
                speech = CORRECT_GUESS;
            } else {
                title = ASKTitle.INCORRECT;
                speech = INCORRECT_GUESS;
            }
            return TriviaResponse.builder()
                    .isTerminal(false)
                    .title(title.get())
                    .speech(speech.get())
                    .build();
        }),

        HELP("AMAZON.HelpIntent", request -> TriviaResponse.builder()
                .isTerminal(true)
                .title(ASKTitle.HELP.get())
                .speech(HELP_MESSAGE.get())
                .build()),

        REPEAT("AMAZON.RepeatIntent", request -> TriviaResponse.builder()
                .isTerminal(false)
                .speech(getContextParam(request, QUESTION_SPEECH))
                .text(getContextParam(request, QUESTION_TEXT))
                .title(ASKTitle.PREVIOUS_QUESTION.get())
                .build()),

        STOP("AMAZON.StopIntent", request -> TriviaResponse.builder()
                .isTerminal(true)
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
