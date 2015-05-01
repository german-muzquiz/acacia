package com.gmr.acacia;

/**
 * Used to report errors during Acacia execution.
 */
public class AcaciaException extends RuntimeException
{

    public AcaciaException(String message)
    {
        super(message);
    }

    public AcaciaException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
