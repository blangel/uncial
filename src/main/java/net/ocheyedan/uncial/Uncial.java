package net.ocheyedan.uncial;

import org.slf4j.Marker;

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
public final class Uncial implements Logger {

    private final Class<?> loggingFor;

    private final Formatter formatter;

    private final Distributor distributor;

    Uncial(Class<?> loggingFor, Formatter formatter, Distributor distributor) {
        this.loggingFor = loggingFor;
        this.formatter = formatter;
        this.distributor = distributor;
    }

    @Override public void log(Meta meta, String level, String message, Object ... params) {
        _log(loggingFor, formatter, distributor, meta, System.currentTimeMillis(), level, message, params);
    }

    @Override public void log(String level, String message, Object ... params) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), level, message, params);
    }

    @SuppressWarnings("overrides") // doesn't seem to suppress, need to give javac "-Xlint:-overrides" (in ply, -Pcompiler.warnings=-overrides)
    @Override public final void trace(String message, Object ... params) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.trace, message, params);
    }

    @SuppressWarnings("overrides") // doesn't seem to suppress, need to give javac "-Xlint:-overrides" (in ply, -Pcompiler.warnings=-overrides)
    @Override public final void debug(String message, Object ... params) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.debug, message, params);
    }

    @SuppressWarnings("overrides") // doesn't seem to suppress, need to give javac "-Xlint:-overrides" (in ply, -Pcompiler.warnings=-overrides)
    @Override public final void info(String message, Object ... params) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.info, message, params);
    }

    @SuppressWarnings("overrides") // doesn't seem to suppress, need to give javac "-Xlint:-overrides" (in ply, -Pcompiler.warnings=-overrides)
    @Override public final void warn(String message, Object ... params) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.warn, message, params);
    }

    @SuppressWarnings("overrides") // doesn't seem to suppress, need to give javac "-Xlint:-overrides" (in ply, -Pcompiler.warnings=-overrides)
    @Override public final void error(String message, Object ... params) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.error, message, params);
    }

    @Override public final void error(Throwable t) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isErrorEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        exception(meta, t, now, Logger.error);
    }

    @Override public final void error(Throwable t, String message, Object ... params) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isErrorEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        exception(meta, t, now, Logger.error);
        _log(loggingFor, formatter, distributor, meta, now, Logger.error, message, params);
    }

    private void exception(Meta meta, Throwable t, long when, String level) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        String stackTrace = stringWriter.toString();
        _log(loggingFor, formatter, distributor, meta, when, level, stackTrace);
    }

    private static void _log(Class<?> loggingFor, net.ocheyedan.uncial.Formatter formatter, Distributor distributor,
                             Meta meta, long when, String level, String message, Object ... params) {
        if (!Loggers.isEnabled(level, loggingFor)) {
            return;
        }
        if (meta == null) {
            meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), when);
        }
        // this must be logged, so create formatted message now (as {@code params} may be mutable and modified by user
        // after method return).
        String formattedMessage = formatter.format(message, params);
        // create serializable and place in log-queue
        distributor.distribute(meta, level, formattedMessage);
    }

    // org.slf4j.Logger implementations

    @Override public String getName() {
        return loggingFor.getName();
    }

    @Override public boolean isTraceEnabled() {
        return Loggers.isTraceEnabled(loggingFor);
    }

    @Override public void trace(String msg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.trace, msg);
    }

    @Override public void trace(String format, Object arg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.trace, format, arg);
    }

    @Override public void trace(String format, Object arg1, Object arg2) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.trace, format, arg1, arg2);
    }

    @Override public void trace(String msg, Throwable t) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isTraceEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        exception(meta, t, now, Logger.trace);
        _log(loggingFor, formatter, distributor, meta, now, Logger.trace, msg);
    }

    @Override public boolean isTraceEnabled(Marker marker) {
        return Loggers.isTraceEnabled(loggingFor);
    }

    @Override public void trace(Marker marker, String msg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.trace, msg);
    }

    @Override public void trace(Marker marker, String format, Object arg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.trace, format, arg);
    }

    @Override public void trace(Marker marker, String format, Object arg1, Object arg2) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.trace, format, arg1, arg2);
    }

    @Override public void trace(Marker marker, String format, Object[] argArray) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.trace, format, argArray);
    }

    @Override public void trace(Marker marker, String msg, Throwable t) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isTraceEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        exception(meta, t, now, Logger.trace);
        _log(loggingFor, formatter, distributor, meta, now, Logger.trace, msg);
    }

    @Override public boolean isDebugEnabled() {
        return Loggers.isDebugEnabled(loggingFor);
    }

    @Override public void debug(String msg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.debug, msg);
    }

    @Override public void debug(String format, Object arg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.debug, format, arg);
    }

    @Override public void debug(String format, Object arg1, Object arg2) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.debug, format, arg1, arg2);
    }

    @Override public void debug(String msg, Throwable t) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isDebugEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        exception(meta, t, now, Logger.debug);
        _log(loggingFor, formatter, distributor, meta, now, Logger.debug, msg);
    }

    @Override public boolean isDebugEnabled(Marker marker) {
        return Loggers.isDebugEnabled(loggingFor);
    }

    @Override public void debug(Marker marker, String msg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.debug, msg);
    }

    @Override public void debug(Marker marker, String format, Object arg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.debug, format, arg);
    }

    @Override public void debug(Marker marker, String format, Object arg1, Object arg2) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.debug, format, arg1, arg2);
    }

    @Override public void debug(Marker marker, String format, Object[] argArray) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.debug, format, argArray);
    }

    @Override public void debug(Marker marker, String msg, Throwable t) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isDebugEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        exception(meta, t, now, Logger.debug);
        _log(loggingFor, formatter, distributor, meta, now, Logger.debug, msg);
    }

    @Override public boolean isInfoEnabled() {
        return Loggers.isInfoEnabled(loggingFor);
    }

    @Override public void info(String msg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.info, msg);
    }

    @Override public void info(String format, Object arg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.info, format, arg);
    }

    @Override public void info(String format, Object arg1, Object arg2) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.info, format, arg1, arg2);
    }

    @Override public void info(String msg, Throwable t) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isInfoEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        exception(meta, t, now, Logger.info);
        _log(loggingFor, formatter, distributor, meta, now, Logger.info, msg);
    }

    @Override public boolean isInfoEnabled(Marker marker) {
        return Loggers.isInfoEnabled(loggingFor);
    }

    @Override public void info(Marker marker, String msg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.info, msg);
    }

    @Override public void info(Marker marker, String format, Object arg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.info, format, arg);
    }

    @Override public void info(Marker marker, String format, Object arg1, Object arg2) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.info, format, arg1, arg2);
    }

    @Override public void info(Marker marker, String format, Object[] argArray) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.info, format, argArray);
    }

    @Override public void info(Marker marker, String msg, Throwable t) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isInfoEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        exception(meta, t, now, Logger.info);
        _log(loggingFor, formatter, distributor, meta, now, Logger.info, msg);
    }

    @Override public boolean isWarnEnabled() {
        return Loggers.isWarnEnabled(loggingFor);
    }

    @Override public void warn(String msg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.warn, msg);
    }

    @Override public void warn(String format, Object arg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.warn, format, arg);
    }

    @Override public void warn(String format, Object arg1, Object arg2) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.warn, format, arg1, arg2);
    }

    @Override public void warn(String msg, Throwable t) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isWarnEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        exception(meta, t, now, Logger.warn);
        _log(loggingFor, formatter, distributor, meta, now, Logger.warn, msg);
    }

    @Override public boolean isWarnEnabled(Marker marker) {
        return Loggers.isWarnEnabled(loggingFor);
    }

    @Override public void warn(Marker marker, String msg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.warn, msg);
    }

    @Override public void warn(Marker marker, String format, Object arg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.warn, format, arg);
    }

    @Override public void warn(Marker marker, String format, Object arg1, Object arg2) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.warn, format, arg1, arg2);
    }

    @Override public void warn(Marker marker, String format, Object[] argArray) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.warn, format, argArray);
    }

    @Override public void warn(Marker marker, String msg, Throwable t) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isWarnEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        exception(meta, t, now, Logger.warn);
        _log(loggingFor, formatter, distributor, meta, now, Logger.warn, msg);
    }

    @Override public boolean isErrorEnabled() {
        return Loggers.isErrorEnabled(loggingFor);
    }

    @Override public void error(String msg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.error, msg);
    }

    @Override public void error(String format, Object arg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.error, format, arg);
    }

    @Override public void error(String format, Object arg1, Object arg2) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.error, format, arg1, arg2);
    }

    @Override public void error(String msg, Throwable t) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isErrorEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        exception(meta, t, now, Logger.error);
        _log(loggingFor, formatter, distributor, meta, now, Logger.error, msg);
    }

    @Override public boolean isErrorEnabled(Marker marker) {
        return Loggers.isErrorEnabled(loggingFor);
    }

    @Override public void error(Marker marker, String msg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.error, msg);
    }

    @Override public void error(Marker marker, String format, Object arg) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.error, format, arg);
    }

    @Override public void error(Marker marker, String format, Object arg1, Object arg2) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.error, format, arg1, arg2);
    }

    @Override public void error(Marker marker, String format, Object[] argArray) {
        _log(loggingFor, formatter, distributor, null, System.currentTimeMillis(), Logger.error, format, argArray);
    }

    @Override public void error(Marker marker, String msg, Throwable t) {
        long now = System.currentTimeMillis();
        // check level before logging as creation of error statement may be expensive
        if (!Loggers.isErrorEnabled(loggingFor)) {
            return;
        }
        // cannot use the Throwable directly as it doesn't contain the method/line/etc from which the log call originated
        Meta meta = Loggers.meta(loggingFor, null, null, null, Thread.currentThread().getName(), now);
        exception(meta, t, now, Logger.error);
        _log(loggingFor, formatter, distributor, meta, now, Logger.error, msg);
    }
}
