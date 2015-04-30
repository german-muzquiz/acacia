package com.gmr.acacia;

/**
 * Used to report errors during AutoService execution.
 */
public class AutoServiceException extends RuntimeException
{

    public AutoServiceException(String message)
    {
        super(message);
    }

    public AutoServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
