package net.ocheyedan.uncial.caliper;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;

/**
 * User: blangel
 * Date: 4/26/12
 * Time: 4:14 PM
 */
public class FileLengthBenchmark extends SimpleBenchmark {

    private static final File file;

    private static final FileChannel channel;

    private static final InputStream stream;

    static {
        file = new File("target/test-classes/net/ocheyedan/uncial/caliper/FileLengthBenchmark.class");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            channel = fileInputStream.getChannel();
            URL url = file.toURI().toURL();
            stream = url.openStream();
        } catch (IOException ioe) {
            throw new AssertionError(ioe.getMessage());
        }
    }

    public int timeFileLength_File(int reps) {
        int value = 0;
        for (int i = 0; i < reps; i++) {
            value += file.length();
        }
        return value;
    }

    public int timeFileLength_Channel(int reps) throws IOException {
        int value = 0;
        for (int i = 0; i < reps; i++) {
            value += channel.size();
        }
        return value;
    }

    public int timeFileLength_Stream(int reps) throws IOException {
        int value = 0;
        for (int i = 0; i < reps; i++) {
            value += stream.available();
        }
        return value;
    }

    public static void main(String[] args) throws Exception {
        Runner.main(FileLengthBenchmark.class, args);
    }
}
