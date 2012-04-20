package net.ocheyedan.uncial;

import net.ocheyedan.uncial.appender.Appender;

import java.util.Comparator;

/**
 * User: blangel
 * Date: 4/19/12
 * Time: 7:24 AM
 *
 * The {@literal JMX} {@literal MBean} interface to allow users to remotely manage the {@literal uncial} configuration
 * singleton.
 *
 * TODO - modify method signatures (and clearly impls) to actually be useful to remote-jmx (i.e., convert Appender/etc
 * TODO - to String and have the impl resolve from String to Appender/etc).
 */
public interface UncialConfigMBean {

    /**
     * Adds {@code appender} to the set of {@link net.ocheyedan.uncial.appender.Appender} objects which will receive log messages.
     * The format is an interpreted string where the following tokens will be expanded according to the {@link Meta}
     * information provided with the log message:
     * <pre>
     *     %t - thread name
     *     %F - file name; the name of the source file
     *     %C - class name
     *     %M - method name
     *     %L - line number
     *     %l - log level
     *     %d - the date/time at which the logging event occurred
     *     %m - the actual message
     *     %n - new line
     * </pre>
     * Anything else will be printed as is for every log message.
     *
     * @param appender to receive log messages.
     * @param format for which to print log messages.
     */
    void addAppender(Appender appender, String format);

    /**
     * Adds {@code appender} to the set of {@link Appender} objects which will receive log messages.  It will
     * use the default format (i.e., {@link UncialConfig#DEFAULT_APPENDER_FORMAT}).
     * @param appender to receive log messages.
     */
    void addAppender(Appender appender);

    /**
     * Sets {@code format} for the given {@link Appender} {@code forAppender}.  Note, if there was no {@link Appender}
     * before this method call there will be one after (equivalent to calling {@link #addAppender(Appender, String)}
     * @param forAppender for which to set {@code format}
     * @param format for which to print log messages.
     */
    void setFormat(Appender forAppender, String format);

    /**
     * Sets {@code level} as the logging level for any class matching {@code forClass}
     * @param forClass the fully/partially qualified class name for which to assign {@code level}
     * @param level for which to log for classes matching {@code forClass}
     */
    void setLevel(String forClass, String level);

    /**
     * Sets the default level (the logging level for which to log if not specified at the class level).  The default
     * logging level is used when no specific level is set for a class on any of its appenders.
     * @param level to be the default log level.
     */
    void setDefaultLevel(String level);

    /**
     * Sets the level comparator to {@code levelComparator}.  The level comparator is responsible for ordering
     * the levels used within the logging system.  Levels are ordered so that disabling a particular level will disable
     * all levels at and less than that level.  A level is less than another if the {@link java.util.Comparator} returns a negative
     * number when compared to another object.  For instance, for the default comparator, disabling {@link Logger#debug}
     * will disable it as well as {@link Logger#trace} as {@link Logger#trace} is less than it.
     * Note, if one uses other levels besides those referenced within {@link Logger} then one should provide a custom
     * level comparator to ensure predictable results.
     * @param levelComparator which to use as the level comparator.
     */
    void setLevelComparator(Comparator<String> levelComparator);

    /**
     * Sets the logging comparator to {@code loggerComparator}.  The logger comparator is responsible for ordering
     * the loggers' classes used within the logging system.  Loggers' classes are ordered so that setting a particular
     * level for a logger class will apply to related loggers.  For instance, for the default comparator, setting a log
     * level for 'org.apache' will apply to any class whose fully qualified name starts with 'org.apache'.  In the case
     * where a more qualified logger is set then it will be used.
     * For instance if 'org.apache' is set to level xxxx but 'org.apache.commons' is set to yyyy then yyyy will take
     * precedence for any class whose name starts with 'org.apache.commons'.
     * @param loggerComparator which to use as the logging comparator.
     */
    void setLoggerComparator(Comparator<String> loggerComparator);
}
