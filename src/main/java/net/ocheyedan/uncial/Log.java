package net.ocheyedan.uncial;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 7:17 AM
 * 
 * A convenience class in which users may log statically yet still get information back about which {@link Class}
 * did the logging.
 */
public final class Log {
    
    public static void log(Meta meta, String level, String message, Object ... params) {
        Loggers.get().log(meta, level, message, params);
    }

    public static void log(String level, String message, Object ... params) {
        log(Loggers.meta(2, Loggers.invokingLogClass()), level, message, params);
    }

    public static void trace(String message, Object ... params) {
        log(Logger.trace, message, params);
    }

    public static void debug(String message, Object ... params) {
        log(Logger.debug, message, params);
    }

    public static void info(String message, Object ... params) {
        log(Logger.info, message, params);
    }

    public static void warn(String message, Object ... params) {
        log(Logger.warn, message, params);
    }

    public static void error(String message, Object ... params) {
        log(Logger.error, message, params);
    }

    public static void error(Throwable t) {
        Loggers.get().error(t);
    }

    public static void error(Throwable t, String message, Object ... params) {
        error(t);
        log(Loggers.from(t), Logger.error, message, params);
    }

    private Log() { }

}
