package net.ocheyedan.uncial;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * User: blangel
 * Date: 4/14/12
 * Time: 8:55 PM
 */
public class UncialConfigTest {

    @Test
    public void defaultLoggerComparator() {
        int result = UncialConfig.DEFAULT_LOGGER_COMPARATOR.compare("org.apache", "org.apache.commons.lang.StringUtils");
        assertTrue(result < 0);

        // TODO - needs to be more complicated as above is fine but need 'org.apache' to not include 'yyy.zzzz' / etc
    }

}
