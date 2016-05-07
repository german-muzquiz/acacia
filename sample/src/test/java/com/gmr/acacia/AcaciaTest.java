//package com.gmr.acacia;
//
//
//import android.content.ComponentName;
//import android.content.ServiceConnection;
//import android.os.Build;
//import com.gmr.acacia.impl.ServiceProxy;
//import com.gmr.acacia.sample.BuildConfig;
//import com.gmr.acacia.test.TestServiceImpl;
//import com.gmr.acacia.test.TestServiceOnCallerThread;
//import com.gmr.acacia.test.TestServiceOnWorkerThread;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricGradleTestRunner;
//import org.robolectric.RuntimeEnvironment;
//import org.robolectric.Shadows;
//import org.robolectric.annotation.Config;
//import org.robolectric.shadows.ShadowLooper;
//import rx.Observable;
//import rx.functions.Action1;
//
//import static org.junit.Assert.*;
//
//
//@RunWith(RobolectricGradleTestRunner.class)
//@Config(constants = BuildConfig.class)
//public class AcaciaTest
//{
//    private TestServiceImpl serviceImpl;
//    private AcaciaService androidService;
//    private Throwable error;
//
//    @Before
//    public void setUp()
//    {
//        error = null;
//        serviceImpl = new TestServiceImpl();
//        androidService = new AcaciaService();
//        androidService.setUserImpl(serviceImpl);
//
//        Shadows.shadowOf(RuntimeEnvironment.application).setComponentNameAndServiceForBindService(
//                new ComponentName(RuntimeEnvironment.application, AcaciaService.class),
//                androidService.onBind(null));
//
//        ShadowLooper.idleMainLooperConstantly(true);
//    }
//
//
//    @Test
//    public void testObservableOnWorkerThread()
//    {
//        serviceImpl.setTestObservable(Observable.just("Hello"));
//
//        TestServiceOnWorkerThread serviceInterface = Acacia.createService(
//                RuntimeEnvironment.application, TestServiceOnWorkerThread.class);
//
//        serviceInterface.getObservable()
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String o) {
//                        assertEquals("Hello", o);
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        error = throwable;
//                    }
//                });
//
//        assertNotNull(androidService.getServiceThread());
//        Shadows.shadowOf(androidService.getServiceThread().getHandler().getLooper()).idle();
//        assertNotErrors();
//    }
//
//    @Test
//    public void testObservableOnCallerThread()
//    {
//        serviceImpl.setTestObservable(Observable.just("Hello"));
//
//        TestServiceOnCallerThread serviceInterface = Acacia.createService(
//                RuntimeEnvironment.application, TestServiceOnCallerThread.class);
//
//        serviceInterface.getObservable()
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String o) {
//                        assertEquals("Hello", o);
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        error = throwable;
//                    }
//                });
//
//        assertNull(androidService.getServiceThread());
//        assertNotErrors();
//    }
//
//    @Test
//    public void testNullObservableOnWorkerThread()
//    {
//        serviceImpl.setTestObservable(null);
//
//        TestServiceOnWorkerThread serviceInterface = Acacia.createService(
//                RuntimeEnvironment.application, TestServiceOnWorkerThread.class);
//        assertNull(serviceInterface.getObservable());
//    }
//
//    @Test
//    public void testNullObservableOnCallerThread()
//    {
//        androidService.setUserImpl(serviceImpl);
//        serviceImpl.setTestObservable(null);
//
//        TestServiceOnCallerThread serviceInterface = Acacia.createService(
//                RuntimeEnvironment.application, TestServiceOnCallerThread.class);
//        assertNull(serviceInterface.getObservable());
//    }
//
//    @Test
//    public void testPrimitiveOnWorkerThread()
//    {
//        serviceImpl.setTestIntPrimitive(77);
//
//        TestServiceOnWorkerThread serviceInterface = Acacia.createService(
//                RuntimeEnvironment.application, TestServiceOnWorkerThread.class);
//
//        assertEquals(77, serviceInterface.getIntPrimitive());
//        assertNotNull(androidService.getServiceThread());
//    }
//
//    @Test
//    public void testPrimitiveOnCallerThread()
//    {
//        serviceImpl.setTestIntPrimitive(77);
//
//        TestServiceOnCallerThread serviceInterface = Acacia.createService(
//                RuntimeEnvironment.application, TestServiceOnCallerThread.class);
//
//        assertEquals(77, serviceInterface.getIntPrimitive());
//        assertNull(androidService.getServiceThread());
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void testStopService() throws InterruptedException
//    {
//        TestServiceOnWorkerThread serviceInterface = Acacia.createService(
//                RuntimeEnvironment.application, TestServiceOnWorkerThread.class);
//
//        Acacia.stopService(serviceInterface);
//
//        assertTrue(Shadows.shadowOf(androidService).isStoppedBySelf());
//
//        Class<? extends ServiceConnection> unboundConn = Shadows.shadowOf(RuntimeEnvironment.application).
//                getUnboundServiceConnections().get(0).getClass();
//        assertEquals(ServiceProxy.class, unboundConn);
//
//        serviceInterface.getIntPrimitive();
//    }
//
//
//
//    private void assertNotErrors()
//    {
//        if (error != null)
//        {
//            error.printStackTrace();
//            fail(error.getMessage());
//        }
//    }
//}
