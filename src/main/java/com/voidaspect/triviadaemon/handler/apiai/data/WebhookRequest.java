package com.voidaspect.triviadaemon.handler.apiai.data;

import lombok.Data;

/**
 * @author mikhail.h
 */
@Data
public class WebhookRequest {

    private String id;

    private String timestamp;

    private IncompleteResult result;

    private String sessionId;

}
