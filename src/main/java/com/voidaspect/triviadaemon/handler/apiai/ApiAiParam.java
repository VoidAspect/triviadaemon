package com.voidaspect.triviadaemon.handler.apiai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author mikhail.h
 */
@Getter
@RequiredArgsConstructor
public enum ApiAiParam {

    DIFFICULTY("difficulty"),

    TYPE("type"),

    NUMBER("number"),

    BOOLEAN("boolean");

    private final String paramName;

}
