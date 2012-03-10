package net.ocheyedan.uncial;

/**
 * User: blangel
 * Date: 3/9/12
 * Time: 4:29 PM
 *
 * A simple logging system utilizing new features added into the {@literal java} language in {@literal >1.5}
 * The desire is to be simple and fast not "full featured."
 */
final class Uncial implements Logger {
    
    private final Class<?> loggingFor;
    
    Uncial(Class<?> loggingFor) {
        this.loggingFor = loggingFor;
    }

    @Override public void log(Meta meta, String level, String message, Object ... params) {
        if (!Loggers.isEnabled(level)) {
            return;
        }
        // this must be logged, so create formatted message now (as {@code params} may be mutable and modified by user
        // after method return).
        String formattedMessage = String.format(message, params);
        // create serializable and place in log-queue

    }

    @Override public void log(String level, String message, Object ... params) {
        // check level before delegation to {@link #log(Meta, String, String, Object[]) as creation of Meta may be expensive
        if (!Loggers.isEnabled(level)) {
            return;
        }
        log(Loggers.meta(1, loggingFor), level, message, params);
    }

    @Override public final void trace(String message, Object ... params) {
        log(Logger.trace, message, params);
    }

    @Override public final void debug(String message, Object ... params) {
        log(Logger.debug, message, params);
    }

    @Override public final void info(String message, Object ... params) {
        log(Logger.info, message, params);
    }

    @Override public final void warn(String message, Object ... params) {
        log(Logger.warn, message, params);
    }

    @Override public final void error(String message, Object ... params) {
        log(Logger.error, message, params);
    }

    @Override public void error(Throwable t) {
        
    }

    @Override public final void error(Throwable t, String message, Object ... params) {
        error(t);
        log(Loggers.from(t), Logger.error, message, params);
    }
}
