package net.ocheyedan.uncial.appender;

import java.io.*;

/**
 * User: blangel
 * Date: 4/19/12
 * Time: 9:42 PM
 *
 * A simple file based {@link Appender}.  The log file is never rolled.  See {@link RollingFileAppender} is such behavior
 * is needed.
 */
public class FileAppender implements Appender {

    private final File file;

    private final Writer fileWriter;

    public FileAppender(String filePath) {
        this(new File(filePath));
    }

    public FileAppender(File file) {
        this.file = file;
        this.file.getParentFile().mkdirs();
        try {
            this.file.createNewFile();
            fileWriter = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException ioe) {
            throw new IllegalArgumentException(
                    String.format("Could not create log-file %s [ at path %s ].", file.getName(), file.getAbsolutePath()), ioe);
        }
    }

    @Override public String getName() {
        return String.format("%s - %s", getClass().getSimpleName(), file.getName());
    }

    @Override public void handle(String message) {
        try {
            fileWriter.write(message);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    @Override public void flush() {
        try {
            fileWriter.flush();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    @Override public void close() {
        try {
            fileWriter.close();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }
}
