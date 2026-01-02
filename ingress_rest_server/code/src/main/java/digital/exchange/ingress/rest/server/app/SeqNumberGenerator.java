package digital.exchange.ingress.rest.server.app;

import java.util.concurrent.atomic.AtomicLong;

public class SeqNumberGenerator
{
    private static final AtomicLong seqNum = new AtomicLong(0);

    public static long getSeqNumber()
    {
        return seqNum.getAndIncrement();
    }
}
