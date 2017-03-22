package com.voidaspect.triviadaemon.service.data;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author mikhail.h
 */
@EqualsAndHashCode
@ToString
public final class GuessRequest implements Predicate<String> {

    private final Set<String> userInput;

    public GuessRequest(Set<String> userInput) {
        this.userInput = Collections.unmodifiableSet(userInput);
    }

    @Override
    public boolean test(String s) {
        return userInput.stream()
                .anyMatch(input -> input.equalsIgnoreCase(s));
    }

}
