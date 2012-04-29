package org.slf4j.impl;

import net.ocheyedan.uncial.Loggers;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * User: blangel
 * Date: 4/29/12
 * Time: 5:11 PM
 *
 * UncialLoggerFactory is an implementation of {@link ILoggerFactory} returning the
 * appropriately named {@link net.ocheyedan.uncial.Uncial} logger instance.
 */
public class UncialLoggerFactory implements ILoggerFactory {

    @Override public Logger getLogger(String name) {
        try {
            return Loggers.get(Class.forName(name));
        } catch (ClassNotFoundException cnfe) {
            return Loggers.get(Object.class);
        }
    }
}
