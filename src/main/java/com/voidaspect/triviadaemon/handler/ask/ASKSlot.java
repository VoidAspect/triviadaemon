package com.voidaspect.triviadaemon.handler.ask;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents conversational parameters (Slots).
 * Should be consistent with slot names in intent schema in Amazon developer console.
 *
 * @author mikhail.h
 */
@RequiredArgsConstructor
@Getter
enum ASKSlot {

    /**
     * Difficulty of the question. Can be "easy", "medium" or "hard".
     *
     * @see com.voidaspect.triviadaemon.service.data.Difficulty
     */
    DIFFICULTY("Difficulty"),

    /**
     * Type of the question. Can be "true or false" or "multiple choice".
     */
    TYPE("Type"),

    /**
     * Number of the answer for multi-choice questions.
     * Can be any integer, but correct one is always 1 to 4.
     */
    NUMBER("Number"),

    /**
     * Answer for true-or-false question.
     * Can be "true" or "false".
     */
    BOOLEAN("Boolean");

    /**
     * Name of the slot from Interaction Schema.
     */
    private final String slotName;

}
