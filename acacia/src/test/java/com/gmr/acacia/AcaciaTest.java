package com.gmr.acacia;


import android.content.ComponentName;
import android.content.ServiceConnection;

import com.gmr.acacia.impl.ServiceProxy;
import com.gmr.acacia.test.TestServiceImpl;
import com.gmr.acacia.test.TestServiceOnCallerThread;
import com.gmr.acacia.test.TestServiceOnWorkerThread;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.functions.Action1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.robolectric.Robolectric.shadowOf;


@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class AcaciaTest
{
    private TestServiceImpl serviceImpl;
    private AcaciaService androidService;
    private Throwable error;

    @Before
    public void setUp()
    {
        error = null;
        serviceImpl = new TestServiceImpl();
        androidService = new AcaciaService();
        androidService.setUserImpl(serviceImpl);

        shadowOf(Robolectric.application).setComponentNameAndServiceForBindService(
                new ComponentName(Robolectric.application, AcaciaService.class),
                androidService.onBind(null));

        Robolectric.idleMainLooperConstantly(true);
    }


    @Test
    public void testObservableOnWorkerThread()
    {
        serviceImpl.setTestObservable(Observable.just("Hello"));

        TestServiceOnWorkerThread serviceInterface = Acacia.createService(
                Robolectric.application, TestServiceOnWorkerThread.class);

        serviceInterface.getObservable()
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String o) {
                        assertEquals("Hello", o);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        error = throwable;
                    }
                });

        assertNotNull(androidService.getServiceThread());
        shadowOf(androidService.getServiceThread().getHandler().getLooper()).idle();
        assertNotErrors();
    }

    @Test
    public void testObservableOnCallerThread()
    {
        serviceImpl.setTestObservable(Observable.just("Hello"));

        TestServiceOnCallerThread serviceInterface = Acacia.createService(
                Robolectric.application, TestServiceOnCallerThread.class);

        serviceInterface.getObservable()
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String o) {
                        assertEquals("Hello", o);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        error = throwable;
                    }
                });

        assertNull(androidService.getServiceThread());
        assertNotErrors();
    }

    @Test
    public void testNullObservableOnWorkerThread()
    {
        serviceImpl.setTestObservable(null);

        TestServiceOnWorkerThread serviceInterface = Acacia.createService(
                Robolectric.application, TestServiceOnWorkerThread.class);
        assertNull(serviceInterface.getObservable());
    }

    @Test
    public void testNullObservableOnCallerThread()
    {
        androidService.setUserImpl(serviceImpl);
        serviceImpl.setTestObservable(null);

        TestServiceOnCallerThread serviceInterface = Acacia.createService(
                Robolectric.application, TestServiceOnCallerThread.class);
        assertNull(serviceInterface.getObservable());
    }

    @Test
    public void testPrimitiveOnWorkerThread()
    {
        serviceImpl.setTestIntPrimitive(77);

        TestServiceOnWorkerThread serviceInterface = Acacia.createService(
                Robolectric.application, TestServiceOnWorkerThread.class);

        assertEquals(77, serviceInterface.getIntPrimitive());
        assertNotNull(androidService.getServiceThread());
    }

    @Test
    public void testPrimitiveOnCallerThread()
    {
        serviceImpl.setTestIntPrimitive(77);

        TestServiceOnCallerThread serviceInterface = Acacia.createService(
                Robolectric.application, TestServiceOnCallerThread.class);

        assertEquals(77, serviceInterface.getIntPrimitive());
        assertNull(androidService.getServiceThread());
    }

    @Test(expected = IllegalStateException.class)
    public void testStopService() throws InterruptedException
    {
        TestServiceOnWorkerThread serviceInterface = Acacia.createService(
                Robolectric.application, TestServiceOnWorkerThread.class);

        Acacia.stopService(serviceInterface);

        assertTrue(shadowOf(androidService).isStoppedBySelf());

        Class<? extends ServiceConnection> unboundConn = shadowOf(Robolectric.application).
                getUnboundServiceConnections().get(0).getClass();
        assertEquals(ServiceProxy.class, unboundConn);

        serviceInterface.getIntPrimitive();
    }



    private void assertNotErrors()
    {
        if (error != null)
        {
            error.printStackTrace();
            fail(error.getMessage());
        }
    }
}
