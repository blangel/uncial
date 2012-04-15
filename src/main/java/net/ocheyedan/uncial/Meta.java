package net.ocheyedan.uncial;

import java.io.Serializable;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 7:54 AM
 * 
 * Provides access to the meta information about a logging invocation; including the invoking {@link Class}, the 
 * associated method name and line number if desired.
 */
public interface Meta extends Serializable {

    /**
     * @return the {@link Class} object from which the logging event originated.
     */
    Class<?> invokingClass();

    /**
     * @return the {@link Class#getName()} value of the result of executing {@link #invokingClass()}
     */
    String invokingClassName();

    /**
     * @return the method name from which the logging event originated.
     */
    String invokingMethodName();

    /**
     * @return the line number within the source file from which the logging event originated.
     */
    Integer invokingLineNumber();

    /**
     * @return the name of the source file from which the logging event originated.
     */
    String invokingFileName();

    /**
     * @return the {@link Thread#getName()} of the {@link Thread} from which the logging event originated (the current
     *         thread is that returned by {@link Thread#currentThread()} at point of logging).
     */
    String invokingThreadName();

    /**
     * @return the {@literal unix-epoch} time at which the logging event happened.
     */
    long invokingEpochTime();
}
