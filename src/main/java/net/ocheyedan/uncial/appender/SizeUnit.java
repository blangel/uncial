package net.ocheyedan.uncial.appender;

/**
 * User: blangel
 * Date: 4/27/12
 * Time: 12:20 PM
 *
 * Provides a unit to the size of files.  Similar to {@link java.util.concurrent.TimeUnit} when dealing
 * with scheduling tasks.
 */
public enum SizeUnit {

    BYTES {
        @Override public long toBytes(long value) {
            return value;
        }
        @Override public long toKilobytes(long value) {
            return (value / FACTOR);
        }
        @Override public long toMegabytes(long value) {
            return (value / FACTOR / FACTOR);
        }
        @Override public long toGigabytes(long value) {
            return (value / FACTOR / FACTOR / FACTOR);
        }
        @Override public long convert(long value, SizeUnit from) {
            return from.toBytes(value);
        }
    },
    KILOBYTES {
        @Override public long toBytes(long value) {
            return (value * FACTOR);
        }
        @Override public long toKilobytes(long value) {
            return value;
        }
        @Override public long toMegabytes(long value) {
            return (value / FACTOR);
        }
        @Override public long toGigabytes(long value) {
            return (value / FACTOR / FACTOR);
        }
        @Override public long convert(long value, SizeUnit from) {
            return from.toKilobytes(value);
        }
    },
    MEGABYTES {
        @Override public long toBytes(long value) {
            return (value * FACTOR * FACTOR);
        }
        @Override public long toKilobytes(long value) {
            return (value * FACTOR);
        }
        @Override public long toMegabytes(long value) {
            return value;
        }
        @Override public long toGigabytes(long value) {
            return (value / FACTOR);
        }
        @Override public long convert(long value, SizeUnit from) {
            return from.toMegabytes(value);
        }
    },
    GIGABYTES {
        @Override public long toBytes(long value) {
            return (value * FACTOR * FACTOR * FACTOR);
        }
        @Override public long toKilobytes(long value) {
            return (value * FACTOR * FACTOR);
        }
        @Override public long toMegabytes(long value) {
            return (value * FACTOR);
        }
        @Override public long toGigabytes(long value) {
            return value;
        }
        @Override public long convert(long value, SizeUnit from) {
            return from.toGigabytes(value);
        }
    };

    private static final long FACTOR = 1024;

    /**
     * Converts {@code value} from the instance's unit to bytes.
     * @param value to convert
     * @return {@code value} as bytes.
     */
    public abstract long toBytes(long value);

    /**
     * Converts {@code value} from the instance's unit to kilobytes.
     * @param value to convert
     * @return {@code value} as kilobytes.
     */
    public abstract long toKilobytes(long value);

    /**
     * Converts {@code value} from the instance's unit to megabytes.
     * @param value to convert
     * @return {@code value} as megabytes.
     */
    public abstract long toMegabytes(long value);

    /**
     * Converts {@code value} from the instance's unit to gigabytes.
     * @param value to convert
     * @return {@code value} as gigabytes.
     */
    public abstract long toGigabytes(long value);

    /**
     * Converts {@code value} from {@code from} units to the instance's unit.
     * @param value to convert
     * @param from the unit of {@code value}
     * @return {@code value} into the instance's unit.
     */
    public abstract long convert(long value, SizeUnit from);

}
