package digital.exchange.ws.server.ws.message.sender;

import digital.exchange.sbe.me.output.MessageHeaderDecoder;
import digital.exchange.ws.server.message.pipe.MessagePipe;
import org.agrona.concurrent.*;

import javax.imageio.plugins.tiff.GeoTIFFTagSet;
import java.nio.ByteBuffer;

public class WsMessageSender
{
    private final MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();
    private final UnsafeBuffer unsafeBuffer = new UnsafeBuffer(0, 0);

    private final WsMessageSenderAgent agent;
    final IdleStrategy idleStrategy = new SleepingMillisIdleStrategy(1);
    final AgentRunner agentRunner;

    private final Gateway gateway;

    public WsMessageSender(final MessagePipe messagePipe, final Gateway gateway)
    {
        agent = new WsMessageSenderAgent(messagePipe, gateway::decode);

        agentRunner = new AgentRunner(
                idleStrategy,
                Throwable::printStackTrace,
                null,
                agent
        );

        this.gateway = gateway;
    }

    public void startOnThread()
    {
        AgentRunner.startOnThread(agentRunner);
    }

    public Agent getAgent() {
        return agent;
    }
}
