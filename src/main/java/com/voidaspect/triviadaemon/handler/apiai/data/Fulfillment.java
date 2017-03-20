package com.voidaspect.triviadaemon.handler.apiai.data;

import lombok.Data;

import java.util.Set;

/**
 * @author mikhail.h
 */
@Data
public class Fulfillment {

    private String speech;

    private Set<Message> messages;

}
