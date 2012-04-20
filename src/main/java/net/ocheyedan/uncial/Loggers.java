package net.ocheyedan.uncial;

import sun.reflect.Reflection;

import java.util.Collection;
import java.util.concurrent.*;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 7:20 AM
 * 
 * A utility class which provides convenience methods regarding creation of {@link Logger} objects.
 */
public final class Loggers {
    
    static final Meta classProvider = getStaticMetaClassPartial();

    private static final ExecutorService logEventExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
        @Override public Thread newThread(Runnable r) {
            Thread defaultThread = defaultThreadFactory.newThread(r);
            defaultThread.setDaemon(true);
            return defaultThread;
        }
    });

    private static final ConcurrentMap<Class<?>, Logger> loggers = new ConcurrentHashMap<Class<?>, Logger>();

    /**
     * @param forClass is the {@link Class} for which to perform logging
     * @return a {@link Logger} implementation specific to {@code forClass}
     */
    public static Logger get(Class<?> forClass) {
        if (loggers.containsKey(forClass)) {
            return loggers.get(forClass);
        }
        // eliminate possibility of ever returning a different instance of the same logging class
        // constructing a logger for the same class is fine, but never return it.
        loggers.putIfAbsent(forClass, new Uncial(forClass));
        return loggers.get(forClass);
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
        // if nothing is null, return without consulting configuration as the configuration is inconsequential
        if ((canProvideMethodName != null) && (canProvideLineNumber != null) &&
                (canProvideFileName != null) && (canProvideThreadName != null)) {
            return new MetaComplete(loggingFor, canProvideMethodName, canProvideLineNumber, canProvideFileName,
                                    canProvideThreadName, epochTime);
        }
        // the information which is non-trivial in terms of time to retrieve is methodName/lineNumber/fileName
        String threadName = (canProvideThreadName == null ? Thread.currentThread().getName() : canProvideThreadName);
        if ((canProvideMethodName != null) && (canProvideLineNumber != null) && (canProvideFileName != null)) {
            return new MetaComplete(loggingFor, canProvideMethodName, canProvideLineNumber, canProvideFileName, threadName, epochTime);
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
     * Distributes {@code logEvent} to all added {@link net.ocheyedan.uncial.appender.Appender} objects.
     * Note, this distribution happens asynchronously by executing a {@link Runnable} on an {@link Executor}.
     * @param logEvent to log
     */
    static void distribute(final LogEvent logEvent) {
        logEventExecutor.execute(new Runnable() {
            @Override public void run() {
                Collection<UncialConfig.AppenderConfig> appenderConfigs = UncialConfig.get().getAppenderConfigs();
                for (UncialConfig.AppenderConfig appenderConfig : appenderConfigs) {
                    String message = appenderConfig.format(logEvent);
                    appenderConfig.appender.handle(message);
                }
            }
        });
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
        if ((stackTrace == null) || (stackTrace.length < 2)) {
            throw new AssertionError("Given a null StrackTraceElement[] or not originating from the Uncial system.");
        }
        for (int i = 1; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            if (!element.getClassName().startsWith("net.ocheyedan.uncial")) {
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
        return classProvider.invokingClass();
    }

    private static Meta getStaticMetaClassPartial() {
        return new MetaPartial() {
            private static final long serialVersionUID = -7205263618048228542L;
            @Override public Class<?> invokingClass() {
                // one level to get to unical,
                // one for invokingLogClass,
                // one for invokingClass and
                // another for Reflection.getCallerClass itself
                return Reflection.getCallerClass(4);
            }
        };
    }

    private Loggers() { }

}
