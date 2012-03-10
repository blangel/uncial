package net.ocheyedan.uncial;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 7:08 AM
 * 
 * The definition of the configurable elements within the {@literal Uncial} framework.
 */
public interface Configuration {

    /**
     * The mutable configurable elements within the {@literal Uncial} framework.  As opposed to the elements
     * within {@link Configuration}, these elements may change while the system is running.
     */
    static interface Mutable {

        void setLogLevelComparator(Comparable<String> comparator);

    }
    
    void setMaxQueueDepth();
    
}
