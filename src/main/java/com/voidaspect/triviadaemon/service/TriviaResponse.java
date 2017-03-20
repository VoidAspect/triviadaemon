package com.voidaspect.triviadaemon.service;

import lombok.NonNull;
import lombok.Value;

import java.util.Optional;

/**
 * @author mikhail.h
 */
@Value
public final class TriviaResponse {

    @NonNull
    String title;

    String text;

    @NonNull
    String speech;

    public String getText() {
        return Optional.ofNullable(text).orElse(speech);
    }

}
