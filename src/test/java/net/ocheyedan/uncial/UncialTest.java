package net.ocheyedan.uncial;

import net.ocheyedan.uncial.appender.FileAppender;
import net.ocheyedan.uncial.appender.PrintStreamAppender;
import org.junit.Test;

/**
 * User: blangel
 * Date: 4/19/12
 * Time: 9:53 PM
 */
public class UncialTest {

    @Test
    public void log() throws InterruptedException {
        Logger logger = Loggers.get(UncialTest.class);
        logger.warn("Hello there %s", UncialTest.class.getSimpleName());
        UncialConfig.get().addAppender(new PrintStreamAppender());
        logger.warn("Now, hello there %s!", UncialTest.class.getSimpleName());
        UncialConfig.get().addAppender(new FileAppender("/tmp/uncial-test.log"));
        logger.warn("Again, hello there %s!", UncialTest.class.getSimpleName());
        Thread.sleep(1000); // give some time to logging thread to do its work
    }

}
