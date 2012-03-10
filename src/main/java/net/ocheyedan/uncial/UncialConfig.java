package net.ocheyedan.uncial;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 10:10 AM
 *
 * The singleton, thread-safe, configuration manager.  Configuration changes via {@literal JMX} are routed here.
 * All {@link Configuration} implementations are routed here as well.
 */
final class UncialConfig {

    static final Comparator<String> defaultLevelComparator = new Comparator<String>() {
        @Override public int compare(String left, String right) {
            return left.compareTo(right);
        }
    };

    static interface AppenderPattern {
        boolean needsClassName();
        boolean needsMethodName();
        boolean needsLineNumber();
        boolean needsFileName();
    }
    
    static final class AppenderConfig {
        final AppenderPattern pattern;
        AppenderConfig(AppenderPattern pattern) {
            this.pattern = pattern;
        }
    }

    // TODO - don't hold on to loggers by class but by some identifier. the identifier will be specified in the config
    // TODO - and allow for chaining like behavior without the 'additivity' oddity.  More like regex, appenders
    // TODO - specify a pattern for which they will log. also that same pattern is used to configure logger levels
    
    final ConcurrentMap<Class<?>, Logger> loggers;

    final ConcurrentMap<Class<?>, Comparator<String>> classLevelComparators;
    
    final ConcurrentMap<Class<?>, String> enabledLevels;

    final ConcurrentMap<Class<?>, >

    final AtomicReference<Appender> defaultAppender;
    
    final ConcurrentMap<Appender, AppenderConfig> appenderConfig;

    final ConcurrentMap<Class<?>, Appender> appenders;
    
    UncialConfig() {
        this.loggers = new ConcurrentHashMap<Class<?>, Logger>();
        this.classLevelComparators = new ConcurrentHashMap<Class<?>, Comparator<String>>();
        this.enabledLevels = new ConcurrentHashMap<Class<?>, String>();
        this.defaultAppender = new AtomicReference<Appender>();
        this.appenderConfig = new ConcurrentHashMap<Appender, AppenderConfig>();
        this.appenders = new ConcurrentHashMap<Class<?>, Appender>();
    }
    
    Logger get(Class<?> forClass) {
        return loggers.get(forClass);
    }

    boolean isEnabled(String level, Class<?> forClass) {
        return false; // TODO
    }

    boolean needsClassName(Class<?> whenLogging) {
           
    }
    
    boolean needsMethodName(Class<?> whenLogging) {
        
    }
    
    boolean needsLineNumber(Class<?> whenLogging) {
        
    }
    
    boolean needsFileName(Class<?> whenLogging) {
        
    }

}
