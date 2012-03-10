package net.ocheyedan.uncial;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 10:37 AM
 * 
 * An end point for {@link LogEvent} objects within the {@literal Uncial} framework.
 */
public interface Appender {
    
    String getName();

    void handle(LogEvent logEvent);
    
}
