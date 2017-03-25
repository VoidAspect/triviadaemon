package com.voidaspect.triviadaemon.dialog;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author miwag.
 */
public class PhraseTest {

    @Test
    public void testRandomGet() throws Exception{
        Arrays.stream(Phrase.values())
                .parallel()
                .forEach(this::randomGetShouldBeNotEmptyAndNotNull);
    }

    private void randomGetShouldBeNotEmptyAndNotNull(Phrase phrase) {
        String randomString = phrase.get();
        assertNotNull(randomString);
        assertNotEquals("", randomString);
        System.out.println(randomString);
    }
}