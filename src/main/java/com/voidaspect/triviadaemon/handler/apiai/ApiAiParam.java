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

    TYPE("Type");

    private final String paramName;

}
