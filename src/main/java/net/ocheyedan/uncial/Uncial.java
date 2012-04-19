package net.ocheyedan.uncial;

import java.io.PrintWriter;
import java.io.StringWriter;

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
        if (!Loggers.isEnabled(level, loggingFor)) {
            return;
        }
        // this must be logged, so create formatted message now (as {@code params} may be mutable and modified by user
        // after method return).
        String formattedMessage = String.format(message, params);
        // create serializable and place in log-queue
        Loggers.distribute(new LogEvent(meta, level, formattedMessage));
    }

    @Override public void log(String level, String message, Object ... params) {
        long now = System.currentTimeMillis();
        // check level before delegation to {@link #log(Meta, String, String, Object[]) as creation of Meta may be expensive
        if (!Loggers.isEnabled(level, loggingFor)) {
            return;
        }
        log(Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now), level, message, params);
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

    @Override public final void error(Throwable t) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isErrorEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        error(meta, t);
    }

    @Override public final void error(Throwable t, String message, Object ... params) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isErrorEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        error(meta, t);
        log(meta, Logger.error, message, params);
    }

    private void error(Meta meta, Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        String stackTrace = stringWriter.toString();
        log(meta, Logger.error, stackTrace);
    }
}
