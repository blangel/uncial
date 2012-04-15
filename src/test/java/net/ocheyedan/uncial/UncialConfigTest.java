package net.ocheyedan.uncial;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: blangel
 * Date: 4/14/12
 * Time: 8:55 PM
 */
public class UncialConfigTest {

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
    public void defaultLoggerComparator() {
        int result = UncialConfig.DEFAULT_LOGGER_COMPARATOR.compare("org.apache", "org.apache.commons.lang.StringUtils");
        assertTrue(result < 0);

        // TODO - needs to be more complicated as above is fine but need 'org.apache' to not include 'yyy.zzzz' / etc
    }

}
