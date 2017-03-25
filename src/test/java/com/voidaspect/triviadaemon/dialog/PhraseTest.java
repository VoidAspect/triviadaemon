package com.voidaspect.triviadaemon.dialog;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author miwag.
 */
@Slf4j
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
        log.debug("{}: {}", phrase, randomString);
    }
}