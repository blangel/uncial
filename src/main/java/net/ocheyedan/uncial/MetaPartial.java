package net.ocheyedan.uncial;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 7:57 AM
 * 
 * An adapter for {@link Meta} implementations to provide partial implementations.
 */
@SuppressWarnings("serial")
abstract class MetaPartial implements Meta {

    @Override public Class<?> invokingClass() {
        return null;
    }

    @Override public String invokingClassName() {
        return null;
    }

    @Override public String invokingMethodName() {
        return null;
    }

    @Override public Integer invokingLineNumber() {
        return null;
    }

    @Override public String invokingFileName() {
        return null;
    }
}
