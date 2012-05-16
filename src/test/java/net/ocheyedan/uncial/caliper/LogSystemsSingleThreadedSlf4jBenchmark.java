package net.ocheyedan.uncial.caliper;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import net.ocheyedan.uncial.Formatter;
import net.ocheyedan.uncial.Logger;
import net.ocheyedan.uncial.Loggers;
import net.ocheyedan.uncial.UncialConfig;
import net.ocheyedan.uncial.appender.Appender;
import net.ocheyedan.uncial.appender.FileAppender;
import org.slf4j.LoggerFactory;

/**
 * User: blangel
 * Date: 4/25/12
 * Time: 7:50 PM
 *
 * {@link com.google.caliper.Benchmark} to compare logging systems against each other.
 * This is different than {@link LogSystemsBenchmark} in that it configures {@literal Uncial} to be single threaded
 * and use the {@literal slf4j} style formatting.
 */
public class LogSystemsSingleThreadedSlf4jBenchmark extends SimpleBenchmark {

    static {
        if (Boolean.getBoolean("caliper")) {
            System.setProperty("uncial.singleThreaded", "true");
        }
    }

    private static final Appender uncialAppender = new FileAppender("/tmp/uncial-benchmark.log");
    static {
        // configure uncial
        UncialConfig.get().addAppender(uncialAppender, UncialConfig.DEFAULT_APPENDER_FORMAT);
    }

    private final Logger uncialLog = Loggers.get(LogSystemsBenchmark.class, Formatter.Slf4j.class);
    private final org.slf4j.Logger logbackLogger = LoggerFactory.getLogger(LogSystemsBenchmark.class);
    private final org.apache.log4j.Logger log4jLogger = org.apache.log4j.Logger.getLogger(LogSystemsBenchmark.class);

    public void timeUncialLogger_info(int reps) {
        for (int i = 0; i < reps; i++) {
            uncialLog.info("My message");
        }
    }

    public void timeLogbackLogger_info(int reps) {
        for (int i = 0; i < reps; i++) {
            logbackLogger.info("My message");
        }
    }

    public void timeLog4jLogger_info(int reps) {
        for (int i = 0; i < reps; i++) {
            log4jLogger.info("My message");
        }
    }

    public void timeUncialLogger_infoWithOneParam(int reps) {
        for (int i = 0; i < reps; i++) {
            uncialLog.info("My message is {}", "hello!");
        }
    }

    public void timeLogbackLogger_infoWithOneParam(int reps) {
        for (int i = 0; i < reps; i++) {
            logbackLogger.info("My message is {}", "hello!");
        }
    }

    public void timeLog4jLogger_infoWithOneParam(int reps) {
        for (int i = 0; i < reps; i++) {
            log4jLogger.info("My message is " + "hello!");
        }
    }

    public void timeUncialLogger_infoWithManyParams(int reps) {
        for (int i = 0; i < reps; i++) {
            uncialLog.info("My message is {} {} {} (number is {})", "hello", "you", "there", 100);
        }
    }

    public void timeLogbackLogger_infoWithManyParams(int reps) {
        for (int i = 0; i < reps; i++) {
            logbackLogger.info("My message is {} {} {} (number is {})", new Object[] { "hello", "you", "there", 100 });
        }
    }

    public void timeLog4jLogger_infoWithManyParams(int reps) {
        for (int i = 0; i < reps; i++) {
            log4jLogger.info("My message is " + "hello" + "you" + "there" + "(number is " + 100 + ")");
        }
    }

    public static void main(String[] args) throws Exception {
        Runner.main(LogSystemsSingleThreadedSlf4jBenchmark.class, args);
    }
}
