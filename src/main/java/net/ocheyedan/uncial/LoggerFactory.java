package net.ocheyedan.uncial;

/**
 * User: blangel
 * Date: 5/28/12
 * Time: 9:52 AM
 *
 * Used by {@link Loggers} to construct instances of the {@link Logger} implementation.
 */
public interface LoggerFactory {

    /**
     * @param forClass the {@link Class} for which to log
     * @param formatter the {@link Formatter} to use when formatting the log messages.
     * @return an instance of {@link Logger} {@code forClass} with {@code formatter}
     */
    Logger construct(Class<?> forClass, Formatter formatter);

}
