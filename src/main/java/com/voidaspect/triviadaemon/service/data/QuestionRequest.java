package com.voidaspect.triviadaemon.service.data;

import lombok.Builder;
import lombok.Value;

/**
 * @author mikhail.h
 */
@Value
@Builder
public class QuestionRequest {

    Difficulty difficulty;

    QuestionType type;

}
