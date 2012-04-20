package net.ocheyedan.uncial.appender;

import java.io.PrintStream;

/**
 * User: blangel
 * Date: 4/19/12
 * Time: 9:40 PM
 */
public class PrintStreamAppender implements Appender {

    private final PrintStream printStream;

    public PrintStreamAppender() {
        this(System.out);
    }

    public PrintStreamAppender(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override public String getName() {
        return String.format("%s - %s", getClass().getSimpleName(), printStream.toString());
    }

    @Override public void handle(String message) {
        printStream.print(message);
    }
}
