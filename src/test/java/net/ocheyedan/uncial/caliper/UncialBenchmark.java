package net.ocheyedan.uncial.caliper;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import net.ocheyedan.uncial.Log;
import net.ocheyedan.uncial.Logger;
import net.ocheyedan.uncial.Loggers;
import net.ocheyedan.uncial.UncialConfig;
import net.ocheyedan.uncial.appender.Appender;
import net.ocheyedan.uncial.appender.ConsoleAppender;

/**
 * User: blangel
 * Date: 4/20/12
 * Time: 11:54 AM
 *
 * {@link com.google.caliper.Benchmark} for {@literal Uncial} specific tests.  For instance, testing {@literal Uncial}
 * instance logging versus {@literal Uncial} static logging.
 */
public class UncialBenchmark extends SimpleBenchmark {

    static {
        // set a console appender
        UncialConfig.get().addAppender(new Appender() {
            @Override public String getName() {
                return "no-op";
            }
            @Override public void handle(String message) {
                // nothing
            }
            @Override public void close() { }
        });
    }

    private final Logger log = Loggers.get(UncialBenchmark.class);

    private final Exception exception = new Exception();

    public void timeLogger_trace(int reps) {
        for (int i = 0; i < reps; i++) {
            log.trace("My message");
        }
    }

    public void timeLogger_debug(int reps) {
        for (int i = 0; i < reps; i++) {
            log.debug("My message");
        }
    }

    public void timeLogger_info(int reps) {
        for (int i = 0; i < reps; i++) {
            log.info("My message");
        }
    }

    public void timeLogger_warn(int reps) {
        for (int i = 0; i < reps; i++) {
            log.warn("My message");
        }
    }

    public void timeLogger_error(int reps) {
        for (int i = 0; i < reps; i++) {
            log.error("My message");
        }
    }

//    public void timeLogger_errorWithException(int reps) {
//        for (int i = 0; i < reps; i++) {
//            log.error(exception, "My message");
//        }
//    }

    public void timeLogger_user(int reps) {
        for (int i = 0; i < reps; i++) {
            log.log("my level", "My message");
        }
    }

    public void timeLog_trace(int reps) {
        for (int i = 0; i < reps; i++) {
            Log.trace("My message");
        }
    }

    public void timeLog_debug(int reps) {
        for (int i = 0; i < reps; i++) {
            Log.debug("My message");
        }
    }

    public void timeLog_info(int reps) {
        for (int i = 0; i < reps; i++) {
            Log.info("My message");
        }
    }

    public void timeLog_warn(int reps) {
        for (int i = 0; i < reps; i++) {
            Log.warn("My message");
        }
    }

    public void timeLog_error(int reps) {
        for (int i = 0; i < reps; i++) {
            Log.error("My message");
        }
    }

//    public void timeLog_errorWithException(int reps) {
//        for (int i = 0; i < reps; i++) {
//            Log.error(exception, "My message");
//        }
//    }

    public void timeLog_user(int reps) {
        for (int i = 0; i < reps; i++) {
            Log.log("my level", "My message");
        }
    }

    public static void main(String[] args) throws Exception {
        Runner.main(UncialBenchmark.class, args);
    }

}
