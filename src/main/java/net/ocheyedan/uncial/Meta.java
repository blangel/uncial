package net.ocheyedan.uncial;

import java.io.Serializable;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 7:54 AM
 * 
 * Provides access to the meta information about a logging invocation; including the invoking {@link Class}, the 
 * associated method name and line number if desired.
 */
public interface Meta extends Serializable {

    Class<?> invokingClass();

    String invokingClassName();
    
    String invokingMethodName();
    
    Integer invokingLineNumber();
    
    String invokingFileName();
    
}
