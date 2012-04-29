package org.slf4j.impl;

import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

/**
 * User: blangel
 * Date: 4/29/12
 * Time: 5:18 PM
 *
 * The binding of {@link org.slf4j.MarkerFactory} class with an actual instance of
 * {@link IMarkerFactory} is performed using information returned by this class.
 */
public class StaticMarkerBinder implements MarkerFactoryBinder {

    /**
     * The unique instance of this class.
     */
    public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();

    /**
     * Return the singleton of this class.
     *
     * @return the StaticMarkerBinder singleton
     */
    public static StaticMarkerBinder getSingleton() {
        return SINGLETON;
    }

    final IMarkerFactory markerFactory = new BasicMarkerFactory();

    private StaticMarkerBinder() { }

    /**
     * Currently this method always returns an instance of
     * {@link BasicMarkerFactory}.
     */
    public IMarkerFactory getMarkerFactory() {
        return markerFactory;
    }

    /**
     * Currently, this method returns the class name of
     * {@link BasicMarkerFactory}.
     */
    public String getMarkerFactoryClassStr() {
        return BasicMarkerFactory.class.getName();
    }
}
