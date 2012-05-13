package net.ocheyedan.uncial;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * User: blangel
 * Date: 5/13/12
 * Time: 9:50 AM
 */
class DaemonThreadFactory implements ThreadFactory {

    final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();

    @Override public Thread newThread(Runnable r) {
        Thread defaultThread = defaultThreadFactory.newThread(r);
        defaultThread.setDaemon(true);
        return defaultThread;
    }

}
