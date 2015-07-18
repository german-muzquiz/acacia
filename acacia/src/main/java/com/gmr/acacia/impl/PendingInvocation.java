package com.gmr.acacia.impl;

import com.gmr.acacia.AcaciaService;


/**
 * Encapsulates a service method invocation that waits for the service connection to be established.
 */
public interface PendingInvocation extends Runnable
{

    void setService(AcaciaService service);

    Object returnOnDisconnectedInvocation();

}
