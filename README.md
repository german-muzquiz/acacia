#Acacia
Acacia lets you use Android Services as Plain Old Java Objects (POJO's) by defining an interface and
its implementing class. All the wiring around bound services, service connection and threading is
automatically handled. Also having the service implementation and the actual Android Service instance
as separate classes, allows them to be easily tested.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Acacia-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1837)

##Installation

Download the jar file and add it to your libs folder:

[ ![Download](https://api.bintray.com/packages/germnix/maven/Acacia/images/download.svg) ](https://bintray.com/germnix/maven/Acacia/_latestVersion)

Or add the dependency to build.gradle:

        compile 'com.gmr:acacia:0.1.1'

Or pom.xml:

        <dependency>
            <groupId>com.gmr</groupId>
            <artifactId>acacia</artifactId>
            <version>0.1.1</version>
            <type>jar</type>
        </dependency>

Acacia doesn't have runtime dependencies other than Android itself and the project it is being used on.

##Simple use
1. Define an interface annotated with `@Service`:

        @Service(ServiceImpl.class)
        public interface MyService {

            void doProcessing(Foo aComplexParam);

            Observable<ServiceState> getState();

            Bar getBar();

        }

    Interface methods may take and return non primitive arguments, just as any plain old java object.

2. Implement your interface:

        public class ServiceImpl implements MyService {
            // your implementation
        }

3. Add `<service>` tag to AndroidManifest.xml:

        <application
            android:icon="@mipmap/ic_launcher"
            android:label="@string/app_name"
            android:theme="@style/AppTheme">
            ...
            <service android:name="com.gmr.acacia.AcaciaService"/>
            ...
        </application>

4. Use the service:

        MyService service = Acacia.createService(context, MyService.class);
        service.doProcessing(foo);

##Getting the Android Service instance
To get the instance of the running Android Service, just implement `ServiceAware` interface:

        public class ServiceImpl implements MyService, ServiceAware<AcaciaService> {
            ...
            @Override
            public void setAndroidService(AcaciaService androidService) {
                androidService.startForeground(id, notification);
            }
            ...
        }
With this you can get the context, start/stop the service from foreground, etc.

##Multiple services
Want more than one service, or have control over the service name published in AndroidManifest.xml?
Easy:

        @Service(value=ServiceImpl.class, androidService=AndroidService.class)
        public interface MyService { ... }

        public class AndroidService extends AcaciaService {
            /* Nothing to do here. */
        }

        <application
            android:icon="@mipmap/ic_launcher"
            android:label="@string/app_name"
            android:theme="@style/AppTheme">
            ...
            <service android:name=".AndroidService"/>
            ...
        </application>

`AndroidService` is a class extending from AcaciaService, it can be empty because all the work is done
either by AcaciaService or delegated to your service implementation. If you are implementing
`ServiceAware` you will be passed an instance of this class.

##Multithreading
By default all `void` method invocations are executed on a background worker thread in sequence.
If the method returns a `rx.Observable`, subscriptions are automatically executed on this worker thread.
If the method return type is not void or `rx.Observable`, it is executed on the calling thread.

Do you want to provide the service multithreading implementation?, worker threads are easily disabled:

        @Service(value=ServiceImpl.class, useWorkerThread=false)
        public interface MyService { ... }

##Service Connection
Acacia does the handling of establishing the service connection for you. If you invoke methods on a
service that is not connected yet, the execution is saved to a pending queue and run when the service
is connected automatically, in the same order of execution.
This is only possible for methods which return type is `void` or `rx.Observable`, if the method
returns something else it will still return immediately with a default value (null, false, 0) and
be executed when the service is connected:

        public class MainActivity extends ActionBarActivity {
            ...
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                MyService service = Acacia.createService(context, MyService.class);

                // Inner Android Service connects at some point in future after onCreate, at which
                // time these method invocations will be executed in order
                service.doProcessing(foo);
                service.doProcessing(bar);
            }
            ...
        }

