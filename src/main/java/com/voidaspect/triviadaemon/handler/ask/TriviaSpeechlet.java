package com.voidaspect.triviadaemon.handler.ask;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.voidaspect.triviadaemon.dialog.ASKTitle;
import com.voidaspect.triviadaemon.dialog.Phrase;
import com.voidaspect.triviadaemon.handler.ASKSlot;
import com.voidaspect.triviadaemon.service.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Optional;

import static com.voidaspect.triviadaemon.service.TriviaRequestContext.ContextParam.*;

/**
 * @author mikhail.h
 */
@Slf4j
final class TriviaSpeechlet implements SpeechletV2 {

    private final SpeechletResponseFactory responseFactory =
            new SpeechletResponseFactory();

    @Getter(value = AccessLevel.PRIVATE, lazy = true)
    private final TriviaStrategy triviaStrategy = new TriviaStrategy();

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}",
                requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        log.info("onLaunch requestId={}, sessionId={}",
                requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        return responseFactory.newAskResponse(
                Phrase.WELCOME_MESSAGE,
                Phrase.WELCOME_PROMPT,
                ASKTitle.WELCOME);
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        val request = requestEnvelope.getRequest();
        val session = requestEnvelope.getSession();

        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

        val intent = request.getIntent();

        val requestBuilder = TriviaRequest.builder()
                .requestContext(createRequestContext(session));

        getSlotValue(intent, ASKSlot.DIFFICULTY)
                .flatMap(Difficulty::getByName)
                .ifPresent(requestBuilder::difficulty);

        getSlotValue(intent, ASKSlot.TYPE)
                .flatMap(QuestionType::getByDescription)
                .ifPresent(requestBuilder::type);

        val triviaRequest = requestBuilder.build();

        val strategy = getTriviaStrategy(); //lazily get the strategy.

        val response = strategy.getIntentByName(intent.getName())
                .apply(triviaRequest);

        saveResponseInSession(session, response);

        val title = response.getTitle();
        val text = response.getText();
        val speech = response.getSpeech();

        final SpeechletResponse speechletResponse;
        if (response.isFinal()) {
            speechletResponse = responseFactory.newTellResponse(speech, text, title);
        } else {
            speechletResponse = responseFactory.newAskResponse(
                    speech,
                    Phrase.REPROMPT.get(),
                    text,
                    title);
        }

        return speechletResponse;
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}",
                requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
    }

    private TriviaRequestContext createRequestContext(Session session) {
        val requestContext = new TriviaRequestContext();
        val contextParams = requestContext.getContextParams();

        contextParams.put(CORRECT_ANSWER, (String) session.getAttribute(CORRECT_ANSWER.name()));
        contextParams.put(QUESTION_SPEECH, (String) session.getAttribute(QUESTION_SPEECH.name()));
        contextParams.put(QUESTION_TEXT, (String) session.getAttribute(QUESTION_TEXT.name()));

        return requestContext;
    }

    private void saveResponseInSession(Session session, TriviaResponse response) {
        session.setAttribute(CORRECT_ANSWER.name(), response.getCorrectAnswer());
        session.setAttribute(QUESTION_SPEECH.name(), response.getSpeech());
        session.setAttribute(QUESTION_TEXT.name(), response.getText());
    }

    private Optional<String> getSlotValue(Intent intent, ASKSlot askSlot) {
        return Optional.ofNullable(intent.getSlot(askSlot.getSlotName()))
                .map(Slot::getValue);
    }

}
