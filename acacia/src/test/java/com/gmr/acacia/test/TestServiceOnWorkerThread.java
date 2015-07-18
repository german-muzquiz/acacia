package com.gmr.acacia.test;

import com.gmr.acacia.Service;

import rx.Observable;


@Service(value = TestServiceImpl.class)
public interface TestServiceOnWorkerThread
{
    Observable<String> getObservable();
    int getIntPrimitive();
}
