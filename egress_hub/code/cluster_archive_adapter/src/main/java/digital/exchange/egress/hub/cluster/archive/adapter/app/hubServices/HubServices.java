package digital.exchange.egress.hub.cluster.archive.adapter.app.hubServices;

import digital.exchange.egress.hub.cluster.archive.adapter.utils.AppConfig;
import org.agrona.collections.Int2ObjectHashMap;

public class HubServices
{
    private final Int2ObjectHashMap<HubService> hubServicesMap = new Int2ObjectHashMap<>();

    public HubServices()
    {
        var serviceId = AppConfig.cacheOrderBookUpdaterServiceId;
        hubServicesMap.put(serviceId, new HubService(serviceId, 0));

        serviceId = AppConfig.cacheAccountUpdaterServiceId;
        hubServicesMap.put(serviceId, new HubService(serviceId, 0));

        serviceId = AppConfig.dbTradesUpdaterServiceId;
        hubServicesMap.put(serviceId, new HubService(serviceId, 0));
    }

    public int getNotUpdatedServiceIdMask(final long currentPosition)
    {
        if(hubServicesMap.size() <=0 )
        {
            return -1;
        }

        int serviceIdMask = 0;

        for(var hubService: hubServicesMap.values())
        {
            if(hubService.getPosition() < currentPosition)
            {
                serviceIdMask = serviceIdMask | hubService.getHubServiceId();
            }
        }

        return serviceIdMask;
    }

    public void updatePosition(final int hubServiceId, final long position)
    {
        final HubService hubService = hubServicesMap.get(hubServiceId);

        if(hubService != null)
        {
            hubService.setPosition(position);
        }
    }
}
