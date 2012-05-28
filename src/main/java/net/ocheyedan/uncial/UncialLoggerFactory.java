package net.ocheyedan.uncial;

/**
 * User: blangel
 * Date: 5/28/12
 * Time: 9:54 AM
 *
 * The default {@link LoggerFactory} implementation.
 */
final class UncialLoggerFactory implements LoggerFactory {

    @Override public Logger construct(Class<?> forClass, Formatter formatter) {
        return new Uncial(forClass, formatter, Loggers.appenderExecutor);
    }
}
