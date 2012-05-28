package net.ocheyedan.uncial;

import sun.reflect.Reflection;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 7:20 AM
 * 
 * A utility class which provides convenience methods regarding creation of {@link Logger} objects.
 */
public final class Loggers {

    /**
     * Users can specify whether logging to the registered {@link net.ocheyedan.uncial.appender.Appender} objects happens
     * on a separate thread (the default) or whether logging happens on the user's invoking thread.  User's specify
     * single-threaded behavior via the system property {@literal uncial.singleThreaded}.
     */
    static final Distributor appenderExecutor;

    static {
        if (Boolean.getBoolean("uncial.singleThreaded")) {
            appenderExecutor = new Distributor.InvokingThread();
        } else {
            appenderExecutor = new Distributor.SeparateThread();
        }
        // periodically flush the appenders
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override public void run() {
                Collection<UncialConfig.AppenderConfig> appenderConfigs = UncialConfig.get().getAppenderConfigs();
                for (UncialConfig.AppenderConfig appenderConfig : appenderConfigs) {
                    appenderConfig.appender.flush();
                }
            }
        }, 15L, 15L, TimeUnit.SECONDS);
    }

    private static final ConcurrentMap<Class<?>, Logger> loggers = new ConcurrentHashMap<Class<?>, Logger>();

    /**
     * Cache of the {@link LoggerFactory} implementation.
     */
    private static final AtomicReference<LoggerFactory> loggerFactory = new AtomicReference<LoggerFactory>();

    /**
     * @param forClass is the {@link Class} for which to perform logging
     * @return a {@link Logger} implementation specific to {@code forClass}
     */
    @SuppressWarnings("unchecked")
    public static Logger get(Class<?> forClass) {
        return get(forClass, Formatter.Printf.class);
    }

    /**
     * @param forClass is the {@link Class} for which to perform logging
     * @param formatterClass the class to use as the {@link Formatter}; either {@literal printf} style (i.e.,
     *                       {@link String#format(String, Object...)}) or {@literal SLF4J} style (i.e., {@literal {}}).
     * @return a {@link Logger} implementation specific to {@code forClass}
     */
    @SuppressWarnings("unchecked")
    public static Logger get(Class<?> forClass, Class<? extends Formatter> formatterClass) {
        if (loggers.containsKey(forClass)) {
            return loggers.get(forClass);
        }
        UncialConfig.get().setLevelIfNotPresent(forClass.getName()); // setup the logger config
        // eliminate possibility of ever returning a different instance of the same logging class
        // constructing a logger for the same class is fine, but never return it.
        loggers.putIfAbsent(forClass, newLogger(forClass, newFormatter(formatterClass)));
        return loggers.get(forClass);
    }

    /**
     * Constructs the {@literal Uncial} {@linkplain Logger} object.  This is how one can extend {@literal Uncial}
     * to have {@literal Uncial} log calls (and consequently {@literal SLF4J} log calls, if using {@literal Uncial}
     * as the {@literal SLF4J} logger) routed to a different logging mechanism (e.g., to {@literal Android}'s logging
     * system).  To do this; create class {@literal net.ocheyedan.uncial.impl.LoggerFactory} which has a public
     * static method named {@literal getSingleton} which returns the {@link LoggerFactory} implementation.
     * @param forClass is the {@link Class} for which to perform logging.
     * @param formatter the {@link Formatter} to use in case the
     * @return the {@link Logger} implementation instance
     */
    private static Logger newLogger(Class<?> forClass, Formatter formatter) {
        LoggerFactory loggerFactory;
        sync:synchronized (Loggers.loggerFactory) {
            if ((loggerFactory = Loggers.loggerFactory.get()) != null) {
                break sync;
            }
            try {
                Class loggerFactoryClass = Class.forName("net.ocheyedan.uncial.impl.LoggerFactory");
                Method getSingletonMethod = loggerFactoryClass.getMethod("getSingleton");
                if (LoggerFactory.class.isAssignableFrom(getSingletonMethod.getReturnType())) {
                    loggerFactory = (LoggerFactory) getSingletonMethod.invoke(null);
                } else {
                    loggerFactory = new UncialLoggerFactory();
                }
            } catch (Exception e) {
                loggerFactory = new UncialLoggerFactory();
            }
            Loggers.loggerFactory.set(loggerFactory);
        }
        return loggerFactory.construct(forClass, formatter);
    }

    /**
     * @return a new instance of the {@link Formatter} implementation.
     */
    private static Formatter newFormatter(Class<? extends Formatter> formatterClass) {
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
            return new Meta.Default(loggingFor, canProvideMethodName, canProvideLineNumber, canProvideFileName,
                                    canProvideThreadName, epochTime);
        }
        // so, don't have either the methodName/lineNumber/fileName but do we even need them?
        UncialConfig uncialConfig = UncialConfig.get();
        // all those are retrieved the same way, so if any one of those is needed, it doesn't matter, all can be constructed.
        boolean need = (uncialConfig.needsMethodName(loggingFor) || uncialConfig.needsLineNumber(loggingFor)
                            || uncialConfig.needsFileName(loggingFor));
        if (need) {
            StackTraceElement stackTraceElement = getCaller(new Exception().getStackTrace());
            return new Meta.Default(loggingFor, stackTraceElement.getMethodName(), stackTraceElement.getLineNumber(),
                                    stackTraceElement.getFileName(), threadName, epochTime);
        } else {
            // expensive things to compute are not even needed...
            return new Meta.Default(loggingFor, canProvideMethodName, canProvideLineNumber, canProvideFileName, threadName, epochTime);
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
