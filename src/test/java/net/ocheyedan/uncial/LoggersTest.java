package net.ocheyedan.uncial;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;

import static junit.framework.Assert.*;

/**
 * User: blangel
 * Date: 4/19/12
 * Time: 5:08 PM
 */
public class LoggersTest {

    @Before
    public void init() throws NoSuchFieldException, IllegalAccessException {
        UncialConfig config = UncialConfig.get();
        Field appenderConfigsField = UncialConfig.class.getDeclaredField("appenderConfigs");
        appenderConfigsField.setAccessible(true);
        ((ConcurrentMap) appenderConfigsField.get(config)).clear();
        Field loggerConfigsField = UncialConfig.class.getDeclaredField("loggerConfigs");
        loggerConfigsField.setAccessible(true);
        ((ConcurrentMap) loggerConfigsField.get(config)).clear();
    }

    @Test
    public void invokingLogClass() {
        // simulate the four deep
        assertSame(LoggersTest.class, getInvokingLogClass());
    }
    private Class<?> getInvokingLogClass() {
        return Loggers.invokingLogClass();
    }

    @Test
    public void meta() {
        try {
            Loggers.meta(null, null, null, null, null, 0);
            fail("Expecting an IllegalArgumentException as the loggingFor Class<?> object must not be null.");
        } catch (NullPointerException npe) {
            // expected
        }
        // get meta without need for creation of an exception
        long now = System.currentTimeMillis();
        Meta meta = Loggers.meta(LoggersTest.class, null, null, null, Thread.currentThread().getName(), now);
        assertEquals(LoggersTest.class, meta.invokingClass());
        assertEquals(LoggersTest.class.getName(), meta.invokingClassName());
        assertEquals(now, meta.invokingEpochTime());
        assertEquals(Thread.currentThread().getName(), meta.invokingThreadName());
        assertNull(meta.invokingMethodName());
        assertNull(meta.invokingLineNumber());
        assertNull(meta.invokingFileName());
        // set need for file/method/line
        UncialConfig.get().addAppender(new Appender() {
            @Override public String getName() {
                return null;
            }
            @Override public void handle(LogEvent logEvent) { }
        }, "%L"); // one is sufficient
        meta = Loggers.meta(LoggersTest.class, null, null, null, Thread.currentThread().getName(), now);
        assertEquals(LoggersTest.class, meta.invokingClass());
        assertEquals(LoggersTest.class.getName(), meta.invokingClassName());
        assertNotNull(meta.invokingClassName());
        assertEquals(now, meta.invokingEpochTime());
        assertEquals(Thread.currentThread().getName(), meta.invokingThreadName());
        assertEquals("invoke0", meta.invokingMethodName()); // will be from NativeMethodAccessoImpl as uncial skips itself including this test case
        assertEquals(-2, (int) meta.invokingLineNumber());
        assertEquals("NativeMethodAccessorImpl.java", meta.invokingFileName());
    }

}
