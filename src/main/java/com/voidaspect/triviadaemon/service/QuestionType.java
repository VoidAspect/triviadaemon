package com.voidaspect.triviadaemon.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author mikhail.h
 */
@RequiredArgsConstructor
@Getter
public enum QuestionType {

    MULTIPLE("multiple"),

    BOOLEAN("boolean");

    private final String name;

}
