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

//    int category; //todo implement categories

    Difficulty difficulty;

    QuestionType type;

    @NonNull
    TriviaRequestContext requestContext;

}
