package net.ocheyedan.uncial;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 7:17 AM
 * 
 * A convenience class in which users may log statically yet still get information about which {@link Class}
 * did the logging.
 * There is a performance penalty to this convenience, so if performance is critical, get a handle to a class-specific
 * {@link Logger} once ({@link net.ocheyedan.uncial.Loggers#get(Class)}) and invoke via it.
 */
public final class Log {

    /**
     * Logs {@code message} with {@code params} for {@code level} and {@code meta}.
     * @param meta information about this log invocation
     * @param level at which to log
     * @param message the formatted message (formatted a la {@link String#format(String, Object...)})
     * @param params to substitute into {@code message} (a la {@link String#format(String, Object...)})
     */
    public static void log(Meta meta, String level, String message, Object ... params) {
        if (meta == null) {
            throw new IllegalArgumentException("Meta cannot be null when logging.");
        }
        Loggers.get(meta.invokingClass()).log(meta, level, message, params);
    }

    /**
     * Logs {@code message} with {@code params} for {@code level} constructing a {@link Meta} information based
     * on what is configured for the class for which to log.
     * @param level at which to log
     * @param message the formatted message (formatted a la {@link String#format(String, Object...)})
     * @param params to substitute into {@code message} (a la {@link String#format(String, Object...)})
     */
    public static void log(String level, String message, Object ... params) {
        log(Loggers.invokingLogClass(), level, message, params);
    }

    /**
     * Logs {@code message} with {@code params} for level {@link Logger#trace} constructing a {@link Meta} information based
     * on what is configured for the class for which to log.
     * @param message the formatted message (formatted a la {@link String#format(String, Object...)})
     * @param params to substitute into {@code message} (a la {@link String#format(String, Object...)})
     */
    public static void trace(String message, Object ... params) {
        log(Loggers.invokingLogClass(), Logger.trace, message, params);
    }

    /**
     * Logs {@code message} with {@code params} for level {@link Logger#debug} constructing a {@link Meta} information based
     * on what is configured for the class for which to log.
     * @param message the formatted message (formatted a la {@link String#format(String, Object...)})
     * @param params to substitute into {@code message} (a la {@link String#format(String, Object...)})
     */
    public static void debug(String message, Object ... params) {
        log(Loggers.invokingLogClass(), Logger.debug, message, params);
    }

    /**
     * Logs {@code message} with {@code params} for level {@link Logger#info} constructing a {@link Meta} information based
     * on what is configured for the class for which to log.
     * @param message the formatted message (formatted a la {@link String#format(String, Object...)})
     * @param params to substitute into {@code message} (a la {@link String#format(String, Object...)})
     */
    public static void info(String message, Object ... params) {
        log(Loggers.invokingLogClass(), Logger.info, message, params);
    }

    /**
     * Logs {@code message} with {@code params} for level {@link Logger#warn} constructing a {@link Meta} information based
     * on what is configured for the class for which to log.
     * @param message the formatted message (formatted a la {@link String#format(String, Object...)})
     * @param params to substitute into {@code message} (a la {@link String#format(String, Object...)})
     */
    public static void warn(String message, Object ... params) {
        log(Loggers.invokingLogClass(), Logger.warn, message, params);
    }

    /**
     * Logs {@code message} with {@code params} for level {@link Logger#error} constructing a {@link Meta} information based
     * on what is configured for the class for which to log.
     * @param message the formatted message (formatted a la {@link String#format(String, Object...)})
     * @param params to substitute into {@code message} (a la {@link String#format(String, Object...)})
     */
    public static void error(String message, Object ... params) {
        log(Loggers.invokingLogClass(), Logger.error, message, params);
    }

    /**
     * Serializes the stack-trace of {@code t} and logs that at level {@link Logger#error} and then calls
     * {@link #error(String, Object...)} with {@code message} and {@code params} as parameters.
     * @param t for which to log its stack-trace ({@link Throwable#printStackTrace()}).
     * @param message the formatted message (formatted a la {@link String#format(String, Object...)})
     * @param params to substitute into {@code message} (a la {@link String#format(String, Object...)})
     *
     * @see #error(String, Object...)
     */
    public static void error(Throwable t, String message, Object ... params) {
        long now = System.currentTimeMillis();
        Class<?> loggingFor = Loggers.invokingLogClass();
        Loggers.get(loggingFor).error(t);
        log(Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now), Logger.error, message, params);
    }

    private static void log(Class<?> loggingFor, String level, String message, Object ... params) {
        // check level before delegation to {@link #log(Meta, String, String, Object[]}  as creation of Meta may be expensive
        if (!Loggers.isEnabled(level, loggingFor)) {
            return;
        }
        log(Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), System.currentTimeMillis()), level, message, params);
    }

    private Log() { }

}
