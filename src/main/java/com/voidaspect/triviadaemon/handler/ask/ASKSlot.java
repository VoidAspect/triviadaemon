package com.voidaspect.triviadaemon.handler.ask;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author mikhail.h
 */
@RequiredArgsConstructor
@Getter
enum ASKSlot {

    DIFFICULTY("Difficulty"),

    TYPE("Type");

    private final String slotName;

}