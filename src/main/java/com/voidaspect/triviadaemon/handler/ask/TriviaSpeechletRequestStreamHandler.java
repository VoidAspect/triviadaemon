package com.voidaspect.triviadaemon.handler.ask;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mikhail.h
 */
@Slf4j
public final class TriviaSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    /**
     * Immutable set of Application IDs supported by Dockee skill.
     */
    public static final Set<String> SUPPORTED_APPLICATION_IDS;

    /**
     * Path to .txt file containing supported application ids.
     */
    private static final String SUPPORTED_IDS_CONFIG_FILE = "/app-ids.txt";

    /* Initialize SUPPORTED_APPLICATION_IDS */
    static {
        val url = TriviaSpeechletRequestStreamHandler.class.getResource(SUPPORTED_IDS_CONFIG_FILE);
        try (val lines = Files.lines(Paths.get(url.toURI()))) { //ids are line-separated
            SUPPORTED_APPLICATION_IDS = Collections.unmodifiableSet(lines
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toSet()));
        } catch (IOException | URISyntaxException e) {
            log.error("Cannot load app-ids.txt", e);
            throw new RuntimeException("Jar is corrupted: cannot load app-ids.txt", e);
        }
    }

    public TriviaSpeechletRequestStreamHandler() {
        super(new TriviaSpeechlet(), SUPPORTED_APPLICATION_IDS);
    }

}
