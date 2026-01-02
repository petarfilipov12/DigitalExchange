
package digital.exchange.egress.hub.me.egress.poller;

import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample cluster application
 */
class Main {
    /**
     * The main method.
     * @param args command line args
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {

        final IdleStrategy idleStrategy = new SleepingMillisIdleStrategy(1);
        final MainLoopAgent agent = new MainLoopAgent();

        try {
            agent.onStart();
            while(true)
            {
                idleStrategy.idle(agent.doWork());
            }
        }
        finally {
            agent.onClose();
            LOGGER.info("Exiting");
        }
    }
}
