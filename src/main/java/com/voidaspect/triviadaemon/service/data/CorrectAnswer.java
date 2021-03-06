package com.voidaspect.triviadaemon.service.data;

import lombok.NonNull;
import lombok.Value;

/**
 * @author mikhail.h
 */
@Value
public class CorrectAnswer {

    @NonNull
    String answerDescription;

    @NonNull
    String answerPlain;

}
