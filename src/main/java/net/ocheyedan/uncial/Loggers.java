package net.ocheyedan.uncial;

import sun.reflect.Reflection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 7:20 AM
 * 
 * A utility class which provides convenience methods regarding creation of {@link Logger} objects.
 */
public final class Loggers {

    /**
     * Users can specify how to handle formatted/parameterized messages.  The default is to use {@literal printf}
     * style (i.e., {@link String#format(String, Object...)}) but if the user the specifies system property {@literal uncial.slf4j}
     * then the {@literal SLF4J} style {@literal {}} is used.
     */
    private static final Class<? extends Formatter> formatterClass;

    /**
     * Users can specify whether logging to the registered {@link net.ocheyedan.uncial.appender.Appender} objects happens
     * on a separate thread (the default) or whether everything happens on the user's invoking thread.  User's specify
     * single-threaded via the system property {@literal uncial.singleThreaded}.
     */
    private static final Distributor appenderExecutor;

    static {
        boolean useSlf4j = Boolean.getBoolean("uncial.slf4j");
        if (useSlf4j) {
            formatterClass = Formatter.Slf4j.class;
        } else {
            formatterClass = Formatter.Uncial.class;
        }
        if (Boolean.getBoolean("uncial.singleThreaded")) {
            appenderExecutor = new Distributor.InvokingThread();
        } else {
            appenderExecutor = new Distributor.SeparateThread();
        }
    }

    private static final ConcurrentMap<Class<?>, Logger> loggers = new ConcurrentHashMap<Class<?>, Logger>();

    /**
     * @param forClass is the {@link Class} for which to perform logging
     * @return a {@link Logger} implementation specific to {@code forClass}
     */
    public static Logger get(Class<?> forClass) {
        if (loggers.containsKey(forClass)) {
            return loggers.get(forClass);
        }
        UncialConfig.get().setLevelIfNotPresent(forClass.getName()); // setup the logger config
        // eliminate possibility of ever returning a different instance of the same logging class
        // constructing a logger for the same class is fine, but never return it.
        loggers.putIfAbsent(forClass, new Uncial(forClass, newFormatter(), appenderExecutor));
        return loggers.get(forClass);
    }

    /**
     * @return a new instance of the {@link Formatter} implementation.
     */
    private static Formatter newFormatter() {
        try {
            return formatterClass.newInstance();
        } catch (InstantiationException ie) {
            throw new AssertionError(ie.getMessage());
        } catch (IllegalAccessException iae) {
            throw new AssertionError(iae.getMessage());
        }
    }

    /**
     * @param level at which to check if logging is enabled {@code forClass}.
     * @param forClass for which to check if {@code level} logging is enabled.
     * @return true if logging is enabled for {@code forClass} at level {@code level}
     */
    public static boolean isEnabled(String level, Class<?> forClass) {
        return UncialConfig.get().isEnabled(level, forClass);
    }

    /**
     * @param forClass for which to check if {@link Logger#error} level logging is enabled.
     * @return true if {@link Logger#trace} is enabled.
     */
    public static boolean isTraceEnabled(Class<?> forClass) {
        return UncialConfig.get().isEnabled(Logger.trace, forClass);
    }

    /**
     * @param forClass for which to check if {@link Logger#error} level logging is enabled.
     * @return true if {@link Logger#debug} is enabled.
     */
    public static boolean isDebugEnabled(Class<?> forClass) {
        return UncialConfig.get().isEnabled(Logger.debug, forClass);
    }

    /**
     * @param forClass for which to check if {@link Logger#error} level logging is enabled.
     * @return true if {@link Logger#info} is enabled.
     */
    public static boolean isInfoEnabled(Class<?> forClass) {
        return UncialConfig.get().isEnabled(Logger.info, forClass);
    }

    /**
     * @param forClass for which to check if {@link Logger#error} level logging is enabled.
     * @return true if {@link Logger#warn} is enabled.
     */
    public static boolean isWarnEnabled(Class<?> forClass) {
        return UncialConfig.get().isEnabled(Logger.warn, forClass);
    }

    /**
     * @param forClass for which to check if {@link Logger#error} level logging is enabled.
     * @return true if {@link Logger#error} is enabled.
     */
    public static boolean isErrorEnabled(Class<?> forClass) {
        return UncialConfig.get().isEnabled(Logger.error, forClass);
    }

    /**
     * Constructs a {@link Meta} object from all non-null parameters augmenting the null parameters as necessary (i.e.,
     * if an {@link net.ocheyedan.uncial.appender.Appender} configuration needs them).
     * @param loggingFor the {@link Class} for which to create the {@link Meta} object
     * @param canProvideMethodName the method name or null if not known
     * @param canProvideLineNumber the line number or null if not known
     * @param canProvideFileName the file name or null if not known
     * @param canProvideThreadName the thread name or null if not known
     * @param epochTime the time at which the log occurred
     * @return a {@link Meta} object representing the provided information and augmented as necessary.
     */
    static Meta meta(Class<?> loggingFor, String canProvideMethodName, Integer canProvideLineNumber,
                     String canProvideFileName, String canProvideThreadName, long epochTime) {
        if (loggingFor == null) {
            throw new NullPointerException("Must provide for which class is being logged.");
        }
        // the information which is non-trivial in terms of time to retrieve is methodName/lineNumber/fileName
        String threadName = (canProvideThreadName == null ? Thread.currentThread().getName() : canProvideThreadName);
        // if nothing is null, return without consulting configuration as the configuration is inconsequential
        if ((canProvideMethodName != null) && (canProvideLineNumber != null) &&
                (canProvideFileName != null) && (canProvideThreadName != null)) {
            return new MetaComplete(loggingFor, canProvideMethodName, canProvideLineNumber, canProvideFileName,
                                    canProvideThreadName, epochTime);
        }
        // so, don't have either the methodName/lineNumber/fileName but do we even need them?
        UncialConfig uncialConfig = UncialConfig.get();
        // all those are retrieved the same way, so if any one of those is needed, it doesn't matter, all can be constructed.
        boolean need = (uncialConfig.needsMethodName(loggingFor) || uncialConfig.needsLineNumber(loggingFor)
                            || uncialConfig.needsFileName(loggingFor));
        if (need) {
            StackTraceElement stackTraceElement = getCaller(new Exception().getStackTrace());
            return new MetaComplete(loggingFor, stackTraceElement.getMethodName(), stackTraceElement.getLineNumber(),
                                    stackTraceElement.getFileName(), threadName, epochTime);
        } else {
            // expensive things to compute are not even needed...
            return new MetaComplete(loggingFor, canProvideMethodName, canProvideLineNumber, canProvideFileName, threadName, epochTime);
        }
    }

    /**
     * Retrieves the first {@link StackTraceElement} from {@code stackTrace} which is not related to {@literal uncial}.
     * Assumes {@code stackTrace} has at least one element which can be skipped as that is the point at which
     * {@literal uncial} made the {@link Throwable} object from which {@code stackTrace} originates (said another way, the
     * originating {@code stackTrace} comes from {@literal uncial} itself).
     * @param stackTrace from which to retrieve the first non-{@literal uncial} element.
     * @return the first {@link StackTraceElement} from {@code stackTrace} which is not related to {@literal uncial}.
     * @throws AssertionError if there is no such {@link StackTraceElement} to return
     */
    private static StackTraceElement getCaller(StackTraceElement[] stackTrace) {
        if ((stackTrace == null) || (stackTrace.length < 3)) {
            throw new AssertionError("Given a null StrackTraceElement[] or not originating from the Uncial system.");
        }
        for (int i = 2; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            if (!element.getClassName().startsWith("net.ocheyedan.uncial")
                    || element.getClassName().startsWith("net.ocheyedan.uncial.caliper")) { // TODO - keep this? essentially a hack for benchmark
                return element;
            }
        }
        throw new AssertionError("Given a StackTraceElement[] completely isolated to the Uncial system.");
    }

    /**
     * To be called internally by {@literal Uncial}.
     * @return the invoking class, assuming two levels of uncial invocations before this method was invoked.
     */
    static Class<?> invokingLogClass() {
        // one level to get to unical,
        // one for invokingLogClass,
        // another for Reflection.getCallerClass itself
        return Reflection.getCallerClass(3);
    }

    private Loggers() { }

}
