package net.ocheyedan.uncial.appender;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: blangel
 * Date: 4/27/12
 * Time: 7:34 PM
 *
 * Implementations provide information necessary to determine when a log file should be rolled.
 */
public interface RollingPolicy {

    /**
     * A time based implementation; i.e., roll the log every day, every hour, etc.
     * The time unit is configured with {@link TimeUnit}, however, only units greater than {@link TimeUnit#SECONDS}
     * are allowed.
     */
    static final class Timed implements RollingPolicy {

        private final int every;

        private final TimeUnit unit;

        private final AtomicReference<Calendar> next;

        public Timed(int every, TimeUnit unit) {
            if (is(unit, TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS, TimeUnit.MILLISECONDS, TimeUnit.SECONDS)) {
                throw new IllegalArgumentException("Time unit must be greater than TimeUnit.SECONDS");
            }
            this.every = every;
            this.unit = unit;
            this.next = new AtomicReference<Calendar>();
        }

        @Override public void update(FileChannel fileChannel) {
            // this is called at start and for every roll, use it to track next
            Calendar now = Calendar.getInstance();
            eraseInaptFields(now);
            now.add(getCalendarField(), every);
            next.set(now);
        }

        @Override public boolean shouldRoll() throws IOException {
            return Calendar.getInstance().after(next.get());
        }

        @Override public File createRollFile(File base) {
            Calendar copy = (Calendar) next.get().clone();
            copy.add(getCalendarField(), every * -1); // renaming the current so subtract from next
            return new File(String.format("%s.%s", base.getAbsolutePath(), dateFormatter.get().format(copy.getTime())));
        }

        private boolean is(TimeUnit unit, TimeUnit ... oneOf) {
            for (TimeUnit of : oneOf) {
                if (unit == of) {
                    return true;
                }
            }
            return false;
        }

        @SuppressWarnings("fallthrough")
        private void eraseInaptFields(Calendar now) {
            now.set(Calendar.MILLISECOND, 0);
            now.set(Calendar.SECOND, 0);
            switch (unit) {
                case DAYS:
                    now.set(Calendar.HOUR_OF_DAY, 0);
                case HOURS:
                    now.set(Calendar.MINUTE, 0);
            }
        }

        private int getCalendarField() {
            switch (unit) {
                case MINUTES:
                    return Calendar.MINUTE;
                case HOURS:
                    return Calendar.HOUR_OF_DAY;
                case DAYS:
                    return Calendar.DATE;
                default:
                    throw new AssertionError(String.format("Unsupported TimeUnit %s", unit));
            }
        }
    }

    /**
     * A size based implementation; i.e., roll the log when its size is 50MB, 5MB, etc.
     * Note, the size chosen should be considered with the following caveat.  The {@link RollingFileAppender} will
     * not flush the file writer and so this number should consider that the buffered amount may be more than this
     * size in which case the file will not actually be rolled until the buffer size has been reached.
     */
    static final class Sized implements RollingPolicy {

        private final long every;

        private final SizeUnit unit;

        private final AtomicReference<FileChannel> fileChannel;

        public Sized(long every, SizeUnit unit) {
            this.every = every;
            this.unit = unit;
            this.fileChannel = new AtomicReference<FileChannel>();
        }

        @Override public void update(FileChannel fileChannel) {
            this.fileChannel.set(fileChannel);
        }

        @Override public boolean shouldRoll() throws IOException {
            long size = fileChannel.get().size();
            long sizeInUnits = unit.convert(size, SizeUnit.BYTES);
            return (sizeInUnits >= every);
        }

        @Override public File createRollFile(File base) {
            String name = String.format("%s.%s", base.getAbsolutePath(), dateFormatter.get().format(new Date(System.currentTimeMillis())));
            // possible that name already exists (saying logging 1 byte files, there will be many within one minute).
            File rolled = new File(name);
            int i = 0;
            while (rolled.exists()) {
                rolled = new File(String.format("%s.%d", name, i++));
            }
            return rolled;
        }
    }

    static final ThreadLocal<SimpleDateFormat> dateFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd-yyyy_HHmm");
        }
    };

    /**
     * Initializes/updates the policy by providing access to the current {@link FileChannel}
     * @param fileChannel the current channel to the log file which log messages are being written
     */
    void update(FileChannel fileChannel);

    /**
     * @return true if the log file needs to be rolled.
     * @throws IOException if there is an error when checking the underlying {@link FileChannel}
     */
    boolean shouldRoll() throws IOException;

    /**
     * This method only creates the file, it does not handle the actual renaming of an existing log file.
     * @param base log file
     * @return a log file based on {@code base} with an appendage for the roll
     */
    File createRollFile(File base);

}
