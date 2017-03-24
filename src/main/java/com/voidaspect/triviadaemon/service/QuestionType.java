package com.voidaspect.triviadaemon.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author mikhail.h
 */
@RequiredArgsConstructor
@Getter
public enum QuestionType {

    MULTIPLE("multiple", "Multiple choice"),

    BOOLEAN("boolean", "True or False");

    private final String name;

    private final String description;

    public static Optional<QuestionType> getByName(String name) {
        return Arrays.stream(values())
                .filter(questionType -> questionType.name.equals(name))
                .findAny();
    }

    public static Optional<QuestionType> getByDescription(String description) {
        return Arrays.stream(values())
                .filter(questionType -> questionType.description.equalsIgnoreCase(description))
                .findAny();
    }

}
