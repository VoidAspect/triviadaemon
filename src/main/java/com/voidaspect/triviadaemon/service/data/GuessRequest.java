package com.voidaspect.triviadaemon.service.data;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.Set;

/**
 * @author mikhail.h
 */
@EqualsAndHashCode
@ToString
public final class GuessRequest {

    private final Set<String> userInput;

    public GuessRequest(Set<String> userInput) {
        this.userInput = Collections.unmodifiableSet(userInput);
    }

    public boolean matches(String s) {
        return userInput.stream()
                .anyMatch(input -> input.equalsIgnoreCase(s));
    }

}
