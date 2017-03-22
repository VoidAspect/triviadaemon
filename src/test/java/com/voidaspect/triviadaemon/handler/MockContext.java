package com.voidaspect.triviadaemon.handler;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mikhail.h
 */
@Slf4j
public class MockContext implements Context {

    @Override
    public String getAwsRequestId() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getLogGroupName() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getLogStreamName() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getFunctionName() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getFunctionVersion() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getInvokedFunctionArn() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public CognitoIdentity getIdentity() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ClientContext getClientContext() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int getRemainingTimeInMillis() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMemoryLimitInMB() {
        return 512;
    }

    @Override
    public LambdaLogger getLogger() {
        return log::info;
    }
}
