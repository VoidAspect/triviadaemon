package com.voidaspect.triviadaemon.handler.apiai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author mikhail.h
 */
@Getter
@RequiredArgsConstructor
public enum ApiAiParam {

    DIFFICULTY("Difficulty"),

    TYPE("Type"),

    NUMBER("Number"),

    BOOLEAN("Boolean");

    private final String paramName;

}
