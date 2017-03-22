package com.voidaspect.triviadaemon.handler.apiai.data;

import lombok.Data;

import java.io.Serializable;

/**
 * @author mikhail.h
 */
@Data
public class Message implements Serializable {

    private int type;

    private String speech;

}
