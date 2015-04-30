package com.gmr.acacia.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation in an interface meant to be a Service:
 *
 * <pre>
 *     {@literal @Service(DefaultCoolService.class) }
 *     public interface MyCoolService {
 *
 *     }
 * </pre>
 *
 * The value attribute is the class implementing the interface.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Service
{

    /**
     * @return class implementing the interface on which this annotation is used (required).
     */
    public Class<?> value();

    /**
     * Whether or not to use a worker thread on the service.
     *
     * When using a worker thread, all methods invoked on the service will be run on a single,
     * separate thread sequentially.
     * If the method returns a {@link rx.Observable}, subscriptions are automatically executed
     * on the worker thread.
     * If the method return type is not void or {@link rx.Observable}, it is executed on the
     * calling thread.
     *
     * @return true to use a worker thread (default).
     */
    public boolean useWorkerThread() default true;

}
