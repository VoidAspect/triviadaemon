package com.voidaspect.triviadaemon.service.data;

import lombok.Value;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author mikhail.h
 */
@Value
public final class TriviaRequestContext {

    public enum ContextParam {
        QUESTION_SPEECH, QUESTION_TEXT, CORRECT_ANSWER, CORRECT_ANSWER_PLAIN
    }

    Map<ContextParam, String> contextParams = new EnumMap<>(ContextParam.class);

}
