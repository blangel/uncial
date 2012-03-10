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

    MetaComplete(Class<?> invokingClass, String invokingMethodName, Integer invokingLineNumber, String invokingFileName) {
        this.invokingClass = invokingClass;
        this.invokingMethodName = invokingMethodName;
        this.invokingLineNumber = invokingLineNumber;
        this.invokingFileName = invokingFileName;
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
    
}
