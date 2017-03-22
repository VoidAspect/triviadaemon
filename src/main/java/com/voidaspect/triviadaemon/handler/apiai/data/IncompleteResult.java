package com.voidaspect.triviadaemon.handler.apiai.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author mikhail.h
 */
@Data
public class IncompleteResult implements Serializable {

    private String source;

    private String resolvedQuery;

    private Fulfillment fulfillment;

    private boolean actionIncomplete;

    private Map<String, String> parameters;

    private Set<RequestContext> contexts;

    private Map<String, String> metadata;

    private double score;

}
