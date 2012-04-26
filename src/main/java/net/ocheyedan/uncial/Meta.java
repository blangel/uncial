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

    /**
     * Default implementation of the {@link Meta} interface.
     */
    static class Default implements Meta, Serializable {

        private static final long serialVersionUID = 3212825114504703641L;

        private final Class<?> invokingClass;

        private final String invokingMethodName;

        private final Integer invokingLineNumber;

        private final String invokingFileName;

        private final String threadName;

        private final long epochTime;

        Default(Class<?> invokingClass, String invokingMethodName, Integer invokingLineNumber, String invokingFileName,
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

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if ((o == null) || (getClass() != o.getClass())) {
                return false;
            }
            Default that = (Default) o;
            if (epochTime != that.epochTime) {
                return false;
            }
            if (invokingClass != null ? !invokingClass.equals(that.invokingClass) : that.invokingClass != null) {
                return false;
            }
            if (invokingFileName != null ? !invokingFileName
                    .equals(that.invokingFileName) : that.invokingFileName != null) {
                return false;
            }
            if (invokingLineNumber != null ? !invokingLineNumber
                    .equals(that.invokingLineNumber) : that.invokingLineNumber != null) {
                return false;
            }
            if (invokingMethodName != null ? !invokingMethodName
                    .equals(that.invokingMethodName) : that.invokingMethodName != null) {
                return false;
            }
            return (threadName == null ? (that.threadName == null) : threadName.equals(that.threadName));
        }

        @Override
        public int hashCode() {
            int result = invokingClass != null ? invokingClass.hashCode() : 0;
            result = 31 * result + (invokingMethodName != null ? invokingMethodName.hashCode() : 0);
            result = 31 * result + (invokingLineNumber != null ? invokingLineNumber.hashCode() : 0);
            result = 31 * result + (invokingFileName != null ? invokingFileName.hashCode() : 0);
            result = 31 * result + (threadName != null ? threadName.hashCode() : 0);
            result = 31 * result + (int) (epochTime ^ (epochTime >>> 32));
            return result;
        }
    }

    /**
     * @return the {@link Class} object from which the logging event originated.
     */
    Class<?> invokingClass();

    /**
     * @return the {@link Class#getName()} value of the result of executing {@link #invokingClass()}
     */
    String invokingClassName();

    /**
     * @return the method name from which the logging event originated.
     */
    String invokingMethodName();

    /**
     * @return the line number within the source file from which the logging event originated.
     */
    Integer invokingLineNumber();

    /**
     * @return the name of the source file from which the logging event originated.
     */
    String invokingFileName();

    /**
     * @return the {@link Thread#getName()} of the {@link Thread} from which the logging event originated (the current
     *         thread is that returned by {@link Thread#currentThread()} at point of logging).
     */
    String invokingThreadName();

    /**
     * @return the {@literal unix-epoch} time at which the logging event happened.
     */
    long invokingEpochTime();
}
