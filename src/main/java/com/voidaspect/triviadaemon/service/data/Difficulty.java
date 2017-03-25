package com.voidaspect.triviadaemon.service.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

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

    public static Optional<Difficulty> getByName(String name) {
        return Arrays.stream(values())
                .filter(difficulty -> difficulty.name.equalsIgnoreCase(name))
                .findAny();
    }

}
