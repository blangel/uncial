package net.ocheyedan.uncial;

import net.ocheyedan.uncial.appender.ConsoleAppender;
import net.ocheyedan.uncial.appender.FileAppender;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static junit.framework.Assert.assertEquals;

/**
 * User: blangel
 * Date: 4/19/12
 * Time: 9:53 PM
 */
public class UncialTest {

    @Before
    public void init() {
        // remove the existing
        File file = new File("/tmp/uncial-test.log");
        file.delete();
    }

    @Test
    public void log() throws InterruptedException, IOException {

        // set the console output to something we can monitor.
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintStream stdout = new PrintStream(stream);
        PrintStream oldStdout = System.out;
        try {
            System.setOut(stdout);

            Logger logger = Loggers.get(UncialTest.class);
            logger.warn("Hello there %s", UncialTest.class.getSimpleName());
            Thread.yield();
            Thread.sleep(500); // give some time to logging thread to do its work
            stdout.flush();
            assertEquals(0, stream.size());

            UncialConfig.get().addAppender(new ConsoleAppender());
            logger.warn("Now, hello there %s!", UncialTest.class.getSimpleName());
            Thread.yield();
            Thread.sleep(500); // give some time to logging thread to do its work
            stdout.flush();
            assertEquals(47 * 2, stream.size());

            FileAppender fileAppender = new FileAppender("/tmp/uncial-test.log");
            UncialConfig.get().addAppender(fileAppender);
            logger.warn("Again, hello there %s!", UncialTest.class.getSimpleName());
            Thread.yield();
            Thread.sleep(500); // give some time to logging thread to do its work
            stdout.flush();
            fileAppender.flush();
            assertEquals((47 * 2) + (48 * 2), stream.size());
            FileReader reader = new FileReader("/tmp/uncial-test.log");
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            bufferedReader.close();
            assertEquals(95, line.length());
        } finally {
            System.setOut(oldStdout);
        }
    }

}
