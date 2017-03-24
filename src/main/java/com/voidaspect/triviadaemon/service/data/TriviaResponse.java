package com.voidaspect.triviadaemon.service.data;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

/**
 * @author mikhail.h
 */
@Value
@Builder
public class TriviaResponse {

    @NonNull
    String title;

    String text;

    @NonNull
    String speech;

    CorrectAnswer correctAnswer;

    boolean isTerminal;

    public String getText() {
        return Optional.ofNullable(text).orElse(speech);
    }

}
