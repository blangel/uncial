package net.ocheyedan.uncial.caliper;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import net.ocheyedan.uncial.Log;
import net.ocheyedan.uncial.Logger;
import net.ocheyedan.uncial.Loggers;
import net.ocheyedan.uncial.UncialConfig;
import net.ocheyedan.uncial.appender.Appender;

/**
 * User: blangel
 * Date: 4/20/12
 * Time: 12:31 PM
 *
 * {@link com.google.caliper.Benchmark} for comparing {@literal Uncial} appender formats against each other.
 */
public class UncialWithExpensiveFormatBenchmark extends SimpleBenchmark {

    private static final String ExpensiveFormat = "%d %t %F %C#%M @ %L [%l] - %m%n";

    private final Appender appender = new Appender() {
        @Override public String getName() {
            return "no-op";
        }
        @Override public void handle(String message) {
            // nothing
        }
        @Override public void close() { }
    };

    private final Logger log = Loggers.get(UncialWithExpensiveFormatBenchmark.class);

    public void timeLogger_info(int reps) {
        UncialConfig.get().addAppender(appender, UncialConfig.DEFAULT_APPENDER_FORMAT);
        for (int i = 0; i < reps; i++) {
            log.info("My message is %s", "Hello!");
        }
    }

    public void timeLogger_infoWithExpensiveFormat(int reps) {
        UncialConfig.get().addAppender(appender, ExpensiveFormat);
        for (int i = 0; i < reps; i++) {
            log.info("My message is %s", "Hello!");
        }
    }

    public void timeLog_info(int reps) {
        UncialConfig.get().addAppender(appender, UncialConfig.DEFAULT_APPENDER_FORMAT);
        for (int i = 0; i < reps; i++) {
            Log.info("My message is %s", "Hello!");
        }
    }

    public void timeLog_infoWithWithExpensiveFormat(int reps) {
        UncialConfig.get().addAppender(appender, ExpensiveFormat);
        for (int i = 0; i < reps; i++) {
            Log.info("My message is %s", "Hello!");
        }
    }

    public static void main(String[] args) throws Exception {
        Runner.main(UncialWithExpensiveFormatBenchmark.class, args);
    }

}
