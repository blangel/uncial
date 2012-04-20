package net.ocheyedan.uncial;

import java.io.Serializable;

/**
 * User: blangel
 * Date: 3/10/12
 * Time: 10:29 AM
 * 
 * A logging event to be passed through an {@link net.ocheyedan.uncial.appender.Appender} pipeline.
 */
public final class LogEvent implements Serializable {

    private static final long serialVersionUID = -7805117218973896860L;

    public final Meta meta;
 
    public final String level;
    
    public final String message;

    public LogEvent(Meta meta, String level, String message) {
        this.meta = meta;
        this.level = level;
        this.message = message;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        LogEvent logEvent = (LogEvent) o;
        if (level == null ? (logEvent.level != null) : !level.equals(logEvent.level)) {
            return false;
        }
        if (message == null ? (logEvent.message != null) : !message.equals(logEvent.message)) {
            return false;
        }
        return (meta == null ? logEvent.meta == null : meta.equals(logEvent.meta));
    }

    @Override public int hashCode() {
        int result = meta != null ? meta.hashCode() : 0;
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
