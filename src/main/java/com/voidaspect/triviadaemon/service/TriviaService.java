package com.voidaspect.triviadaemon.service;

import com.voidaspect.triviadaemon.dialog.ASKTitle;
import com.voidaspect.triviadaemon.dialog.Phrase;
import com.voidaspect.triviadaemon.service.data.QuestionRequest;
import com.voidaspect.triviadaemon.service.data.TriviaRequest;
import com.voidaspect.triviadaemon.service.data.TriviaRequestContext;
import com.voidaspect.triviadaemon.service.data.TriviaResponse;
import lombok.val;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.voidaspect.triviadaemon.dialog.Phrase.*;
import static com.voidaspect.triviadaemon.service.TriviaIntent.*;
import static com.voidaspect.triviadaemon.service.data.TriviaRequestContext.ContextParam.*;

/**
 * @author mikhail.h
 */
public final class TriviaService implements ServiceProducer {

    private final Map<TriviaIntent, Function<TriviaRequest, TriviaResponse>> intentMap;

    public TriviaService(Function<QuestionRequest, TriviaResponse> questionRequestFunction) {
        intentMap = new EnumMap<>(TriviaIntent.class);

        intentMap.put(QUESTION,
                questionRequestFunction.compose(TriviaRequest::getQuestion));

        intentMap.put(ANSWER, request -> TriviaResponse.builder()
                .isTerminal(false)
                .title(ASKTitle.CORRECT_ANSWER.get())
                .speech(getContextParam(request, CORRECT_ANSWER)
                        .orElseGet(NO_QUESTION))
                .build());

        intentMap.put(GUESS, TriviaService::checkGuess);

        intentMap.put(HELP, request -> TriviaResponse.builder()
                .isTerminal(false)
                .title(ASKTitle.HELP.get())
                .speech(HELP_MESSAGE.get())
                .build());

        intentMap.put(REPEAT, request -> TriviaResponse.builder()
                .isTerminal(false)
                .speech(getContextParam(request, QUESTION_SPEECH)
                        .orElseGet(NO_QUESTION))
                .text(getContextParam(request, QUESTION_TEXT)
                        .orElseGet(NO_QUESTION))
                .title(ASKTitle.PREVIOUS_QUESTION.get())
                .build());

        intentMap.put(STOP, request -> TriviaResponse.builder()
                .isTerminal(true)
                .speech(GOODBYE.get())
                .title(ASKTitle.EXIT.get())
                .build());

        intentMap.put(CANCEL, intentMap.get(STOP));

    }

    @Override
    public Function<TriviaRequest, TriviaResponse> getFunctionByIntentName(String intentName) {
        val intent = getByName(intentName);
        return intentMap.get(intent);
    }

    private static Optional<String>
    getContextParam(TriviaRequest request, TriviaRequestContext.ContextParam cp) {
        val param = request.getRequestContext().getContextParams().get(cp);
        return Optional.ofNullable(param);
    }

    private static TriviaResponse checkGuess(TriviaRequest request) {
        val correctAnswer = request.getRequestContext()
                .getContextParams()
                .get(CORRECT_ANSWER_PLAIN);
        final ASKTitle title;
        final Phrase speech;
        final boolean isCorrect;
        if (correctAnswer == null || correctAnswer.isEmpty()) { //if request doesn't supply context
            title = ASKTitle.NO_QUESTION_FOUND;
            speech = NO_QUESTION;
            isCorrect = false;
        } else if (request.getGuessRequest().matches(correctAnswer)) { //if user's input matches answer from context
            title = ASKTitle.CORRECT;
            speech = CORRECT_GUESS;
            isCorrect = true;
        } else { //if user input doesn't match answer from context
            title = ASKTitle.INCORRECT;
            speech = INCORRECT_GUESS;
            isCorrect = false;
        }
        return TriviaResponse.builder()
                .isTerminal(isCorrect)
                .title(title.get())
                .speech(speech.get())
                .build();
    }

}
