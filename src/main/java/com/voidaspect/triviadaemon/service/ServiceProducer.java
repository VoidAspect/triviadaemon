package com.voidaspect.triviadaemon.service;

import com.voidaspect.triviadaemon.service.data.TriviaRequest;
import com.voidaspect.triviadaemon.service.data.TriviaResponse;

import java.util.function.Function;

/**
 * @author mikhail.h.
 */
@FunctionalInterface
public interface ServiceProducer {

    Function<TriviaRequest, TriviaResponse> getFunctionByIntentName(String intentName);

}
