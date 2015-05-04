package com.gmr.acacia.test;

import com.gmr.acacia.Service;

import rx.Observable;


@Service(value = TestServiceImpl.class, useWorkerThread = false)
public interface TestServiceOnCallerThread
{
    Observable<String> getObservable();
    int getIntPrimitive();
}
