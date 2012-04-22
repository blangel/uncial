package net.ocheyedan.uncial.caliper;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import net.ocheyedan.uncial.*;
import net.ocheyedan.uncial.appender.Appender;
import net.ocheyedan.uncial.appender.FileAppender;
import net.ocheyedan.uncial.caliper.uncial.NopAppender;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: blangel
 * Date: 4/21/12
 * Time: 1:31 PM
 */
public class AppenderFormatImplBenchmark extends SimpleBenchmark {

    private static final String appenderFormat = "%d %t %F %C#%M @ %L [%l] - %m%n";

    private static final Meta meta = new Meta() {
        private static final long serialVersionUID = 2374571977173759630L;
        @Override public Class<?> invokingClass() {
            return AppenderFormatImplBenchmark.class;
        }
        @Override public String invokingClassName() {
            return AppenderFormatImplBenchmark.class.getName();
        }
        @Override public String invokingMethodName() {
            return "invoke";
        }

        @Override public Integer invokingLineNumber() {
            return 12;
        }

        @Override public String invokingFileName() {
            return AppenderFormatImplBenchmark.class.getSimpleName() + ".java";
        }

        @Override public String invokingThreadName() {
            return Thread.currentThread().getName();
        }
        @Override public long invokingEpochTime() {
            return System.currentTimeMillis();
        }
    };
    private static final String level = Logger.info;
    private static final String message = "Hello there!";

    private static final ThreadLocal<SimpleDateFormat> dateFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        }
    };

    public int timeLogger_regex(int reps) {
        int value = 0;
        for (int i = 0; i < reps; i++) {
            String formatted = appenderFormat.replaceAll("%t", meta.invokingThreadName());
            formatted = formatted.replaceAll("%F", meta.invokingFileName());
            formatted = formatted.replaceAll("%C", meta.invokingClassName());
            formatted = formatted.replaceAll("%M", meta.invokingMethodName());
            formatted = formatted.replaceAll("%L", String.valueOf(meta.invokingLineNumber()));
            formatted = formatted.replaceAll("%l", level);
            formatted = formatted.replaceAll("%d", dateFormatter.get().format(
                    new Date(meta.invokingEpochTime())));
            formatted = formatted.replaceAll("%m", message);
            value += formatted.replaceAll("%n", "\n").hashCode();
        }
        return value;
    }

    public int timeLogger_manual(int reps) {
        int value = 0;
        for (int i = 0; i < reps; i++) {
            StringBuilder buffer = new StringBuilder();
            char[] chars = appenderFormat.toCharArray();
            boolean lastWasPercent = false;
            for (char character : chars) {
                if (lastWasPercent) {
                    lastWasPercent = false;
                    switch (character) {
                        case 't':
                            buffer.append(meta.invokingThreadName());
                            break;
                        case 'F':
                            buffer.append(meta.invokingFileName());
                            break;
                        case 'C':
                            buffer.append(meta.invokingClassName());
                            break;
                        case 'M':
                            buffer.append(meta.invokingMethodName());
                            break;
                        case 'L':
                            buffer.append(meta.invokingLineNumber());
                            break;
                        case 'l':
                            buffer.append(level);
                            break;
                        case 'd':
                            buffer.append(dateFormatter.get().format(new Date(meta.invokingEpochTime())));
                            break;
                        case 'm':
                            buffer.append(message);
                            break;
                        case 'n':
                            buffer.append('\n');
                            break;
                        case '%':
                            buffer.append('%');
                            lastWasPercent = true;
                            break;
                        default:
                            buffer.append('%');
                            buffer.append(character);
                    }
                } else {
                    if (character == '%') {
                        lastWasPercent = true;
                    } else {
                        buffer.append(character);
                    }
                }
            }
            value += buffer.toString().hashCode();
        }
        return value;
    }

    public int timeLogger_regexWithNullCheck(int reps) {
        int value = 0;
        for (int i = 0; i < reps; i++) {
            String formatted = (meta.invokingThreadName() == null ? appenderFormat : appenderFormat.replaceAll("%t", meta.invokingThreadName()));
            formatted = (meta.invokingFileName() == null ? formatted : formatted.replaceAll("%F", meta.invokingFileName()));
            formatted = (meta.invokingClassName() == null ? formatted : formatted.replaceAll("%C", meta.invokingClassName()));
            formatted = (meta.invokingMethodName() == null ? formatted : formatted.replaceAll("%M", meta.invokingMethodName()));
            formatted = (meta.invokingLineNumber() == null ? formatted : formatted.replaceAll("%L", String.valueOf(meta.invokingLineNumber())));
            formatted = (level == null ? formatted : formatted.replaceAll("%l", level));
            formatted = formatted.replaceAll("%d", dateFormatter.get().format(
                    new Date(meta.invokingEpochTime())));
            formatted = (message == null ? formatted : formatted.replaceAll("%m", message));
            value += formatted.replaceAll("%n", "\n").hashCode();
        }
        return value;
    }

    public int timeLogger_manualWithNullCheck(int reps) {
        int value = 0;
        for (int i = 0; i < reps; i++) {
            StringBuilder buffer = new StringBuilder();
            char[] chars = appenderFormat.toCharArray();
            boolean lastWasPercent = false;
            for (char character : chars) {
                if (lastWasPercent) {
                    lastWasPercent = false;
                    switch (character) {
                        case 't':
                            if (meta.invokingThreadName() != null) {
                                buffer.append(meta.invokingThreadName());
                            }
                            break;
                        case 'F':
                            if (meta.invokingFileName() != null) {
                                buffer.append(meta.invokingFileName());
                            }
                            break;
                        case 'C':
                            if (meta.invokingClassName() != null) {
                                buffer.append(meta.invokingClassName());
                            }
                            break;
                        case 'M':
                            if (meta.invokingMethodName() != null) {
                                buffer.append(meta.invokingMethodName());
                            }
                            break;
                        case 'L':
                            if (meta.invokingLineNumber() != null) {
                                buffer.append(meta.invokingLineNumber());
                            }
                            break;
                        case 'l':
                            if (level != null) {
                                buffer.append(level);
                            }
                            break;
                        case 'd':
                            buffer.append(dateFormatter.get().format(new Date(meta.invokingEpochTime())));
                            break;
                        case 'm':
                            if (message != null) {
                                buffer.append(message);
                            }
                            break;
                        case 'n':
                            buffer.append('\n');
                            break;
                        case '%':
                            buffer.append('%');
                            lastWasPercent = true;
                            break;
                        default:
                            buffer.append('%');
                            buffer.append(character);
                    }
                } else {
                    if (character == '%') {
                        lastWasPercent = true;
                    } else {
                        buffer.append(character);
                    }
                }
            }
            value += buffer.toString().hashCode();
        }
        return value;
    }

    public static void main(String[] args) throws Exception {
        Runner.main(AppenderFormatImplBenchmark.class, args);
    }
}
