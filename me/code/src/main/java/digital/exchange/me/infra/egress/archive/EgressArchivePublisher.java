package digital.exchange.me.infra.egress.archive;

import io.aeron.Aeron;
import io.aeron.Publication;
import digital.exchange.me.utils.AppConfig;
import org.agrona.CloseHelper;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EgressArchivePublisher
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EgressArchivePublisher.class);

    private final IdleStrategy idleStrategy = new SleepingIdleStrategy();

    private Aeron aeron;
    private Publication publication;
    private String publicationChannel = AppConfig.recordChannel;

    public void launch(final String publicationChannel)
    {
        LOGGER.info("Starting ArchivePublisher...");

        launchAeron();

        if((publicationChannel != null) && (!publicationChannel.isEmpty()) )
        {
            this.publicationChannel = publicationChannel;
        }

        initPublication();
    }

    private void launchAeron()
    {
        aeron = Aeron.connect(new Aeron.Context()
                .aeronDirectoryName(AppConfig.aeronDirName));
    }

    private void initPublication()
    {
        publication = aeron.addExclusivePublication(publicationChannel, AppConfig.recordStreamID);

        while (!publication.isConnected()) {
            idleStrategy.idle();
        }
    }

    public void publish(final DirectBuffer buffer, final int length)
    {
        while (publication.offer(buffer, 0, length) < 0)
        {
            idleStrategy.idle();
        }
    }


    public void close() {
        CloseHelper.close(aeron);
    }
}
