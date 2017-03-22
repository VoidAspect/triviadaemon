package com.voidaspect.triviadaemon.service;

import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author mikhail.h
 */
public final class TriviaRequestContext {

    public enum ContextParam {
        QUESTION_SPEECH, QUESTION_TEXT, CORRECT_ANSWER, CORRECT_ANSWER_PLAIN
    }

    @Getter
    private final Map<ContextParam, String> contextParams = new EnumMap<>(ContextParam.class);

}
