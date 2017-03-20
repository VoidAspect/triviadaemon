package com.voidaspect.triviadaemon.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.omg.CORBA.UNKNOWN;

import java.util.Arrays;

/**
 * @author mikhail.h
 */
@RequiredArgsConstructor
@Getter
public enum QuestionType {

    MULTIPLE("multiple", "Multiple choice"),

    BOOLEAN("boolean", "True/False"),

    UNKNOWN("unknown", "Unknown");

    private final String name;

    private final String description;

    public static QuestionType getByName(String name) {
        return Arrays.stream(values())
                .filter(questionType -> questionType.name.equals(name))
                .findAny()
                .orElse(UNKNOWN);
    }

}
