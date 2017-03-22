package com.voidaspect.triviadaemon.service.data;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * @author mikhail.h
 */
@Value
@Builder
public final class TriviaRequest {

    QuestionRequest question;

    GuessRequest guessRequest;

    @NonNull
    TriviaRequestContext requestContext;

}
