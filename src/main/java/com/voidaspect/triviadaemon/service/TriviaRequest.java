package com.voidaspect.triviadaemon.service;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Set;

/**
 * @author mikhail.h
 */
@Value
@Builder
public final class TriviaRequest {

//    int category; //todo implement categories

    Difficulty difficulty;

    QuestionType type;

    Set<String> userInput;

    @NonNull
    TriviaRequestContext requestContext;

}
