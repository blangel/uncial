package net.ocheyedan.uncial.caliper;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import net.ocheyedan.uncial.Logger;
import net.ocheyedan.uncial.Loggers;
import net.ocheyedan.uncial.UncialConfig;
import net.ocheyedan.uncial.appender.Appender;
import net.ocheyedan.uncial.appender.FileAppender;
import org.slf4j.LoggerFactory;

/**
 * User: blangel
 * Date: 4/21/12
 * Time: 11:36 AM
 *
 * Note, the log4j.properties and logback.xml files must be changed to the proper appender format before running
 * this benchmark.
 */
public class LogSystemsWithExpensiveFormatBenchmark extends SimpleBenchmark {

    private static final Appender uncialAppender = new FileAppender("/tmp/uncial-benchmark.log");
    static {
        // configure uncial
        UncialConfig.get().addAppender(uncialAppender, "%d %t %F %C#%M @ %L [%l] - %m%n");
    }

    private final Logger uncialLog = Loggers.get(LogSystemsWithExpensiveFormatBenchmark.class);
    private final org.slf4j.Logger logbackLogger = LoggerFactory.getLogger(LogSystemsWithExpensiveFormatBenchmark.class);
    private final org.apache.log4j.Logger log4jLogger = org.apache.log4j.Logger.getLogger(LogSystemsWithExpensiveFormatBenchmark.class);

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
            uncialLog.info("My message is %s", "hello!");
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
            uncialLog.info("My message is %s %s %s (number is %d)", "hello", "you", "there", 100);
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
        Runner.main(LogSystemsWithExpensiveFormatBenchmark.class, args);
    }
}
