package com.gmr.acacia.test;

import rx.Observable;


public class TestServiceImpl implements TestServiceOnCallerThread, TestServiceOnWorkerThread
{
    private Observable<String> testObservable;
    private int testIntPrimitive;

    public void setTestObservable(Observable<String> testObservable)
    {
        this.testObservable = testObservable;
    }

    public void setTestIntPrimitive(int testIntPrimitive)
    {
        this.testIntPrimitive = testIntPrimitive;
    }

    @Override
    public Observable<String> getObservable()
    {
        return testObservable;
    }

    @Override
    public int getIntPrimitive()
    {
        return testIntPrimitive;
    }
}
