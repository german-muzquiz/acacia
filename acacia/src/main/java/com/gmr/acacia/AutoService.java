package com.gmr.acacia;

import android.content.Context;

import com.gmr.acacia.impl.ServiceProxy;


/**
 * Creates local services that execute operations on a worker thread.
 *
 * Usage:
 * <pre>
 *     1. Add to AndroidManifest.xml the service tag:
 *
 *         {@literal <application }
                 android:icon="@mipmap/ic_launcher"
                 android:label="@string/app_name"
                 android:theme="@style/AppTheme">
                 ...
                 {@literal <service android:name="com.gmr.autoservice.Service"/>}
                 ...
           {@literal </application> }

       2. Create a service instance:
          MyService myService = AutoService.createService(context, MyService.class);

       3. (Optional) Implement ServiceAware in your service implementation in order to receive a
          ServiceControl object and show/hide: a persistent notification:

          public class ServiceImpl implements MyService, ServiceAware {
                ...
                {@literal @Override}
                public void onServiceConnected(ServiceControl serviceControl) {
                    serviceControl.startForeground(id, notification);
                }
                ...
          }

 * </pre>
 * MyService is an interface defined by you, which only restriction is that it must be annotated
 * with {@link com.gmr.acacia.annotations.Service}. You also write the implementation of this
 * interface, which must have an empty no-arg constructor.
 */
public class AutoService
{

    /**
     * Creates an instance of a service backed by a client defined interface and implementation.
     *
     * @param aContext used to start the service. This context is then passed to your implementation
     *                 by {@link com.gmr.acacia.ServiceAware#setContext(android.content.Context)}.
     * @param aServiceInterface Any interface annotated with {@link com.gmr.acacia.annotations.Service}.
     * @param <T> interface type.
     * @return an instance of T.
     */
    public static <T> T createService(Context aContext, Class<T> aServiceInterface)
    {
        return ServiceProxy.newInstance(aContext, aServiceInterface);
    }

}
