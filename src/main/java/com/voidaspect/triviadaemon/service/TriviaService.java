package com.voidaspect.triviadaemon.service;

import com.voidaspect.triviadaemon.dialog.ASKTitle;
import com.voidaspect.triviadaemon.dialog.Phrase;
import com.voidaspect.triviadaemon.service.data.QuestionRequest;
import com.voidaspect.triviadaemon.service.data.TriviaRequest;
import com.voidaspect.triviadaemon.service.data.TriviaRequestContext;
import com.voidaspect.triviadaemon.service.data.TriviaResponse;
import lombok.Getter;
import lombok.val;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.voidaspect.triviadaemon.dialog.Phrase.*;
import static com.voidaspect.triviadaemon.service.TriviaService.TriviaIntent.*;
import static com.voidaspect.triviadaemon.service.data.TriviaRequestContext.ContextParam.*;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;

/**
 * @author mikhail.h
 */
public final class TriviaService {

    private final Map<TriviaIntent, Function<TriviaRequest, TriviaResponse>> intentMap;

    public TriviaService(Function<QuestionRequest, TriviaResponse> questionRequestFunction) {
        intentMap = new EnumMap<>(TriviaIntent.class);

        intentMap.put(QUESTION,
                questionRequestFunction.compose(TriviaRequest::getQuestion));

        intentMap.put(ANSWER, request -> TriviaResponse.builder()
                .isTerminal(false)
                .title(ASKTitle.CORRECT_ANSWER.get())
                .speech(getContextParam(request, CORRECT_ANSWER))
                .build());

        intentMap.put(GUESS, TriviaService::checkGuess);

        intentMap.put(HELP, request -> TriviaResponse.builder()
                .isTerminal(true)
                .title(ASKTitle.HELP.get())
                .speech(HELP_MESSAGE.get())
                .build());

        intentMap.put(REPEAT, request -> TriviaResponse.builder()
                .isTerminal(false)
                .speech(getContextParam(request, QUESTION_SPEECH))
                .text(getContextParam(request, QUESTION_TEXT))
                .title(ASKTitle.PREVIOUS_QUESTION.get())
                .build());

        intentMap.put(STOP, request -> TriviaResponse.builder()
                .isTerminal(true)
                .speech(GOODBYE.get())
                .title(ASKTitle.EXIT.get())
                .build());

        intentMap.put(CANCEL, intentMap.get(STOP));

    }

    public Function<TriviaRequest, TriviaResponse> getIntentByName(String name) {
        val intent = Arrays.stream(TriviaIntent.values())
                .filter(triviaIntent -> triviaIntent.names.contains(name))
                .findAny()
                .orElse(TriviaIntent.HELP);
        return intentMap.get(intent);
    }

    private static String
    getContextParam(TriviaRequest request, TriviaRequestContext.ContextParam cp) {
        val param = request.getRequestContext().getContextParams().get(cp);
        return Optional.ofNullable(param)
                .orElseGet(NO_QUESTION);
    }

    private static TriviaResponse checkGuess(TriviaRequest request) {
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
    }

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

    }

}
