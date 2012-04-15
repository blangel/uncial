package net.ocheyedan.uncial;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 8:38 AM
 * 
 * A complete implementation of {@link Meta}, however, field values may be null.
 */
final class MetaComplete implements Meta {

    private static final long serialVersionUID = 3212825114504703641L;

    private final Class<?> invokingClass;
    
    private final String invokingMethodName;
    
    private final Integer invokingLineNumber;
    
    private final String invokingFileName;

    private final String threadName;

    private final long epochTime;

    MetaComplete(Class<?> invokingClass, String invokingMethodName, Integer invokingLineNumber, String invokingFileName,
                 String threadName, long epochTime) {
        this.invokingClass = invokingClass;
        this.invokingMethodName = invokingMethodName;
        this.invokingLineNumber = invokingLineNumber;
        this.invokingFileName = invokingFileName;
        this.threadName = threadName;
        this.epochTime = epochTime;
    }

    @Override public Class<?> invokingClass() {
        return this.invokingClass;
    }

    @Override public String invokingClassName() {
        return (this.invokingClass == null ? null : this.invokingClass.getName());
    }

    @Override public String invokingMethodName() {
        return this.invokingMethodName;
    }

    @Override public Integer invokingLineNumber() {
        return this.invokingLineNumber;
    }

    @Override public String invokingFileName() {
        return this.invokingFileName;
    }

    @Override public String invokingThreadName() {
        return threadName;
    }

    @Override public long invokingEpochTime() {
        return epochTime;
    }
}
