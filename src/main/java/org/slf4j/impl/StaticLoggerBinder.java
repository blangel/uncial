package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * User: blangel
 * Date: 4/29/12
 * Time: 5:07 PM
 *
 * The binding of {@link org.slf4j.LoggerFactory} class with an actual instance of
 * {@link ILoggerFactory} is performed using information returned by this class.
 *
 * @see {@literal http://www.slf4j.org/faq.html#slf4j_compatible}
 *
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {

    /**
     * The unique instance of this class.
     */
    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    /**
     * Return the singleton of this class.
     *
     * @return the StaticLoggerBinder singleton
     */
    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    /**
     * Version tag used to check compatibility. The value of this field is
     * modified with each release.
     */

    // to avoid constant folding by the compiler, this field must *not* be final
    public static String REQUESTED_API_VERSION = "1.6";

    // Binding specific code:
    private static final String loggerFactoryClassStr = UncialLoggerFactory.class.getName();

    /**
     * The ILoggerFactory instance returned by the {@link #getLoggerFactory}
     * method should always be the same object
     */
    private final ILoggerFactory loggerFactory;

    private StaticLoggerBinder() {
        // Binding specific code:
        loggerFactory = new UncialLoggerFactory();
    }

    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    public String getLoggerFactoryClassStr() {
        return loggerFactoryClassStr;
    }
}
