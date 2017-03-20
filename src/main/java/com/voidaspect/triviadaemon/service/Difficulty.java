package com.voidaspect.triviadaemon.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author mikhail.h
 */
@RequiredArgsConstructor
@Getter
public enum Difficulty {

    EASY("easy"),

    MEDIUM("medium"),

    HARD("hard");

    private final String name;

}
