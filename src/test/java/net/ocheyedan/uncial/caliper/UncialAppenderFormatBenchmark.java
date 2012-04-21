package net.ocheyedan.uncial.caliper;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import net.ocheyedan.uncial.Log;
import net.ocheyedan.uncial.Logger;
import net.ocheyedan.uncial.Loggers;
import net.ocheyedan.uncial.UncialConfig;
import net.ocheyedan.uncial.appender.Appender;
import net.ocheyedan.uncial.appender.PrintStreamAppender;
import net.ocheyedan.uncial.caliper.uncial.NopAppender;
import org.junit.Test;

/**
 * User: blangel
 * Date: 4/20/12
 * Time: 12:31 PM
 *
 * {@link com.google.caliper.Benchmark} for comparing {@literal Uncial} appender formats against each other.
 */
public class UncialAppenderFormatBenchmark extends SimpleBenchmark {

    private static final String STFormat = "%d %t %F %C#%M @ %L [%l] - %m%n";

    private final Appender appender = new NopAppender();

    private final Logger log = Loggers.get(UncialAppenderFormatBenchmark.class);

    public void timeLogger_info(int reps) {
        UncialConfig.get().addAppender(appender, UncialConfig.DEFAULT_APPENDER_FORMAT);
        for (int i = 0; i < reps; i++) {
            log.info("My message is %s", "Hello!");
        }
    }

    public void timeLogger_infoWithSTFormat(int reps) {
        UncialConfig.get().addAppender(appender, STFormat);
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

    public void timeLog_infoWithSTFormat(int reps) {
        UncialConfig.get().addAppender(appender, STFormat);
        for (int i = 0; i < reps; i++) {
            Log.info("My message is %s", "Hello!");
        }
    }

    public static void main(String[] args) throws Exception {
        // run the benchmark
        Runner.main(UncialAppenderFormatBenchmark.class, args);
    }

}
