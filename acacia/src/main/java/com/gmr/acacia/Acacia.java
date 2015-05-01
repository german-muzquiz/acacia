package com.gmr.acacia;

import android.content.Context;

import com.gmr.acacia.impl.ServiceProxy;


/**
 * Creates local services that can execute operations on a worker thread.
 *
 * Usage:
 * <pre>
       1. Create a service instance:
          MyService myService = Acacia.createService(context, MyService.class);

          {@literal @Service(ServiceImpl.class)}
          public interface MyService {
            ...
          }

       2. Add to AndroidManifest.xml the service tag:

          {@literal <application }
               android:icon="@mipmap/ic_launcher"
               android:label="@string/app_name"
               android:theme="@style/AppTheme">
               ...
               {@literal <service android:name="com.gmr.acacia.AcaciaService"/>}
               ...
          {@literal </application> }

       3. (Optional) Implement ServiceAware in your service implementation in order to receive
          the Android Service object and show/hide: a persistent notification:

          public class ServiceImpl implements MyService, ServiceAware<AcaciaService> {
                ...
                {@literal @Override}
                public void setAndroidService(AcaciaService androidService) {
                    androidService.startForeground(id, notification);
                }
                ...
          }

       4. (Optional) Specify another Android Service class extending from AcaciaService:

          {@literal @Service(value=ServiceImpl.class, androidService=AndroidService.class)}
          public interface MyService {
             ...
          }

          public class AndroidService extends AcaciaService { nothing to do here }

          {@literal <application }
              android:icon="@mipmap/ic_launcher"
              android:label="@string/app_name"
              android:theme="@style/AppTheme">
              ...
              {@literal <service android:name=".AndroidService"/>}
              ...
          {@literal </application> }

 * </pre>
 * MyService is an interface defined by you, which only restriction is that it must be annotated
 * with {@link Service}. You also write the implementation of this
 * interface, which must have an empty no-arg constructor.
 */
public class Acacia
{

    /**
     * Creates an instance of a service backed by a client defined interface and implementation.
     *
     * @param aContext used to start the service.
     * @param aServiceInterface Any interface annotated with {@link Service}.
     * @param <T> interface type.
     * @return an instance of T.
     */
    public static <T> T createService(Context aContext, Class<T> aServiceInterface)
    {
        return ServiceProxy.newInstance(aContext, aServiceInterface);
    }

}
