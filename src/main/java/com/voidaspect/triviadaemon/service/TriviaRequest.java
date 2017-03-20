package com.voidaspect.triviadaemon.service;

import lombok.Value;

/**
 * @author mikhail.h
 */
@Value
public final class TriviaRequest {

    int category;

    Difficulty difficulty;

    QuestionType type;

}
