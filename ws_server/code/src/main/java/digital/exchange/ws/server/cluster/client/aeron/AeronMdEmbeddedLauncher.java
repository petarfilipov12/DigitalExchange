package digital.exchange.ws.server.cluster.client.aeron;

import digital.exchange.ws.server.utils.AppConfig;
import io.aeron.driver.MediaDriver;
import org.agrona.CloseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public class AeronMdEmbeddedLauncher implements Closeable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AeronMdEmbeddedLauncher.class);

    private MediaDriver.Context mediaDriverContext;
    private MediaDriver mediaDriver;

    private void initAeronMdContext()
    {
        LOGGER.info("Initializing Aeron MD Context");

        mediaDriverContext = new MediaDriver.Context()
                .aeronDirectoryName(AppConfig.aeronDirName)
                .dirDeleteOnStart(true);
    }

    private void launchMediaDriver()
    {
        LOGGER.info("Launching Aeron MD");
        mediaDriver = MediaDriver.launchEmbedded(mediaDriverContext);
    }

    public void startAeronMd()
    {
        initAeronMdContext();
        launchMediaDriver();
    }


    @Override
    public void close()
    {
        CloseHelper.closeAll(mediaDriver);
        mediaDriverContext.close();
    }
}
