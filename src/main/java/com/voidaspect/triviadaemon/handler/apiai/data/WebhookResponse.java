package com.voidaspect.triviadaemon.handler.apiai.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Bean for TriviaWebhook responses.
 *
 * @author mikhail.h
 */
@Data
public class WebhookResponse implements Serializable {

    private String speech;

    private String displayText;

    private Map<String, Object> data;

    private Set<RequestContext> contextOut;

    private String source;

}
