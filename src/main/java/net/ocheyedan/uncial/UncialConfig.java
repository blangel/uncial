package net.ocheyedan.uncial;

import net.ocheyedan.uncial.appender.Appender;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 10:10 AM
 *
 * The singleton, thread-safe, configuration manager.  Configuration changes via {@literal JMX} are routed here.
 */
public final class UncialConfig implements UncialConfigMBean {

    /**
     * An immutable structure containing {@link net.ocheyedan.uncial.appender.Appender} related configuration.
     */
    static final class AppenderConfig {

        private static final ThreadLocal<SimpleDateFormat> dateFormatter = new ThreadLocal<SimpleDateFormat>() {
            @Override protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
            }
        };

        private static final ConcurrentMap<LogEvent, String> formattedCache = new ConcurrentHashMap<LogEvent, String>();

        /**
         * An appender implementation
         */
        final Appender appender;

        /**
         * The format to use when handling log messages on {@link #appender}
         * @see {@link UncialConfig#addAppender(Appender, String)} for a description of the format.
         */
        final String format;

        private AppenderConfig(Appender appender, String format) {
            this.appender = appender;
            this.format = format;
        }

        String format(LogEvent logEvent) {
            if (formattedCache.containsKey(logEvent)) {
                return formattedCache.get(logEvent);
            }
            StringBuilder buffer = new StringBuilder();
            char[] chars = format.toCharArray();
            boolean lastWasPercent = false;
            for (char character : chars) {
                if (lastWasPercent) {
                    lastWasPercent = false;
                    switch (character) {
                        case 't':
                            if (logEvent.meta.invokingThreadName() != null) {
                                buffer.append(logEvent.meta.invokingThreadName());
                            }
                            break;
                        case 'F':
                            if (logEvent.meta.invokingFileName() != null) {
                                buffer.append(logEvent.meta.invokingFileName());
                            }
                            break;
                        case 'C':
                            if (logEvent.meta.invokingClassName() != null) {
                                buffer.append(logEvent.meta.invokingClassName());
                            }
                            break;
                        case 'M':
                            if (logEvent.meta.invokingMethodName() != null) {
                                buffer.append(logEvent.meta.invokingMethodName());
                            }
                            break;
                        case 'L':
                            if (logEvent.meta.invokingLineNumber() != null) {
                                buffer.append(logEvent.meta.invokingLineNumber());
                            }
                            break;
                        case 'l':
                            if (logEvent.level != null) {
                                buffer.append(logEvent.level);
                            }
                            break;
                        case 'd':
                            buffer.append(dateFormatter.get().format(new Date(logEvent.meta.invokingEpochTime())));
                            break;
                        case 'm':
                            if (logEvent.message != null) {
                                buffer.append(logEvent.message);
                            }
                            break;
                        case 'n':
                            buffer.append('\n');
                            break;
                        case '%':
                            buffer.append('%');
                            lastWasPercent = true;
                            break;
                        default:
                            buffer.append('%');
                            buffer.append(character);
                    }
                } else {
                    if (character == '%') {
                        lastWasPercent = true;
                    } else {
                        buffer.append(character);
                    }
                }
            }
            String formatted = buffer.toString();
            formattedCache.put(logEvent, formatted);
            return formatted;
        }
    }

    /**
     * An immutable structure containing {@link Logger} related configuration.
     */
    private static final class LoggerConfig {
        /**
         * The base-name of a logger's class-name. This may not necessarily be the fully qualified name but simply a
         * beginning portion; e.g., 'org.apache' which would apply to all class-names which are less than this value
         * according to the logger comparator.
         * @see {@link UncialConfig#DEFAULT_LOGGER_COMPARATOR} for information about the logger comparator
         */
        private final String className;

        /**
         * The level at which to log for {@linkplain #className} for all comparable levels.
         * @see {@link UncialConfig#DEFAULT_LEVEL_COMPARATOR} for information about comparable levels.
         */
        private final String associatedLevel;

        private LoggerConfig(String className, String associatedLevel) {
            this.className = className;
            this.associatedLevel = associatedLevel;
        }
    }

    /**
     * The default format to use when logging messages to appender objects.
     */
    public static final String DEFAULT_APPENDER_FORMAT = "%d %C [%l] - %m%n";

    /**
     * The default level to use for the {@link #defaultLevel} initial value.
     */
    public static final String DEFAULT_LEVEL = Logger.info;

    /**
     * The default comparator for ordering log levels.  Levels are ordered, by this default comparator, so that disabling
     * a particular level will disable all levels at and less than that level.  That is, disabling {@link Logger#debug}
     * will disable it as well as any value for which the comparator returns a negative number when compared
     * against {@link Logger#debug}
     */
    public static final Comparator<String> DEFAULT_LEVEL_COMPARATOR = new Comparator<String>() {
        @Override public int compare(String left, String right) {
            // the natively supported uncial levels are ordered (descending)
            // trace, debug, info, warn, error
            if (Logger.trace.equals(left)) {
                // either equal to trace or should be negative
                return Math.abs(left.compareTo(right)) * -1;
            } else if (Logger.trace.equals(right)) {
                return 1; // left is not trace from above and so right < left
            } else if (Logger.debug.equals(left)) {
                // either equal to debug (right is definitely not trace from above) or should be negative
                return Math.abs(left.compareTo(right)) * -1;
            } else if (Logger.debug.equals(right)) {
                return 1; // left is not trace/debug from above and so right < left
            } else if (Logger.info.equals(left)) {
                // either equal to info (right is definitely not trace/debug from above) or should be negative
                return Math.abs(left.compareTo(right)) * -1;
            } else if (Logger.info.equals(right)) {
                return 1; // left is not trace/debug/info from above and so right < left
            } else if (Logger.warn.equals(left)) {
                // either equal to warn (right is definitely not trace/debug/info from above) or should be negative
                return Math.abs(left.compareTo(right)) * -1;
            } else if (Logger.warn.equals(right)) {
                return 1; // left is not trace/debug/info/warn from above and so right < left
            } else if (Logger.error.equals(left)) {
                // either equal to error (right is definitely not trace/debug/info/warn from above) or should be negative
                return Math.abs(left.compareTo(right)) * -1;
            } else if (Logger.error.equals(right)) {
                return 1; // left is not trace/debug/info/warn/error from above and so right < left
            }
            // Both left & right are user types, by default user types have highest order so as to never exclude them.
            // Users should implement their own Comparator if this behavior is not desired. Delegate to natural ordering
            return left.compareTo(right);
        }
    };

    /**
     * The default comparator for grouping class names for logging.  Class names are grouped, by this default comparator,
     * such that referring to a base class package (e.g., 'org.apache') will also refer to any class-name whose
     * fully qualified class name starts with the base class package (again, e.g., 'org.apache').
     */
    public static final Comparator<String> DEFAULT_LOGGER_COMPARATOR = new Comparator<String>() {
        @Override public int compare(String left, String right) {
            return left.compareTo(right);
        }
    };

    /**
     * The singleton instance, via the {@literal holder pattern}.
     * @see {@literal http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom}
     */
    private static final class Singleton {
        private static final UncialConfig INSTANCE = new UncialConfig();
    }

    /**
     * @return the singleton/thread-safe instance to use to configure logging.
     */
    public static UncialConfig get() {
        return Singleton.INSTANCE;
    }

    private final ConcurrentMap<String, AppenderConfig> appenderConfigs;

    private final AtomicReference<String> defaultLevel;

    private final ConcurrentMap<String, LoggerConfig> loggerConfigs;

    private final AtomicReference<Comparator<String>> levelComparator;

    private final AtomicReference<Comparator<String>> loggerComparator;

    private final ConcurrentMap<String, Boolean> cacheIsEnabled;

    private UncialConfig() {
        this.appenderConfigs = new ConcurrentHashMap<String, AppenderConfig>(2, 1.0f);
        this.defaultLevel = new AtomicReference<String>(DEFAULT_LEVEL);
        this.loggerConfigs = new ConcurrentHashMap<String, LoggerConfig>(16, 1.0f);
        this.levelComparator = new AtomicReference<Comparator<String>>(DEFAULT_LEVEL_COMPARATOR);
        this.loggerComparator = new AtomicReference<Comparator<String>>(DEFAULT_LOGGER_COMPARATOR);
        this.cacheIsEnabled = new ConcurrentHashMap<String, Boolean>(16, 1.0f);
    }

    /**
     * @param level for which to check if {@code forClass} is enabled
     * @param forClass for which to check if {@code level} is enabled
     * @return true if logging is enabled for {@code forClass} at (or above) level {@code level}.
     */
    boolean isEnabled(String level, Class<?> forClass) {
        if ((level == null) || (forClass == null)) {
            throw new NullPointerException("Level and Class must not be null.");
        }
        String cacheKey = forClass.getName() + "@" + level;
        Boolean cachedResult = cacheIsEnabled.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        if (!loggerConfigs.isEmpty()) {
            // first iteration will simply look up the fully qualified class name directly.  subsequent iterations will
            // look by 'pealing-away' the class name itself and packages from the fully qualified class name
            String forClassName = forClass.getName();
            int lastPackageSplitIndex = forClassName.length();
            while (lastPackageSplitIndex != -1) {
                forClassName = forClassName.substring(0, lastPackageSplitIndex);
                LoggerConfig loggerConfig = loggerConfigs.get(forClassName);
                if (loggerConfig != null) {
                    cachedResult = (getLevelComparator().compare(loggerConfig.associatedLevel, level) <= 0);
                    this.cacheIsEnabled.put(cacheKey, cachedResult);
                    return cachedResult;
                }
                lastPackageSplitIndex = forClassName.lastIndexOf(".");
            }
        }
        cachedResult = (getLevelComparator().compare(getDefaultLevel(), level) <= 0); // not specified; compare against default
        this.cacheIsEnabled.put(cacheKey, cachedResult);
        return cachedResult;
    }

    /**
     * @param whenLogging NOT CURRENTLY USED
     * @return true if when logging {@code whenLogging} the method name is needed for any appender configuration
     */
    boolean needsMethodName(Class<?> whenLogging) {
        return needs("%M", whenLogging);
    }

    /**
     * @param whenLogging NOT CURRENTLY USED
     * @return true if when logging {@code whenLogging} the line number is needed for any appender configuration
     */
    boolean needsLineNumber(Class<?> whenLogging) {
        return needs("%L", whenLogging);
    }

    /**
     * @param whenLogging NOT CURRENTLY USED
     * @return true if when logging {@code whenLogging} the file name is needed for any appender configuration
     */
    boolean needsFileName(Class<?> whenLogging) {
        return needs("%F", whenLogging);
    }

    /**
     * @param format for which to check (the valid formats are those specified in the {@link #addAppender(Appender, String)},
     *               i.e., {@literal %M}.
     * @param whenLogging NOT CURRENTLY USED
     * @return true if when logging {@code whenLogging} the specified {@code format} is needed for any appender configuration
     */
    private boolean needs(String format, Class<?> whenLogging) {
        for (AppenderConfig config : this.appenderConfigs.values()) {
            if (config.format.contains(format)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds {@code appender} to the set of {@link Appender} objects which will receive log messages.  It will
     * use the default format (i.e., {@link #DEFAULT_APPENDER_FORMAT}).
     * @param appender to receive log messages.
     */
    @Override
    public void addAppender(Appender appender) {
        addAppender(appender, DEFAULT_APPENDER_FORMAT);
    }

    /**
     * Adds {@code appender} to the set of {@link Appender} objects which will receive log messages.
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
    @Override
    public void addAppender(Appender appender, String format) {
        if (appender == null) {
            return;
        }
        String appenderFormat = (format == null ? DEFAULT_APPENDER_FORMAT : format);
        String appenderKey = appender.getClass().getName(); // no guarantee Appender#getName() will be unique
        this.appenderConfigs.put(appenderKey, new AppenderConfig(appender, appenderFormat));
    }

    /**
     * Sets {@code format} for the given {@link Appender} {@code forAppender}.  Note, if there was no {@link Appender}
     * before this method call there will be one after (equivalent to calling {@link #addAppender(Appender, String)}
     * @param forAppender for which to set {@code format}
     * @param format for which to print log messages.
     */
    @Override
    public void setFormat(Appender forAppender, String format) {
        addAppender(forAppender, format);
    }

    /**
     * Calls {@link #setLevel(String, String)} using the {@link Class#getName()} value of {@code forClass}
     * @param forClass the class for which to assign {@code level}
     * @param level for which to log for {@code forClass}
     */
    public void setLevel(Class<?> forClass, String level) {
        if (forClass == null) {
            return;
        }
        setLevel(forClass.getName(), level);
    }

    /**
     * Sets {@code level} as the logging level for any class matching {@code forClass}
     * @param forClass the fully/partially qualified class name for which to assign {@code level}
     * @param level for which to log for classes matching {@code forClass}
     */
    @Override
    public void setLevel(String forClass, String level) {
        if (forClass == null) {
            return;
        }
        if (level == null) {
            this.loggerConfigs.remove(forClass);
        } else {
            this.loggerConfigs.put(forClass, new LoggerConfig(forClass, level));
        }
        this.cacheIsEnabled.clear(); // TODO - can this be cleared at a more granular level?
    }

    /**
     * Sets the default level (the logging level for which to log if not specified at the class level).  The default
     * logging level is used when no specific level is set for a class on any of its appenders.
     * @param level to be the default log level.
     */
    @Override
    public void setDefaultLevel(String level) {
        if ((level == null) || level.isEmpty()) {
            return;
        }
        this.defaultLevel.set(level);
    }

    /**
     * The default log level is used as the logging level for any class which doesn't specify its own logging level.
     * @return the current default log level
     */
    public String getDefaultLevel() {
        return this.defaultLevel.get();
    }

    /**
     * Sets the level comparator to {@code levelComparator}.  The level comparator is responsible for ordering
     * the levels used within the logging system.  Levels are ordered so that disabling a particular level will disable
     * all levels at and less than that level.  A level is less than another if the {@link Comparator} returns a negative
     * number when compared to another object.  For instance, for the default comparator ({@link #DEFAULT_LEVEL_COMPARATOR}),
     * disabling {@link Logger#debug} will disable it as well as {@link Logger#trace} as {@link Logger#trace} is less
     * than it.
     * Note, if one uses other levels besides those referenced within {@link Logger} then one should provide a custom
     * level comparator to ensure predictable results.
     * @param levelComparator which to use as the level comparator.
     */
    @Override
    public void setLevelComparator(Comparator<String> levelComparator) {
        if (levelComparator == null) {
            return;
        }
        this.levelComparator.set(levelComparator);
    }

    /**
     * @return the current level comparator.
     */
    public Comparator<String> getLevelComparator() {
        return this.levelComparator.get();
    }

    /**
     * Sets the logging comparator to {@code loggerComparator}.  The logger comparator is responsible for ordering
     * the loggers' classes used within the logging system.  Loggers' classes are ordered so that setting a particular
     * level for a logger class will apply to related loggers.  For instance, for the default comparator
     * ({@link #DEFAULT_LOGGER_COMPARATOR}), setting a log level for 'org.apache' will apply to any class whose fully
     * qualified name starts with 'org.apache'.  In the case where a more qualified logger is set then it will be used.
     * For instance if 'org.apache' is set to level xxxx but 'org.apache.commons' is set to yyyy then yyyy will take
     * precedence for any class whose name starts with 'org.apache.commons'.
     * @param loggerComparator which to use as the logging comparator.
     */
    @Override
    public void setLoggerComparator(Comparator<String> loggerComparator) {
        if (loggerComparator == null) {
            return;
        }
        this.loggerComparator.set(loggerComparator);
    }

    /**
     * @return the current logger comparator.
     */
    public Comparator<String> getLoggerComparator() {
        return this.loggerComparator.get();
    }

    Collection<AppenderConfig> getAppenderConfigs() {
        return this.appenderConfigs.values();
    }
}
