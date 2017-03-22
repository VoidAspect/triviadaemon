package com.voidaspect.triviadaemon.handler.apiai.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author mikhail.h
 */
@Data
public class Fulfillment implements Serializable {

    private String speech;

    private Set<Message> messages;

}
