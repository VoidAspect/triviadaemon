package com.voidaspect.triviadaemon.handler.apiai.data;

import lombok.Data;

import java.io.Serializable;

/**
 * Bean for TriviaWebhook requests.
 *
 * @author mikhail.h
 */
@Data
public class WebhookRequest implements Serializable {

    private String id;

    private String timestamp;

    private IncompleteResult result;

    private String sessionId;

}
