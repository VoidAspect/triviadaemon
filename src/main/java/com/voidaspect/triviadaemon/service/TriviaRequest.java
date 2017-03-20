package com.voidaspect.triviadaemon.service;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * @author mikhail.h
 */
@Value
@Builder
public final class TriviaRequest {

    int category;

    Difficulty difficulty;

    QuestionType type;

    @NonNull
    TriviaRequestContext requestContext;

}
