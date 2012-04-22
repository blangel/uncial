package net.ocheyedan.uncial;

import net.ocheyedan.uncial.appender.Appender;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * User: blangel
 * Date: 4/14/12
 * Time: 8:55 PM
 */
public class UncialConfigTest {

    private static class NestedClass { }

    @Before
    public void init() throws NoSuchFieldException, IllegalAccessException {
        UncialConfig config = UncialConfig.get();
        Field appenderConfigsField = UncialConfig.class.getDeclaredField("appenderConfigs");
        appenderConfigsField.setAccessible(true);
        ((ConcurrentMap) appenderConfigsField.get(config)).clear();
        Field loggerConfigsField = UncialConfig.class.getDeclaredField("loggerConfigs");
        loggerConfigsField.setAccessible(true);
        ((ConcurrentMap) loggerConfigsField.get(config)).clear();
        Field needsMethodField = UncialConfig.class.getDeclaredField("needsMethod");
        needsMethodField.setAccessible(true);
        ((AtomicBoolean) needsMethodField.get(config)).set(false);
        Field needsLineField = UncialConfig.class.getDeclaredField("needsLine");
        needsLineField.setAccessible(true);
        ((AtomicBoolean) needsLineField.get(config)).set(false);
        Field needsFileField = UncialConfig.class.getDeclaredField("needsFile");
        needsFileField.setAccessible(true);
        ((AtomicBoolean) needsFileField.get(config)).set(false);
    }

    @Test
    public void defaultLevelComparator() {
        // left is trace
        int result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.trace, Logger.trace);
        assertEquals(0, result);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.trace, Logger.debug);
        assertTrue(result < 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.trace, Logger.info);
        assertTrue(result < 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.trace, Logger.warn);
        assertTrue(result < 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.trace, Logger.error);
        assertTrue(result < 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.trace, "user type");
        assertTrue(result < 0);
        // left is debug
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.debug, Logger.trace);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.debug, Logger.debug);
        assertEquals(0, result);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.debug, Logger.info);
        assertTrue(result < 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.debug, Logger.warn);
        assertTrue(result < 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.debug, Logger.error);
        assertTrue(result < 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.debug, "user type");
        assertTrue(result < 0);
        // left is info
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.info, Logger.trace);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.info, Logger.debug);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.info, Logger.info);
        assertEquals(0, result);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.info, Logger.warn);
        assertTrue(result < 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.info, Logger.error);
        assertTrue(result < 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.info, "user type");
        assertTrue(result < 0);
        // left is warn
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.warn, Logger.trace);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.warn, Logger.debug);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.warn, Logger.info);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.warn, Logger.warn);
        assertEquals(0, result);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.warn, Logger.error);
        assertTrue(result < 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.warn, "user type");
        assertTrue(result < 0);
        // left is error
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.error, Logger.trace);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.error, Logger.debug);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.error, Logger.info);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.error, Logger.warn);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.error, Logger.error);
        assertEquals(0, result);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare(Logger.error, "user type");
        assertTrue(result < 0);
        // right has been checked above for everything but user types
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare("user type", Logger.trace);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare("user type", Logger.debug);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare("user type", Logger.info);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare("user type", Logger.warn);
        assertTrue(result > 0);
        result = UncialConfig.DEFAULT_LEVEL_COMPARATOR.compare("user type", Logger.error);
        assertTrue(result > 0);
    }

    @Test
    public void isEnabled() {
        UncialConfig uncialConfig = UncialConfig.get();
        Loggers.get(UncialConfigTest.class);
        Loggers.get(UncialConfig.class);
        Loggers.get(NestedClass.class);
        // the default is info, check above and below
        assertFalse(uncialConfig.isEnabled(Logger.trace, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.debug, UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled(Logger.info, UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled(Logger.warn, UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled(Logger.error, UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled("user type", UncialConfigTest.class));

        // set default of UncialConfigTest to be trace
        uncialConfig.setLevel(UncialConfigTest.class.getName(), Logger.trace);
        assertTrue(uncialConfig.isEnabled(Logger.trace, UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled(Logger.debug, UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled(Logger.info, UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled(Logger.warn, UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled(Logger.error, UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled("user type", UncialConfigTest.class));
        // set default of UncialConfigTest to be error
        uncialConfig.setLevel(UncialConfigTest.class.getName(), Logger.error);
        assertFalse(uncialConfig.isEnabled(Logger.trace, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.debug, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.info, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.warn, UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled(Logger.error, UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled("user type", UncialConfigTest.class));
        // set default of UncialConfigTest to be a specific user-type
        uncialConfig.setLevel(UncialConfigTest.class.getName(), "user type");
        assertFalse(uncialConfig.isEnabled(Logger.trace, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.debug, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.info, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.warn, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.error, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled("different user type", UncialConfigTest.class)); // user-types by default are compared via String.compareTo
        assertTrue(uncialConfig.isEnabled("user type", UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled("z different user type", UncialConfigTest.class)); // user-types by default are compared via String.compareTo

        // now test setting the level at a package granularity
        uncialConfig.setLevel("net.ocheyedan", Logger.trace);
        // more specific setting should still hold
        assertFalse(uncialConfig.isEnabled(Logger.trace, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.debug, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.info, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.warn, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.error, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled("different user type", UncialConfigTest.class)); // user-types by default are compared via String.compareTo
        assertTrue(uncialConfig.isEnabled("user type", UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled("z different user type", UncialConfigTest.class)); // user-types by default are compared via String.compareTo
        // but other classes should be at the Logger.trace level now
        assertTrue(uncialConfig.isEnabled(Logger.trace, UncialConfig.class));
        assertTrue(uncialConfig.isEnabled(Logger.debug, UncialConfig.class));
        assertTrue(uncialConfig.isEnabled(Logger.info, UncialConfig.class));
        assertTrue(uncialConfig.isEnabled(Logger.warn, UncialConfig.class));
        assertTrue(uncialConfig.isEnabled(Logger.error, UncialConfig.class));
        assertTrue(uncialConfig.isEnabled("user type", UncialConfig.class));
        // up the level for a package above to see that this level 'masks' the previous one set
        uncialConfig.setLevel("net.ocheyedan", Logger.warn);
        assertFalse(uncialConfig.isEnabled(Logger.trace, UncialConfig.class));
        assertFalse(uncialConfig.isEnabled(Logger.debug, UncialConfig.class));
        assertFalse(uncialConfig.isEnabled(Logger.info, UncialConfig.class));
        assertTrue(uncialConfig.isEnabled(Logger.warn, UncialConfig.class));
        assertTrue(uncialConfig.isEnabled(Logger.error, UncialConfig.class));
        assertTrue(uncialConfig.isEnabled("user type", UncialConfig.class));
        // but still doesn't override the explicitly set level
        assertFalse(uncialConfig.isEnabled(Logger.trace, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.debug, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.info, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.warn, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.error, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled("different user type", UncialConfigTest.class)); // user-types by default are compared via String.compareTo
        assertTrue(uncialConfig.isEnabled("user type", UncialConfigTest.class));
        assertTrue(uncialConfig.isEnabled("z different user type", UncialConfigTest.class)); // user-types by default are compared via String.compareTo

        // test that NestedClass is modified as setting its container means the container becomes its parent
        assertFalse(uncialConfig.isEnabled(Logger.trace, NestedClass.class));
        assertFalse(uncialConfig.isEnabled(Logger.debug, NestedClass.class));
        assertFalse(uncialConfig.isEnabled(Logger.info, NestedClass.class));
        assertFalse(uncialConfig.isEnabled(Logger.warn, NestedClass.class));
        assertFalse(uncialConfig.isEnabled(Logger.error, NestedClass.class));
        assertTrue(uncialConfig.isEnabled("user type", NestedClass.class));
        // set the NestedClass explicitly and ensure its setting doesn't interfere with its container or its related package's level
        uncialConfig.setLevel(NestedClass.class.getName(), Logger.debug);
        assertFalse(uncialConfig.isEnabled(Logger.trace, NestedClass.class));
        assertTrue(uncialConfig.isEnabled(Logger.debug, NestedClass.class));
        assertTrue(uncialConfig.isEnabled(Logger.info, NestedClass.class));
        assertTrue(uncialConfig.isEnabled(Logger.warn, NestedClass.class));
        assertTrue(uncialConfig.isEnabled(Logger.error, NestedClass.class));
        assertTrue(uncialConfig.isEnabled("user type", NestedClass.class));
        // ensure package level is fine
        assertFalse(uncialConfig.isEnabled(Logger.trace, UncialConfig.class));
        assertFalse(uncialConfig.isEnabled(Logger.debug, UncialConfig.class));
        assertFalse(uncialConfig.isEnabled(Logger.info, UncialConfig.class));
        assertTrue(uncialConfig.isEnabled(Logger.warn, UncialConfig.class));
        assertTrue(uncialConfig.isEnabled(Logger.error, UncialConfig.class));
        assertTrue(uncialConfig.isEnabled("user type", UncialConfig.class));
        // and the container
        assertFalse(uncialConfig.isEnabled(Logger.trace, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.debug, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.info, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.warn, UncialConfigTest.class));
        assertFalse(uncialConfig.isEnabled(Logger.error, UncialConfigTest.class));
    }

    @Test
    public void needsFileName() {
        Appender mock = new Appender() {
            @Override public String getName() {
                return null;
            }
            @Override public void handle(String logEvent) {
            }
        };
        UncialConfig uncialConfig = UncialConfig.get();
        // with no appenders
        assertFalse(uncialConfig.needsFileName(UncialConfig.class));
        // with appender without file-name need
        uncialConfig.addAppender(mock);
        assertFalse(uncialConfig.needsFileName(UncialConfig.class));
        // add an appender which needs file-name
        Appender mock1 = new Appender() {
            @Override public String getName() {
                return null;
            }
            @Override public void handle(String logEvent) {
            }
        };
        uncialConfig.addAppender(mock1, "%F %m%n");
        assertTrue(uncialConfig.needsFileName(UncialConfig.class));
        // now, override with one which doesn't need file-name
        uncialConfig.addAppender(mock1, "%T %m%n");
        assertFalse(uncialConfig.needsFileName(UncialConfig.class));
    }

    @Test
    public void needsLineNumber() {
        Appender mock = new Appender() {
            @Override public String getName() {
                return null;
            }
            @Override public void handle(String logEvent) {
            }
        };
        UncialConfig uncialConfig = UncialConfig.get();
        // with no appenders
        assertFalse(uncialConfig.needsLineNumber(UncialConfig.class));
        // with appender without line-number need
        uncialConfig.addAppender(mock);
        assertFalse(uncialConfig.needsLineNumber(UncialConfig.class));
        // add an appender which needs line-number
        Appender mock1 = new Appender() {
            @Override public String getName() {
                return null;
            }
            @Override public void handle(String logEvent) {
            }
        };
        uncialConfig.addAppender(mock1, "%L %m%n");
        assertTrue(uncialConfig.needsLineNumber(UncialConfig.class));
        // now, override with one which doesn't need line-number
        uncialConfig.addAppender(mock1, "%T %m%n");
        assertFalse(uncialConfig.needsLineNumber(UncialConfig.class));
    }

    @Test
    public void needsMethodName() {
        Appender mock = new Appender() {
            @Override public String getName() {
                return null;
            }
            @Override public void handle(String logEvent) {
            }
        };
        UncialConfig uncialConfig = UncialConfig.get();
        // with no appenders
        assertFalse(uncialConfig.needsMethodName(UncialConfig.class));
        // with appender without method-name need
        uncialConfig.addAppender(mock);
        assertFalse(uncialConfig.needsMethodName(UncialConfig.class));
        // add an appender which needs method-name
        Appender mock1 = new Appender() {
            @Override public String getName() {
                return null;
            }
            @Override public void handle(String logEvent) {
            }
        };
        uncialConfig.addAppender(mock1, "%M %m%n");
        assertTrue(uncialConfig.needsMethodName(UncialConfig.class));
        // now, override with one which doesn't need file-name
        uncialConfig.addAppender(mock1, "%T %m%n");
        assertFalse(uncialConfig.needsMethodName(UncialConfig.class));
    }

}
