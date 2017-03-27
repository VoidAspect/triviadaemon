package com.voidaspect.triviadaemon.service;

import com.voidaspect.triviadaemon.service.data.TriviaRequest;
import com.voidaspect.triviadaemon.service.data.TriviaResponse;

import java.util.function.Function;

/**
 * @author miwag.
 */
@FunctionalInterface
public interface IntentProcessingService {

    Function<TriviaRequest, TriviaResponse> getFunctionByIntentName(String intentName);

}
