package net.ocheyedan.uncial.appender;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 10:37 AM
 * 
 * An end point for formatted {@link String} messages within the {@literal Uncial} framework.
 */
public interface Appender {
    
    String getName();

    void handle(String message);
    
}
