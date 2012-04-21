package net.ocheyedan.uncial.caliper;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import net.ocheyedan.uncial.Log;
import net.ocheyedan.uncial.Logger;
import net.ocheyedan.uncial.Loggers;
import net.ocheyedan.uncial.UncialConfig;
import net.ocheyedan.uncial.appender.PrintStreamAppender;
import net.ocheyedan.uncial.caliper.uncial.NopAppender;

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
        UncialConfig.get().addAppender(new NopAppender());
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

    public void timeLogger_traceWithParams(int reps) {
        for (int i = 0; i < reps; i++) {
            log.trace("My message is %s and at %d", "Hello!", System.currentTimeMillis());
        }
    }

    public void timeLogger_debugWithParams(int reps) {
        for (int i = 0; i < reps; i++) {
            log.debug("My message is %s and at %d", "Hello!", System.currentTimeMillis());
        }
    }

    public void timeLogger_infoWithParams(int reps) {
        for (int i = 0; i < reps; i++) {
            log.info("My message is %s and at %d", "Hello!", System.currentTimeMillis());
        }
    }

    public void timeLogger_warnWithParams(int reps) {
        for (int i = 0; i < reps; i++) {
            log.warn("My message is %s and at %d", "Hello!", System.currentTimeMillis());
        }
    }

    public void timeLogger_errorWithParams(int reps) {
        for (int i = 0; i < reps; i++) {
            log.error("My message is %s and at %d", "Hello!", System.currentTimeMillis());
        }
    }

//    public void timeLogger_errorWithExceptionAndParams(int reps) {
//        for (int i = 0; i < reps; i++) {
//            log.error(exception, "My message is %s and at %d", "Hello!", System.currentTimeMillis());
//        }
//    }

    public void timeLogger_userWithParams(int reps) {
        for (int i = 0; i < reps; i++) {
            log.log("my level", "My message is %s and at %d", "Hello!", System.currentTimeMillis());
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

    public void timeLog_traceWithParams(int reps) {
        for (int i = 0; i < reps; i++) {
            Log.trace("My message is %s and at %d", "Hello!", System.currentTimeMillis());
        }
    }

    public void timeLog_debugWithParams(int reps) {
        for (int i = 0; i < reps; i++) {
            Log.debug("My message is %s and at %d", "Hello!", System.currentTimeMillis());
        }
    }

    public void timeLog_infoWithParams(int reps) {
        for (int i = 0; i < reps; i++) {
            Log.info("My message is %s and at %d", "Hello!", System.currentTimeMillis());
        }
    }

    public void timeLog_warnWithParams(int reps) {
        for (int i = 0; i < reps; i++) {
            Log.warn("My message is %s and at %d", "Hello!", System.currentTimeMillis());
        }
    }

    public void timeLog_errorWithParams(int reps) {
        for (int i = 0; i < reps; i++) {
            Log.error("My message is %s and at %d", "Hello!", System.currentTimeMillis());
        }
    }

//    public void timeLog_errorWithExceptionAndParams(int reps) {
//        for (int i = 0; i < reps; i++) {
//            Log.error(exception, "My message is %s and at %d", "Hello!", System.currentTimeMillis());
//        }
//    }

    public void timeLog_userWithParams(int reps) {
        for (int i = 0; i < reps; i++) {
            Log.log("my level", "My message is %s and at %d", "Hello!", System.currentTimeMillis());
        }
    }

    public static void main(String[] args) throws Exception {
        // run the benchmark
        Runner.main(UncialBenchmark.class, args);
    }

}
