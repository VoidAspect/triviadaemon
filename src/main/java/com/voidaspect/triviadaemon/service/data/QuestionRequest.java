package com.voidaspect.triviadaemon.service.data;

import com.voidaspect.triviadaemon.service.Difficulty;
import com.voidaspect.triviadaemon.service.QuestionType;
import lombok.Builder;
import lombok.Value;

/**
 * @author mikhail.h
 */
@Value
@Builder
public final class QuestionRequest {

    Difficulty difficulty;

    QuestionType type;

}
