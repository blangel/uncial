package net.ocheyedan.uncial;

/**
 * User: blangel
 * Date: 3/9/12
 * Time: 4:34 PM
 * 
 * The definition of a logger within the {@literal Uncial} framework.
 */
public interface Logger {

    /**
     * The {@literal trace} log level; used by method {@link #trace(String, Object[])}
     */
    static final String trace = "trace";

    /**
     * The {@literal debug} log level; used by method {@link #debug(String, Object[])}
     */
    static final String debug = "debug";

    /**
     * The {@literal info} log level; used by method {@link #info(String, Object[])}
     */
    static final String info = "info";

    /**
     * The {@literal warn} log level; used by method {@link #warn(String, Object[])}
     */
    static final String warn = "warn";

    /**
     * The {@literal error} log level; used by method {@link #error(String, Object[])}
     */
    static final String error = "error";

    /**
     * The log method to which all other methods within a {@link Logger} implementation should delegate.
     * @param meta information about the logging invocation.
     * @param level for which to log the {@code message}; can be anything.
     * @param message to log; can be a formatted string (like taken in {@link String#format(String, Object...)})
     * @param params to the formatted message.
     * @see String#format(String, Object...)
     */
    void log(Meta meta, String level, String message, Object ... params);

    /**
     * Calls {@link #log(Meta, String, String, Object...)} with a minimally populated {@link Meta} implementation.
     * The {@link Meta} implementation will only provide information which is either cheap to obtain (i.e., th
     * calling class) or which is explicitly demanded via configuration (i.e., user has asked for line numbers and
     * so this method will construct a {@link Meta} implementation which contains line numbers).
     * @param level for which to log the {@code message}; can be anything.
     * @param message to log; can be a formatted string (like taken in {@link String#format(String, Object...)})
     * @param params to the formatted message.
     * @see String#format(String, Object...)
     */
    void log(String level, String message, Object ... params);

    /**
     * Calls {@link #log(String, String, Object...)} with {@link #trace} as the level parameter
     * @param message @see {@link #log(String, String, Object...)}
     * @param params @see {@link #log(String, String, Object...)}
     * @see #log(String, String, Object...)
     */
    void trace(String message, Object ... params);

    /**
     * Calls {@link #log(String, String, Object...)} with {@link #debug} as the level parameter
     * @param message @see {@link #log(String, String, Object...)}
     * @param params @see {@link #log(String, String, Object...)}
     * @see #log(String, String, Object...)
     */
    void debug(String message, Object ... params);

    /**
     * Calls {@link #log(String, String, Object...)} with {@link #info} as the level parameter
     * @param message @see {@link #log(String, String, Object...)}
     * @param params @see {@link #log(String, String, Object...)}
     * @see #log(String, String, Object...)
     */
    void info(String message, Object ... params);

    /**
     * Calls {@link #log(String, String, Object...)} with {@link #warn} as the level parameter
     * @param message @see {@link #log(String, String, Object...)}
     * @param params @see {@link #log(String, String, Object...)}
     * @see #log(String, String, Object...)
     */
    void warn(String message, Object ... params);

    /**
     * Calls {@link #log(String, String, Object...)} with {@link #error} as the level parameter
     * @param message @see {@link #log(String, String, Object...)}
     * @param params @see {@link #log(String, String, Object...)}
     * @see #log(String, String, Object...)
     */
    void error(String message, Object ... params);

    /**
     * Logs the {@link Class} of {@code t} followed by the value of {@link Throwable#getMessage()} and then
     * the {@link String} value of {@link Throwable#printStackTrace(java.io.PrintWriter)}
     * @param t to log
     */
    void error(Throwable t);

    /**
     * Calls {@link #error(Throwable)} with {@code t} then calls {@link #log(String, String, Object...)} with
     * {@link #error} as the level parameter.
     * @param t @see {@link #error(Throwable)}
     * @param message @see {@link #log(String, String, Object...)}
     * @param params @see {@link #log(String, String, Object...)}
     * @see #log(String, String, Object...)
     * @see #error(Throwable)
     */
    void error(Throwable t, String message, Object ... params);
    
}
