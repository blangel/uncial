package net.ocheyedan.uncial.caliper.uncial;

import net.ocheyedan.uncial.appender.Appender;

/**
 * User: blangel
 * Date: 4/20/12
 * Time: 3:07 PM
 *
 * Instead of using a PrintStreamAppender, using this no-op as caliper continuously runs out of memory sending over
 * the results if actually logging (as caliper captures that logging and attempts to send as well).
 */
public class NopAppender implements Appender {

    @Override public String getName() {
        return "uncial-nop";
    }
    @Override public void handle(String message) {
        // do nothing with the message
    }

}
