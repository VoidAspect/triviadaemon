package com.voidaspect.triviadaemon.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author mikhail.h
 */
@RequiredArgsConstructor
@Getter
public enum ASKSlot {

    DIFFICULTY("Difficulty"),

    TYPE("Type");

    private final String slotName;

}
