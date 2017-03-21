package com.voidaspect.triviadaemon.service;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

/**
 * @author mikhail.h
 */
@Value
@Builder
public final class TriviaResponse {

    @NonNull
    String title;

    String text;

    @NonNull
    String speech;

    String correctAnswer;

    boolean isTerminal;

    boolean isQuestion;

    public String getText() {
        return Optional.ofNullable(text).orElse(speech);
    }

}
