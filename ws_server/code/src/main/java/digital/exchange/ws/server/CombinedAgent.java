package digital.exchange.ws.server;

import org.agrona.concurrent.Agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinedAgent implements Agent
{
    private final List<Agent> agents = new ArrayList<>();

    public CombinedAgent(Agent... agents)
    {
        this.agents.addAll(Arrays.asList(agents));
    }

    @Override
    public void onStart() {
        for(Agent agent : agents)
        {
            agent.onStart();
        }
    }

    @Override
    public int doWork() {
        int ret = 0;

        for(Agent agent : agents)
        {
            try {
                ret += agent.doWork();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    @Override
    public void onClose() {
        for(Agent agent : agents)
        {
            agent.onClose();
        }
    }

    @Override
    public String roleName() {
        return "MainLoopAgentRunner";
    }
}
