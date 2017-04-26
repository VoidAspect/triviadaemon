package com.voidaspect.triviadaemon.handler.ask;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.voidaspect.triviadaemon.dialog.ASKTitle;
import com.voidaspect.triviadaemon.dialog.Phrase;
import com.voidaspect.triviadaemon.service.*;
import com.voidaspect.triviadaemon.service.data.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.voidaspect.triviadaemon.service.data.TriviaRequestContext.ContextParam.*;

/**
 * This class represents a speech-enabled web service that runs on AWS Lambda.
 * <br>This {@link SpeechletV2} implementation enables user to request and answer questions
 * from opentdb.com
 *
 * @author mikhail.h
 */
@Slf4j
final class TriviaSpeechlet implements SpeechletV2 {

    /**
     * {@link SpeechletResponseFactory} instance.
     */
    private final SpeechletResponseFactory responseFactory;

    /**
     * {@link ServiceProducer} instance.
     */
    private final ServiceProducer intentService;

    TriviaSpeechlet(ServiceProducer intentService) {
        this.intentService = intentService;
        responseFactory = new SpeechletResponseFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}",
                requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        val request = requestEnvelope.getRequest();
        val session = requestEnvelope.getSession();

        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

        val intent = request.getIntent();

        val triviaRequest = createTriviaRequest(session, intent);

        log.debug("TriviaRequest: {}", triviaRequest);

        val response = intentService
                .getFunctionByIntentName(intent.getName())
                .apply(triviaRequest);

        return createSpeechletResponse(session, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}",
                requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
    }

    /**
     * Creates {@link TriviaRequest} objects from {@link Intent} and {@link Session} parameters.
     *
     * @param session dialog session with context parameters.
     * @param intent  user's intent with slots.
     * @return {@link TriviaRequest} value-object.
     */
    private TriviaRequest createTriviaRequest(Session session, Intent intent) {
        val requestContext = new TriviaRequestContext();
        val contextParams = requestContext.getContextParams();

        contextParams.put(CORRECT_ANSWER, (String) session.getAttribute(CORRECT_ANSWER.name()));
        contextParams.put(CORRECT_ANSWER_PLAIN, (String) session.getAttribute(CORRECT_ANSWER_PLAIN.name()));
        contextParams.put(QUESTION_SPEECH, (String) session.getAttribute(QUESTION_SPEECH.name()));
        contextParams.put(QUESTION_TEXT, (String) session.getAttribute(QUESTION_TEXT.name()));

        val questionRequestBuilder = QuestionRequest.builder();
        getSlotValue(intent, ASKSlot.DIFFICULTY)
                .flatMap(Difficulty::getByName)
                .ifPresent(questionRequestBuilder::difficulty);
        getSlotValue(intent, ASKSlot.TYPE)
                .flatMap(QuestionType::getByDescription)
                .ifPresent(questionRequestBuilder::type);

        Set<String> userInput = new HashSet<>();
        getSlotValue(intent, ASKSlot.BOOLEAN)
                .ifPresent(userInput::add);
        getSlotValue(intent, ASKSlot.NUMBER)
                .ifPresent(userInput::add);

        return TriviaRequest.builder()
                .requestContext(requestContext)
                .question(questionRequestBuilder.build())
                .guessRequest(new GuessRequest(userInput))
                .build();
    }

    /**
     * Creates {@link SpeechletResponse} objects from {@link TriviaResponse} and,
     * if needed, saves the recently generated question data into {@link Session}.
     *
     * @param session  dialog session
     * @param response {@link #intentService} response.
     * @return {@link SpeechletResponse} object.
     */
    private SpeechletResponse createSpeechletResponse(Session session, TriviaResponse response) {
        val text = response.getText();
        val speech = response.getSpeech();

        val title = response.getTitle();

        final SpeechletResponse speechletResponse;
        if (response.isTerminal()) {
            speechletResponse = responseFactory
                    .newTellResponse(speech, text, title);
        } else {
            val correctAnswer = response.getCorrectAnswer();
            if (correctAnswer != null) {
                session.setAttribute(CORRECT_ANSWER.name(), correctAnswer.getAnswerDescription());
                session.setAttribute(CORRECT_ANSWER_PLAIN.name(), correctAnswer.getAnswerPlain());
                session.setAttribute(QUESTION_SPEECH.name(), speech);
                session.setAttribute(QUESTION_TEXT.name(), text);
            }
            speechletResponse = responseFactory
                    .newAskResponse(speech, Phrase.REPROMPT.get(), text, title);
        }
        return speechletResponse;
    }

    /**
     * Utility method for null-safe retrieval of {@link Slot#getValue()}
     *
     * @param intent  intent from {@link IntentRequest}.
     * @param askSlot {@link ASKSlot} type.
     * @return optional of slot value
     */
    private Optional<String> getSlotValue(Intent intent, ASKSlot askSlot) {
        return Optional.ofNullable(intent.getSlot(askSlot.getSlotName()))
                .map(Slot::getValue);
    }

}
