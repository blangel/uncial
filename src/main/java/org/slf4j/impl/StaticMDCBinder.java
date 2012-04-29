package org.slf4j.impl;

import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;

/**
 * User: blangel
 * Date: 4/29/12
 * Time: 5:16 PM
 */
public class StaticMDCBinder {

    /**
     * The unique instance of this class.
     */
    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    /**
     * Return the singleton of this class.
     *
     * @return the StaticMDCBinder singleton
     */
    public static StaticMDCBinder getSingleton() {
        return SINGLETON;
    }

    private StaticMDCBinder() { }

    /**
     * Currently this method always returns an instance of
     * {@link StaticMDCBinder}.
     */
    public MDCAdapter getMDCA() {
        return new NOPMDCAdapter();
    }

    public String  getMDCAdapterClassStr() {
        return NOPMDCAdapter.class.getName();
    }

}
