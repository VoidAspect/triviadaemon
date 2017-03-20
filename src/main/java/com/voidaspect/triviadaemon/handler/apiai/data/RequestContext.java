package com.voidaspect.triviadaemon.handler.apiai.data;

import lombok.Data;

import java.util.Map;

/**
 * @author mikhail.h
 */
@Data
public class RequestContext {

    private String name;

    private Map<String, String> parameters;

    int lifespan;

}
