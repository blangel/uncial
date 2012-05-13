package net.ocheyedan.uncial;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * User: blangel
 * Date: 4/22/12
 * Time: 12:29 PM
 *
 * Specifies how {@link net.ocheyedan.uncial.Logger} implementations give messages to registered
 * {@link net.ocheyedan.uncial.appender.Appender} objects.
 * For instance, on a separate thread or on the user's invoking thread.
 */
interface Distributor {

    /**
     * Process the log message on a separate thread, managed and created by this class.
     */
    static final class SeparateThread implements Distributor {

        private static final class Runner implements Runnable {
            private final Meta meta;
            private final String level;
            private final String formattedMessage;
            private final InvokingThread delegate;
            private Runner(Meta meta, String level, String formattedMessage, InvokingThread delegate) {
                this.meta = meta;
                this.level = level;
                this.formattedMessage = formattedMessage;
                this.delegate = delegate;
            }
            @Override public void run() {
                delegate.distribute(meta, level, formattedMessage);
            }
        }

        private static final ExecutorService logEventExecutor = Executors.newSingleThreadExecutor(new DaemonThreadFactory());

        private final InvokingThread delegate = new InvokingThread();

        @Override public void distribute(Meta meta, String level, String formattedMessage) {
            logEventExecutor.execute(new Runner(meta, level, formattedMessage, delegate));
        }
    }

    /**
     * Handles the log message on the same thread, handling it instantly.
     */
    static class InvokingThread implements Distributor {

        /**
         * Install a {@literal JVM} shutdown hook to ensure the appenders get a change to flush.
         */
        InvokingThread() {
            Thread flusher = new Thread(new Runnable() {
                @Override public void run() {
                    Collection<UncialConfig.AppenderConfig> appenderConfigs = UncialConfig.get().getAppenderConfigs();
                    for (UncialConfig.AppenderConfig appenderConfig : appenderConfigs) {
                        appenderConfig.appender.close();
                    }
                }
            });
            flusher.setDaemon(true);
            Runtime.getRuntime().addShutdownHook(flusher);
        }

        @Override public void distribute(Meta meta, String level, String formattedMessage) {
            Collection<UncialConfig.AppenderConfig> appenderConfigs = UncialConfig.get().getAppenderConfigs();
            for (UncialConfig.AppenderConfig appenderConfig : appenderConfigs) {
                String message = appenderConfig.format(meta, level, formattedMessage);
                appenderConfig.appender.handle(message);
            }
        }

    }

    /**
     * Distributes the log message to all configured {@link UncialConfig.AppenderConfig} objects.
     * @param meta associated with the log message to distribute
     * @param level of the log message to distribute
     * @param formattedMessage the actual message to distribute
     */
    void distribute(final Meta meta, final String level, final String formattedMessage);

}
