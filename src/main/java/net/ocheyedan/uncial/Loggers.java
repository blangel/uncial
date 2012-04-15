package net.ocheyedan.uncial;

import sun.reflect.Reflection;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 7:20 AM
 * 
 * A utility class which provides convenience methods regarding creation of {@link Logger} objects.
 */
public final class Loggers {
    
    static final Meta classProvider = getStaticMetaClassPartial();
    
    public static boolean isEnabled(String level) {
        return false; // TODO - consult configuration
    }

    /**
     * @return true if {@link Logger#trace} is enabled.
     */
    public static boolean isTraceEnabled() {
        return isEnabled(Logger.trace);
    }

    /**
     * @return true if {@link Logger#debug} is enabled.
     */
    public static boolean isDebugEnabled() {
        return isEnabled(Logger.debug);
    }

    /**
     * @return true if {@link Logger#info} is enabled.
     */
    public static boolean isInfoEnabled() {
        return isEnabled(Logger.info);
    }

    /**
     * @return true if {@link Logger#warn} is enabled.
     */
    public static boolean isWarnEnabled() {
        return isEnabled(Logger.warn);
    }

    /**
     * @return true if {@link Logger#error} is enabled.
     */
    public static boolean isErrorEnabled() {
        return isEnabled(Logger.error);
    }

    /**
     * Since {@link Throwable} objects contain all information necessary to construct a fully populated {@link Meta}
     * object, this is a convenience method to construct a {@link Meta} object from a {@link Throwable} object.
     * Note, the {@link net.ocheyedan.uncial.Meta#invokingThreadName()} is assumed to be the current thread at
     * the point of <i>this</i> method invocation.
     * @param t from which to construct a {@link Meta} object
     * @param threadName from which the logging event occurred
     * @param epochTime at which the logging event occurred
     * @return a {@link Meta} object built from the values within {@code t}
     */
    public static Meta from(Throwable t, String threadName, long epochTime) {
        final StackTraceElement stackTrace = t.getStackTrace()[3];
        Class<?> invokingClass;
        try {
            invokingClass = Class.forName(stackTrace.getClassName());
        } catch (ClassNotFoundException cnfe) {
            invokingClass = null;
        }
        return new MetaComplete(invokingClass, stackTrace.getMethodName(), stackTrace.getLineNumber(),
                                stackTrace.getFileName(), threadName, epochTime);
    }
    
    static Meta meta(int depth, Class<?> loggingFor, long epochTime) {
        return meta(depth, loggingFor, null, null, null, null, epochTime);
    }

    static Meta meta(int depth, Class<?> loggingFor, String canProvideMethodName, long epochTime) {
        return meta(depth, loggingFor, canProvideMethodName, null, null, null, epochTime);
    }

    static Meta meta(int depth, Class<?> loggingFor, String canProvideMethodName, Integer canProvideLineNumber, long epochTime) {
        return meta(depth, loggingFor, canProvideMethodName, canProvideLineNumber, null, null, epochTime);
    }

    static Meta meta(int depth, Class<?> loggingFor, String canProvideMethodName, Integer canProvideLineNumber,
                     String canProvideFileName, long epochTime) {
        return meta(depth, loggingFor, canProvideMethodName, canProvideLineNumber, canProvideFileName, null, epochTime);
    }

    static Meta meta(int depth, Class<?> loggingFor, String canProvideMethodName, Integer canProvideLineNumber,
                     String canProvideFileName, String canProvideThreadName, long epochTime) {
        // if nothing is null, return without consulting configuration as the configuration is inconsequential
        if ((loggingFor != null) && (canProvideMethodName != null) && (canProvideLineNumber != null) &&
                (canProvideFileName != null) && (canProvideThreadName != null)) {
            return new MetaComplete(loggingFor, canProvideMethodName, canProvideLineNumber, canProvideFileName,
                                    canProvideThreadName, epochTime);
        }
        // TODO - consult UncialConfig to see which meta information is strictly necessary
        return null;
    }

    static Class<?> invokingLogClass() {
        return classProvider.invokingClass();
    }

    static Logger get() { // TODO - cached logger
        return new Uncial(classProvider.invokingClass());
    }

    private static Meta getStaticMetaClassPartial() {
        return new MetaPartial() {
            private static final long serialVersionUID = -7205263618048228542L;
            @Override public Class<?> invokingClass() {
                return Reflection.getCallerClass(3);
            }
        };
    }

    private Loggers() { }

}
