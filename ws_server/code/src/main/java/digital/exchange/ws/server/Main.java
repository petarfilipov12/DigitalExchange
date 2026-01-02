
package digital.exchange.ws.server;

import digital.exchange.ws.server.cluster.client.MeEgressManager;
import digital.exchange.ws.server.message.pipe.MessagePipe;
import digital.exchange.ws.server.message.pipe.OneToOneRingBufferMessagePipe;
import digital.exchange.ws.server.ws.WsServer;
import digital.exchange.ws.server.ws.message.sender.Gateway;
import digital.exchange.ws.server.ws.message.sender.WsMessageSender;
import org.agrona.CloseHelper;
import org.agrona.concurrent.*;
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
        final MessagePipe messagePipe = new OneToOneRingBufferMessagePipe(4096);

        final WsServer wsServer = new WsServer(8080);
        final Gateway gateway = new Gateway(wsServer);
        final WsMessageSender wsMessageSender = new WsMessageSender(messagePipe, gateway);

        final MeEgressManager meEgressManager = new MeEgressManager(messagePipe);

        try (final ShutdownSignalBarrier barrier = new ShutdownSignalBarrier())
        {
            meEgressManager.startOnThread();
            wsServer.start();
            wsMessageSender.startOnThread();

            barrier.await();
        }
        finally
        {
            CloseHelper.close(meEgressManager);
            stopWsServer(wsServer);
        }
    }

    private static void stopWsServer(final WsServer wsServer)
    {
        try {
            wsServer.stop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
