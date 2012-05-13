package net.ocheyedan.uncial.appender;

import java.io.PrintStream;

/**
 * User: blangel
 * Date: 4/19/12
 * Time: 9:40 PM
 */
public class ConsoleAppender implements Appender {

    private final PrintStream printStream;

    public ConsoleAppender() {
        this(System.out);
    }

    public ConsoleAppender(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override public String getName() {
        return String.format("%s - %s", getClass().getSimpleName(), printStream.toString());
    }

    @Override public void handle(String message) {
        printStream.print(message);
    }

    @Override public void flush() {
        printStream.flush();
    }

    @Override public void close() {
        printStream.flush();
    }
}
