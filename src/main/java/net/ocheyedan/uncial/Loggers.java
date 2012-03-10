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
    
    public static boolean isTraceEnabled() {
        return isEnabled(Logger.trace);
    }

    public static boolean isDebugEnabled() {
        return isEnabled(Logger.debug);
    }

    public static boolean isInfoEnabled() {
        return isEnabled(Logger.info);
    }

    public static boolean isWarnEnabled() {
        return isEnabled(Logger.warn);
    }

    public static boolean isErrorEnabled() {
        return isEnabled(Logger.error);
    }

    public static Meta from(Throwable t) {
        final StackTraceElement stackTrace = t.getStackTrace()[3];
        Class<?> invokingClass;
        try {
            invokingClass = Class.forName(stackTrace.getClassName());
        } catch (ClassNotFoundException cnfe) {
            invokingClass = null;
        }
        return new MetaComplete(invokingClass, stackTrace.getMethodName(), stackTrace.getLineNumber(), stackTrace.getFileName());
    }
    
    static Meta meta(int depth, Class<?> loggingFor) {
        return meta(depth, loggingFor, null, null, null);
    }

    static Meta meta(int depth, Class<?> loggingFor, String canProvideMethodName) {
        return meta(depth, loggingFor, canProvideMethodName, null, null);
    }

    static Meta meta(int depth, Class<?> loggingFor, String canProvideMethodName, Integer canProvideLineNumber) {
        return meta(depth, loggingFor, canProvideMethodName, canProvideLineNumber, null);
    }

    static Meta meta(int depth, Class<?> loggingFor, String canProvideMethodName, Integer canProvideLineNumber, String canProvideFileName) {
        // if nothing is null, return without consulting configuration as the configuration is inconsequential
        if ((loggingFor != null) && (canProvideMethodName != null) && (canProvideLineNumber != null) &&
                (canProvideFileName != null)) {
            return new MetaComplete(loggingFor, canProvideMethodName, canProvideLineNumber, canProvideFileName);
        }
        // TODO - consult Configuration to see which meta information is strictly necessary
    }

    static Class<?> invokingLogClass() {
        return classProvider.invokingClass();
    }
    
    static Logger get() {
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
