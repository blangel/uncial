package net.ocheyedan.uncial.appender;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 10:37 AM
 * 
 * An end point for formatted {@link String} messages within the {@literal Uncial} framework.
 */
public interface Appender {

    /**
     * @return a name for the appender
     */
    String getName();

    /**
     * Handle the log {@code message}.  For instance, an appender might write the message to file.
     * @param message to log
     */
    void handle(String message);

    /**
     * Allows for periodic flushing of the underlying stream.
     */
    void flush();

    /**
     * Allows implementations a hook into flushing their stream, if applicable, at JVM shutdown.
     */
    void close();
    
}
