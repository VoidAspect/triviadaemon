package com.voidaspect.triviadaemon.handler.apiai.data;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * @author mikhail.h
 */
@Data
public class WebhookResponse {

    private String speech;

    private String displayText;

    private Map<String, Object> data;

    private Set<RequestContext> contextOut;

    private String source;

}
