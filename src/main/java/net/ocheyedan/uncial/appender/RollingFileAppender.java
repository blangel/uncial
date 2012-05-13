package net.ocheyedan.uncial.appender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: blangel
 * Date: 4/27/12
 * Time: 9:22 AM
 *
 * Similar to {@link FileAppender} but users pass in parameters which dictate when the file should be "rolled", that is
 * another file created and logged to.
 */
public class RollingFileAppender implements Appender {

    private final AtomicReference<FileWriter> writer;

    private final File file;

    private final RollingPolicy rollingPolicy;

    /**
     * Creates a {@link RollingFileAppender} which rolls the log file {@code every} {@code unit} of time.
     * The rolled log files will be renamed with the corresponding date for which they logged.
     * @param file to which to log
     * @param every the duration in {@code unit} which to roll the log (e.g., 1 day)
     * @param unit the unit for {@code every} (e.g., day)
     */
    public RollingFileAppender(String file, int every, TimeUnit unit) {
        this(new File(file), every, unit);
    }

    /**
     * Creates a {@link RollingFileAppender} which rolls the log file {@code every} {@code unit} of time.
     * The rolled log files will be renamed with the corresponding date for which they logged.
     * @param file to which to log
     * @param every the duration in {@code unit} which to roll the log (e.g., 1 day)
     * @param unit the unit for {@code every} (e.g., day)
     */
    public RollingFileAppender(File file, int every, TimeUnit unit) {
        this(file, new RollingPolicy.Timed(every, unit));
    }

    /**
     * Creates a {@link RollingFileAppender} which rolls the log file whenever the file reaches {@code every} {@code unit}
     * of size.
     * <p/>
     * Note, the size chosen should be considered with the following caveat.  The underlying {@link FileWriter} will
     * not be flushed (unless closed) and so this number should consider that the buffered amount of the {@link FileWriter}'s
     * underlying {@link java.io.OutputStream} may be more than this size in which case the file will not actually be
     * rolled until the buffer size has been reached.
     * <p/>
     * The rolled log files will be renamed with the corresponding date at which they were rolled.
     * @param file to which to log
     * @param every the size at which to roll the log file (e.g., 50 MB)
     * @param unit the unit for {@code every} (e.g., MB)
     */
    public RollingFileAppender(String file, long every, SizeUnit unit) {
        this(new File(file), every, unit);
    }

    /**
     * Creates a {@link RollingFileAppender} which rolls the log file whenever the file reaches {@code every} {@code unit}
     * of size.
     * <p/>
     * Note, the size chosen should be considered with the following caveat.  The underlying {@link FileWriter} will
     * not be flushed (unless closed) and so this number should consider that the buffered amount of the {@link FileWriter}'s
     * underlying {@link java.io.OutputStream} may be more than this size in which case the file will not actually be
     * rolled until the buffer size has been reached.
     * <p/>
     * The rolled log files will be renamed with the corresponding date at which they were rolled.
     * @param file to which to log
     * @param every the size at which to roll the log file (e.g., 50 MB)
     * @param unit the unit for {@code every} (e.g., MB)
     */
    public RollingFileAppender(File file, long every, SizeUnit unit) {
        this(file, new RollingPolicy.Sized(every, unit));
    }

    private RollingFileAppender(File file, RollingPolicy rollingPolicy) {
        this.file = file;
        this.rollingPolicy = rollingPolicy;
        this.writer = new AtomicReference<FileWriter>();
        update();
    }

    private void update() {
        try {
            this.file.getParentFile().mkdirs();
            this.file.createNewFile();
            FileOutputStream stream = new FileOutputStream(this.file, true);
            FileChannel channel = stream.getChannel();
            this.writer.set(new FileWriter(stream.getFD()));
            this.rollingPolicy.update(channel);
        } catch (IOException ioe) {
            throw new IllegalArgumentException(
                    String.format("Could not create log-file %s [ at path %s ].", file.getName(), file.getAbsolutePath()), ioe);
        }
    }

    @Override public String getName() {
        return String.format("%s - %s", getClass().getSimpleName(), file.getName());
    }

    @Override public void handle(String message) {

        synchronized (rollingPolicy) {
            try {
                if (rollingPolicy.shouldRoll()) {
                    // close current writer
                    writer.get().close();
                    // rename current file
                    File renamed = rollingPolicy.createRollFile(file);
                    renamed.createNewFile();
                    boolean success = file.renameTo(renamed);
                    if (!success) {
                        System.err.printf("Failed to rename file [ %s ] to [ %s ].", file.getName(), renamed.getName());
                    }
                    // update reference to new FileWriter and update FileChannel for RollingPolicy
                    update();
                }
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
            }
        }

        // TODO - likely need this within the synchronized block (Thread A is still doing write below when ThreadB closes writer above)

        try {
            writer.get().write(message);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    @Override public void flush() {
        try {
            writer.get().flush();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    @Override public void close() {
        try {
            writer.get().close();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

}
