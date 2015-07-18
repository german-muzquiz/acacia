package com.gmr.acacia;

import android.content.Context;
import android.util.Log;

import com.gmr.acacia.impl.Constants;
import com.gmr.acacia.impl.ServiceControl;
import com.gmr.acacia.impl.ServiceProxy;

import java.lang.reflect.Method;


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
               {@literal android:theme="@style/AppTheme">}
               ...
               {@literal <service android:name="com.gmr.acacia.AcaciaService"/>}
               ...
          {@literal </application> }

       3. (Optional) Implement ServiceAware in your service implementation in order to receive
          the Android Service object and show/hide: a persistent notification:

          public class ServiceImpl implements MyService, ServiceAware{@literal <AcaciaService>} {
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
              {@literal android:theme="@style/AppTheme">}
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

    /**
     * Stop a previously created service. After this method is called, all further interactions with
     * the service object will throw {@link java.lang.IllegalStateException}.
     *
     * @param aService instance to be stopped.
     */
    public static void stopService(Object aService)
    {
        for (Class<?> myInterface : aService.getClass().getInterfaces())
        {
            Service serviceAnnotation = myInterface.getAnnotation(Service.class);
            if (serviceAnnotation != null)
            {
                try
                {
                    Method stopMethod = ServiceControl.class.getDeclaredMethod("stop");
                    stopMethod.invoke(aService);
                }
                catch (Exception anEx)
                {
                    Log.e(Constants.LOG_TAG, "Unable to stop service", anEx);
                }

                return;
            }
        }

        throw new AcaciaException("Given class " + aService.getClass().getName() +
                " is not a service interface (must be annotated " +
                "with " + Service.class.getName() + ")");
    }

}
