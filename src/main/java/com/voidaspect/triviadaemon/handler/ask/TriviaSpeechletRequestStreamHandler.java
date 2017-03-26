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
 * This class implements the handler required for hosting the service as an AWS Lambda function.
 * <p>When configuring your Lambda function in the AWS Lambda console, specify this class as the
 * <strong>Handler</strong>.
 *
 * @author mikhail.h
 */
@SuppressWarnings("WeakerAccess")
@Slf4j
public final class TriviaSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    /**
     * Immutable set of Application IDs supported by Trivia Demon skill.
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
            throw new ExceptionInInitializerError(e);
        }
    }

    public TriviaSpeechletRequestStreamHandler() {
        super(new TriviaSpeechlet(), SUPPORTED_APPLICATION_IDS);
    }

}
