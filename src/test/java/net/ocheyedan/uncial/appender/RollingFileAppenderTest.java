package net.ocheyedan.uncial.appender;

import org.junit.Ignore;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.fail;
import static junit.framework.Assert.format;

/**
 * User: blangel
 * Date: 4/27/12
 * Time: 6:43 PM
 */
public class RollingFileAppenderTest {

    @Test
    public void timedIllegalArgumentException() {
        try {
            new RollingFileAppender("/tmp/rolling.log", 1, TimeUnit.NANOSECONDS);
            fail("Expecting an IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }try {
            new RollingFileAppender("/tmp/rolling.log", 1, TimeUnit.MICROSECONDS);
            fail("Expecting an IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        try {
            new RollingFileAppender("/tmp/rolling.log", 1, TimeUnit.MILLISECONDS);
            fail("Expecting an IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        try {
            new RollingFileAppender("/tmp/rolling.log", 1, TimeUnit.SECONDS);
            fail("Expecting an IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    @Test @Ignore
    public void timed() throws InterruptedException {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy_HH-mm");
        RollingFileAppender appender = new RollingFileAppender("/tmp/rolling-timed.log", 1, TimeUnit.MINUTES);
        long twoMinuesInFuture = System.currentTimeMillis() + (1000 * 60 * 2);
        while (System.currentTimeMillis() < twoMinuesInFuture) {
            appender.handle(String.format("Hello you @ %s%n", formatter.format(new Date(System.currentTimeMillis()))));
            TimeUnit.SECONDS.sleep(10);
        }
        appender.close();
    }

    @Test @Ignore
    public void sized() throws InterruptedException {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss_SSS");
        RollingFileAppender appender = new RollingFileAppender("/tmp/rolling-sized.log", 10, SizeUnit.KILOBYTES);
        long twoMinuesInFuture = System.currentTimeMillis() + (1000 * 60 * 1);
        while (System.currentTimeMillis() < twoMinuesInFuture) {
            appender.handle(String.format("Hello you @ %s%n", formatter.format(new Date(System.currentTimeMillis()))));
        }
        appender.close();
    }

}
