package com.voidaspect.triviadaemon.handler.apiai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Parameters of api.ai webhook request which are essential for request processing.
 *
 * @author mikhail.h
 */
@Getter
@RequiredArgsConstructor
public enum ApiAiParam {

    /**
     * Difficulty of the question. Can be "easy", "medium" or "hard".
     *
     * @see com.voidaspect.triviadaemon.service.data.Difficulty
     */
    DIFFICULTY("difficulty"),

    /**
     * Type of the question. Can be "true or false" or "multiple choice".
     *
     * @see com.voidaspect.triviadaemon.service.data.QuestionType
     */
    TYPE("type"),

    /**
     * Number of the answer for multi-choice questions.
     * Can be any integer, but correct one is always in range of 1 to 4 (inclusive).
     */
    NUMBER("number"),

    /**
     * Answer for true-or-false question.
     * Can be "true" or "false".
     */
    BOOLEAN("boolean");

    /**
     * Name of the parameter.
     */
    private final String paramName;

}
