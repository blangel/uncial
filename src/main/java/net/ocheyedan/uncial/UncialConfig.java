package net.ocheyedan.uncial;

import net.ocheyedan.uncial.appender.Appender;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
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

        private static final Formatter.Appender formatter = new Formatter.Appender();

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

        String format(final Meta meta, final String level, final String formattedMessage) {
            if ((meta == null) || (level == null) || (formattedMessage == null)) {
                throw new NullPointerException("The meta/level/message all cannot be null.");
            }
            return formatter.format(meta, level, formattedMessage, format);
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

        /**
         * The logger's parent, if any.  A logger has a parent if there is another logger whose {@link #className} is
         * the base of the logger's own {@link #className}.
         */
        private final AtomicReference<LoggerConfig> parent;

        private final ConcurrentMap<String, Boolean> isEnabledCache;

        private LoggerConfig(String className, String associatedLevel) {
            this.className = className;
            this.associatedLevel = associatedLevel;
            this.parent = new AtomicReference<LoggerConfig>();
            this.isEnabledCache = new ConcurrentHashMap<String, Boolean>(5, 1.0f);
        }

        boolean isEnabled(String level) {
            if (isEnabledCache.containsKey(level)) {
                return isEnabledCache.get(level);
            }
            boolean isEnabled;
            if (associatedLevel != null) {
                isEnabled = (UncialConfig.get().getLevelComparator().compare(associatedLevel, level) <= 0);
            } else if (parent.get() != null) {
                isEnabled = parent.get().isEnabled(level);
            } else {
                UncialConfig config = UncialConfig.get();
                isEnabled = (config.getLevelComparator().compare(config.getDefaultLevel(), level) <= 0);
            }
            isEnabledCache.put(level, isEnabled);
            return isEnabled;
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

    private final AtomicBoolean needsMethod = new AtomicBoolean(false);

    private final AtomicBoolean needsLine = new AtomicBoolean(false);

    private final AtomicBoolean needsFile = new AtomicBoolean(false);

    private UncialConfig() {
        this.appenderConfigs = new ConcurrentHashMap<String, AppenderConfig>(2, 1.0f);
        this.defaultLevel = new AtomicReference<String>(DEFAULT_LEVEL);
        this.loggerConfigs = new ConcurrentHashMap<String, LoggerConfig>(16, 1.0f);
        this.levelComparator = new AtomicReference<Comparator<String>>(DEFAULT_LEVEL_COMPARATOR);
        this.loggerComparator = new AtomicReference<Comparator<String>>(DEFAULT_LOGGER_COMPARATOR);
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
        setLevelIfNotPresent(forClass.getName());
        LoggerConfig loggerConfig = loggerConfigs.get(forClass.getName());
        return loggerConfig.isEnabled(level);
    }

    /**
     * @param whenLogging NOT CURRENTLY USED
     * @return true if when logging {@code whenLogging} the method name is needed for any appender configuration
     */
    boolean needsMethodName(Class<?> whenLogging) {
        return needsMethod.get();
    }

    /**
     * @param whenLogging NOT CURRENTLY USED
     * @return true if when logging {@code whenLogging} the line number is needed for any appender configuration
     */
    boolean needsLineNumber(Class<?> whenLogging) {
        return needsLine.get();
    }

    /**
     * @param whenLogging NOT CURRENTLY USED
     * @return true if when logging {@code whenLogging} the file name is needed for any appender configuration
     */
    boolean needsFileName(Class<?> whenLogging) {
        return needsFile.get();
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
    public synchronized void addAppender(Appender appender, String format) {
        if (appender == null) {
            return;
        }
        String appenderFormat = (format == null ? DEFAULT_APPENDER_FORMAT : format);
        String appenderKey = appender.getClass().getName(); // no guarantee Appender#getName() will be unique
        AppenderConfig old = this.appenderConfigs.put(appenderKey, new AppenderConfig(appender, appenderFormat));
        if (appenderFormat.contains("%M")) {
            needsMethod.set(true);
        } else if ((old != null) && old.format.contains("%M")) {
            needsMethod.set(false);
        }
        if (appenderFormat.contains("%L")) {
            needsLine.set(true);
        } else if ((old != null) && old.format.contains("%L")) {
            needsLine.set(false);
        }
        if (appenderFormat.contains("%F")) {
            needsFile.set(true);
        } else if ((old != null) && old.format.contains("%F")) {
            needsFile.set(false);
        }
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
    public synchronized void setLevel(String forClass, String level) {
        if (forClass == null) {
            return;
        }
        LoggerConfig loggerConfig = new LoggerConfig(forClass, level);
        LoggerConfig old = this.loggerConfigs.put(forClass, loggerConfig);
        adjustParent(loggerConfig, old);
        adjustChildren(loggerConfig);
    }

    private void adjustParent(LoggerConfig loggerConfig, LoggerConfig old) {
        // if there was LoggerConfig, replacing it doesn't change the parent, so use it
        if (old != null) {
            loggerConfig.parent.set(old.parent.get());
            return;
        }
        LoggerConfig parent = null;
        for (LoggerConfig possibleParent : loggerConfigs.values()) {
            if ((possibleParent != loggerConfig) && loggerConfig.className.startsWith(possibleParent.className)) {
                // potential match; only use if longer than any existing match
                if ((parent == null) || (possibleParent.className.length() >= parent.className.length())) {
                    parent = possibleParent;
                }
            }
        }
        loggerConfig.parent.set(parent);
    }

    private void adjustChildren(LoggerConfig loggerConfig) {
        for (LoggerConfig possibleChild : loggerConfigs.values()) {
            if ((possibleChild != loggerConfig) && possibleChild.className.startsWith(loggerConfig.className)) {
                // potential child, only use if loggerConfig is more of a match than any existing parent
                if ((possibleChild.parent.get() == null)
                        || (loggerConfig.className.length() >= possibleChild.parent.get().className.length())) {
                    possibleChild.parent.set(loggerConfig);
                    possibleChild.isEnabledCache.clear();
                }
            }
        }
    }

    /**
     * Called whenever a logger is created.  Pre-setting the config allows for fast 'isEnabled' lookup.
     * @param forClass the class name of the logger created.
     */
    void setLevelIfNotPresent(String forClass) {
        if ((forClass == null) || this.loggerConfigs.containsKey(forClass)) {
            return;
        }
        setLevel(forClass, null);
    }

    /**
     * Sets the default level (the logging level for which to log if not specified at the class level).  The default
     * logging level is used when no specific level is set for a class on any of its appenders.
     * @param level to be the default log level.
     */
    @Override
    public synchronized void setDefaultLevel(String level) {
        if ((level == null) || level.isEmpty()) {
            return;
        }
        this.defaultLevel.set(level);
        // invalidate isEnabled caches
        for (LoggerConfig loggerConfig : loggerConfigs.values()) {
            loggerConfig.isEnabledCache.clear();
        }
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
