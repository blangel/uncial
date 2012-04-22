package net.ocheyedan.uncial;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: blangel
 * Date: 4/21/12
 * Time: 9:28 PM
 *
 * A reusable and thread-safe {@link java.util.Formatter}.
 */
public interface Formatter {

    /**
     * Creates a thread-safe {@link StringBuilder} with the ability of resetting its value.
     */
    static final class Buffer extends ThreadLocal<StringBuilder> {
        @Override protected StringBuilder initialValue() {
            return new StringBuilder();
        }
        public void clear() {
            get().setLength(0);
        }
    }

    /**
     * Mimics {@literal SLF4J}'s parameters formatter implementation but expands it with varargs support.
     */
    static final class Slf4j implements Formatter {

        private final Buffer buffer = new Buffer();

        @Override
        public String format(String format, Object ... args) {
            buffer.clear();
            StringBuilder buffer = this.buffer.get();
            char[] chars = format.toCharArray();
            boolean lastWasBracket = false;
            int count = 0;
            for (char character : chars) {
                if (lastWasBracket) {
                    lastWasBracket = false;
                    switch (character) {
                        case '}':
                            buffer.append(String.valueOf(args[count++]));
                            break;
                        case '{':
                            buffer.append('{');
                            lastWasBracket = true;
                            break;
                        default:
                            buffer.append('{');
                            buffer.append(character);
                    }
                } else {
                    if (character == '{') {
                        lastWasBracket = true;
                    } else {
                        buffer.append(character);
                    }
                }
            }
            return buffer.toString();
        }

    }

    /**
     * Wrapper around {@link String#format(String, Object...)} which shares, in  a thread-safe way, both a {@link java.util.Formatter}
     * and {@link StringBuilder} object.
     */
    static final class Uncial implements Formatter {

        private final Buffer buffer = new Buffer();

        private final ThreadLocal<java.util.Formatter> formatter = new ThreadLocal<java.util.Formatter>() {
            @Override protected java.util.Formatter initialValue() {
                return new java.util.Formatter(buffer.get());
            }
        };

        @Override
        public String format(String format, Object ... args) {
            buffer.clear();
            return formatter.get().format(format, args).toString();
        }
    }

    /**
     * A {@link Formatter} like implementation which {@link Appender} objects can use to format their message according
     * to the {@link Uncial} appender format.
     */
    static class Appender {

        private final ThreadLocal<SimpleDateFormat> dateFormatter = new ThreadLocal<SimpleDateFormat>() {
            @Override protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
            }
        };

        private final Buffer buffer = new Buffer();

        public String format(Meta meta, String level, String formattedMessage, String format) {
            buffer.clear();
            StringBuilder buffer = this.buffer.get();
            char[] chars = format.toCharArray();
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
                            buffer.append(level);
                            break;
                        case 'd':
                            buffer.append(dateFormatter.get().format(new Date(meta.invokingEpochTime())));
                            break;
                        case 'm':
                            buffer.append(formattedMessage);
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
            return buffer.toString();
        }

    }

    /**
     * @param format including placeholders (i.e., {@literal %s} in {@link Uncial} implementation and {@literal {}}
     *               in {@link Slf4j} implementation.
     * @param args the arguments to replace into the {@code format}
     * @return a formatted string
     * @see {@link String#format(String, Object...)}
     */
    String format(String format, Object ... args);

}
