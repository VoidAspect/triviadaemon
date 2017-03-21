package com.voidaspect.triviadaemon.handler.ask;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.voidaspect.triviadaemon.dialog.ASKTitle;
import com.voidaspect.triviadaemon.dialog.Phrase;
import lombok.val;

/**
 * Factory for speechlet responses (ASK response format).
 *
 * @author mikhail.h
 */
@SuppressWarnings("WeakerAccess")
final class SpeechletResponseFactory {

    /**
     * Method for creating the Ask response. The OutputSpeech and {@link Reprompt} objects are
     * created from the input strings.
     *
     * @param output    the output to be spoken
     * @param reprompt  the reprompt for if the user doesn't reply or is misunderstood.
     * @param cardTitle the title of the UI card.
     * @return SpeechletResponse the speechlet response
     */
    SpeechletResponse newAskResponse(Phrase output,
                                            Phrase reprompt,
                                            ASKTitle cardTitle) {
        return newAskResponse(output.get(), reprompt.get(), cardTitle);
    }

    /**
     * Method for creating the Ask response. The OutputSpeech and {@link Reprompt} objects are
     * created from the input strings.
     *
     * @param stringOutput the output to be spoken
     * @param repromptText the reprompt for if the user doesn't reply or is misunderstood.
     * @param cardTitle    the title of the UI card.
     * @return SpeechletResponse the speechlet response
     */
    SpeechletResponse newAskResponse(String stringOutput,
                                            String repromptText,
                                            ASKTitle cardTitle) {
        return newAskResponse(stringOutput, repromptText, stringOutput, cardTitle);
    }

    /**
     * Method for creating the Ask response. The OutputSpeech and {@link Reprompt} objects are
     * created from the input strings.
     *
     * @param stringOutput the output to be spoken
     * @param repromptText the reprompt for if the user doesn't reply or is misunderstood.
     * @param cardContent  the content of the UI card.
     * @param cardTitle    the title of the UI card.
     * @return SpeechletResponse the speechlet response
     */
    SpeechletResponse newAskResponse(String stringOutput,
                                            String repromptText,
                                            String cardContent,
                                            ASKTitle cardTitle) {
        return newAskResponse(stringOutput, repromptText, cardContent, cardTitle.get());
    }

    /**
     * Method for creating the Ask response. The OutputSpeech and {@link Reprompt} objects are
     * created from the input strings.
     *
     * @param stringOutput the output to be spoken
     * @param repromptText the reprompt for if the user doesn't reply or is misunderstood.
     * @param cardContent  the content of the UI card.
     * @param cardTitle    the title of the UI card.
     * @return SpeechletResponse the speechlet response
     */
    SpeechletResponse newAskResponse(String stringOutput,
                                            String repromptText,
                                            String cardContent,
                                            String cardTitle) {
        val outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(stringOutput);

        val repromptOutputSpeech = new PlainTextOutputSpeech();
        repromptOutputSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);

        val card = new SimpleCard();
        card.setTitle(cardTitle);
        card.setContent(cardContent);

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt, card);
    }

    /**
     * Method for creating the Tell response.
     * Returning such a response will stop the session.
     * The OutputSpeech and {@link Reprompt} objects are
     * created from the input strings.
     *
     * @param speech    the output to be spoken
     * @param cardTitle the title of the UI card.
     * @param text      the textual content of the UI card.
     * @return SpeechletResponse the speechlet response
     */
    SpeechletResponse newTellResponse(String speech,
                                             String text,
                                             String cardTitle) {
        val outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(speech);

        val card = new SimpleCard();
        card.setTitle(cardTitle);
        card.setContent(text);

        return SpeechletResponse.newTellResponse(outputSpeech, card);
    }
}
