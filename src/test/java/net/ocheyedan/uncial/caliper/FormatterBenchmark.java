package net.ocheyedan.uncial.caliper;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import net.ocheyedan.uncial.Formatter;

import java.util.Date;

/**
 * User: blangel
 * Date: 4/21/12
 * Time: 9:45 PM
 */
public class FormatterBenchmark extends SimpleBenchmark {

    private final Formatter formatter = new Formatter.Uncial();

    public int timeFormatter_standard(int reps) {
        int value = 0;
        for (int i = 0; i < reps; i++) {
            value += String.format("my format %s", "hello!").hashCode();
        }
        return value;
    }

    public int timeFormatter_reuse(int reps) {
        int value = 0;
        for (int i = 0; i < reps; i++) {
            value += formatter.format("my format %s", "hello!").hashCode();
        }
        return value;
    }

    private final String format = "my format %s";
    private final Object[] args = new Object[] { "hello!" };
    public int timeFormatter_custom(int reps) {
        int value = 0;
        for (int i = 0; i < reps; i++) {
            StringBuilder buffer = new StringBuilder();
            char[] chars = format.toCharArray();
            boolean lastWasPercent = false;
            int arg = 0;
            for (char character : chars) {
                if (lastWasPercent) {
                    lastWasPercent = false;
                    switch (character) {
                        case 's':
                            buffer.append(args[arg++]);
                            break;
                        case 'd':
                            buffer.append(args[arg++]);
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
        Runner.main(FormatterBenchmark.class, args);
    }

}
