package com.voidaspect.triviadaemon.handler.ask;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.voidaspect.triviadaemon.dialog.ASKTitle;
import com.voidaspect.triviadaemon.dialog.Phrase;
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

        val triviaRequest = createTriviaRequest(session, intent);

        val response = getTriviaStrategy()
                .getIntentByName(intent.getName())
                .apply(triviaRequest);

        return createSpeechletResponse(session, response);
    }

    private SpeechletResponse createSpeechletResponse(Session session, TriviaResponse response) {
        val text = response.getText();
        val speech = response.getSpeech();

        if (response.isQuestion()) {
            session.setAttribute(CORRECT_ANSWER.name(), response.getCorrectAnswer());
            session.setAttribute(CORRECT_ANSWER_PLAIN.name(), response.getCorrectAnswerPlain());
            session.setAttribute(QUESTION_SPEECH.name(), speech);
            session.setAttribute(QUESTION_TEXT.name(), text);
        }

        val title = response.getTitle();

        final SpeechletResponse speechletResponse;
        if (response.isTerminal()) {
            speechletResponse = responseFactory
                    .newTellResponse(speech, text, title);
        } else {
            speechletResponse = responseFactory
                    .newAskResponse(speech, Phrase.REPROMPT.get(), text, title);
        }
        return speechletResponse;
    }

    private TriviaRequest createTriviaRequest(Session session, Intent intent) {
        val requestContext = new TriviaRequestContext();
        val contextParams = requestContext.getContextParams();

        contextParams.put(CORRECT_ANSWER, (String) session.getAttribute(CORRECT_ANSWER.name()));
        contextParams.put(CORRECT_ANSWER_PLAIN, (String) session.getAttribute(CORRECT_ANSWER_PLAIN.name()));
        contextParams.put(QUESTION_SPEECH, (String) session.getAttribute(QUESTION_SPEECH.name()));
        contextParams.put(QUESTION_TEXT, (String) session.getAttribute(QUESTION_TEXT.name()));

        val requestBuilder = TriviaRequest.builder()
                .requestContext(requestContext);

        getSlotValue(intent, ASKSlot.DIFFICULTY)
                .flatMap(Difficulty::getByName)
                .ifPresent(requestBuilder::difficulty);

        getSlotValue(intent, ASKSlot.TYPE)
                .flatMap(QuestionType::getByDescription)
                .ifPresent(requestBuilder::type);

        return requestBuilder.build();
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}",
                requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
    }

    private Optional<String> getSlotValue(Intent intent, ASKSlot askSlot) {
        return Optional.ofNullable(intent.getSlot(askSlot.getSlotName()))
                .map(Slot::getValue);
    }

}
