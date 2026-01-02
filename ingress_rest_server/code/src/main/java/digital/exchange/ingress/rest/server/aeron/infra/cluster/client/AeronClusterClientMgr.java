package digital.exchange.ingress.rest.server.aeron.infra.cluster.client;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingMillisIdleStrategy;

import static io.aeron.Publication.*;
import static digital.exchange.ingress.rest.server.aeron.infra.cluster.client.AeronClusterClientStateMachine.AeronClusterClientState.*;

public class AeronClusterClientMgr
{
    private static final AeronClusterClientStateMachine stateMachine = new AeronClusterClientStateMachine();

    private static final long retryCount = 10;
    private static final IdleStrategy retryIdleStrategy = new SleepingMillisIdleStrategy();

    public static boolean check()
    {
        boolean ret = true;

        if(stateMachine.getState() != CONNECTED)
        {
            ret = false;
        }
        else if(stateMachine.getClient().isClosed())
        {
            stateMachine.startStateMachine(CONNECT);
            ret = false;
        }

        return ret;
    }

    public static boolean keepAlive()
    {
        for(long i = 0; (stateMachine.getState() == CONNECTED) && (i < retryCount); i++)
        {
            if(stateMachine.getClient().sendKeepAlive())
            {
                return true;
            }

            retryIdleStrategy.idle();
        }

        return false;
    }

    public static int pollClusterEgress()
    {
        if(stateMachine.getState() != CONNECTED)
        {
            return 0;
        }

        return stateMachine.getClient().pollEgress();
    }

    public static boolean offer(final DirectBuffer buffer, final int length)
    {
        if(stateMachine.getState() != CONNECTED)
        {
            return false; //NOK
        }

        for(long i = 0; (stateMachine.getState() == CONNECTED) && (i < retryCount); i++)
        {
            var res = stateMachine.getClient().offer(buffer, 0, length);

            if(res >= 0)
            {
                return true; //OK
            }
            else if( (res == NOT_CONNECTED) || (res == CLOSED) )
            {
                stateMachine.startStateMachine(DISCONNECT);

                return false; //NOK
            }
            else if(res == MAX_POSITION_EXCEEDED)
            {
                stateMachine.startStateMachine(DISCONNECT);

                return false; //NOK
            }
            //else: BACK_PRESSURED, ADMIN_ACTION - retry

            retryIdleStrategy.idle();
        }

        return false; //NOK
    }

}
