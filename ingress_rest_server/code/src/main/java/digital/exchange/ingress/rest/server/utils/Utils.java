package digital.exchange.ingress.rest.server.utils;

import org.agrona.concurrent.SystemEpochClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.Integer.parseInt;

public class Utils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static String getHostname()
    {
        return System.getenv("HOSTNAME");
    }

    public static String getClusterAddresses()
    {
        return System.getenv("ME_CLUSTER_ADDRESSES");
    }

    public static int getBasePort()
    {
        String portBaseString = System.getenv("ME_CLUSTER_PORT_BASE");
        if (null == portBaseString || portBaseString.isEmpty())
        {
            portBaseString = "9000";
        }
        return parseInt(portBaseString);
    }

    public static String getAeronDir()
    {
        return System.getenv("AERON_DIR");
    }

    public static void awaitDnsResolution(final String host) {
        if (applyDnsDelay())
        {
            LOGGER.info("Waiting 5 seconds for DNS to be registered...");
            quietSleep(5000);
        }

        final long endTime = SystemEpochClock.INSTANCE.time() + 60000;
        java.security.Security.setProperty("networkaddress.cache.ttl", "0");

        boolean resolved = false;
        while (!resolved)
        {
            try
            {
                InetAddress.getByName(host);
                resolved = true;
            }
            catch (final UnknownHostException e)
            {
                LOGGER.warn("cannot yet resolve name {}, retrying in 3 seconds", host);
                quietSleep(3000);
            }
        }
    }

    private static boolean applyDnsDelay()
    {
        final String dnsDelay = System.getenv("DNS_DELAY");
        if (null == dnsDelay || dnsDelay.isEmpty())
        {
            return false;
        }
        return Boolean.parseBoolean(dnsDelay);
    }

    private static void quietSleep(final long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (final InterruptedException ex)
        {
            LOGGER.warn("Interrupted while sleeping");
        }
    }
}
