/*
 * Copyright 2023 Adaptive Financial Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package digital.exchange.me.infra.cluster;

import digital.exchange.me.infra.cluster.runtime.context.RuntimeContext;
import io.aeron.ExclusivePublication;
import io.aeron.Image;
import io.aeron.cluster.codecs.CloseReason;
import io.aeron.cluster.service.ClientSession;
import io.aeron.cluster.service.Cluster;
import io.aeron.cluster.service.ClusteredService;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The clustered service for the auction application.
 */
public class AppClusteredService implements ClusteredService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppClusteredService.class);
    private static final long clockUpdateIntervalMs = 60*1000; //on every round 1min

    private final Gateway gateway;
    private final ClockUpdater clockUpdater;

    private boolean initialized = false;

    public AppClusteredService(final Gateway gateway, final ClockUpdater clockUpdater)
    {
        this.gateway = gateway;
        this.clockUpdater = clockUpdater;
    }

    @Override
    public void onStart(final Cluster cluster, final Image snapshotImage)
    {
        LOGGER.info("Starting Node");

        RuntimeContext.setCluster(cluster);
    }

    @Override
    public void onSessionOpen(final ClientSession session, final long timestamp)
    {
        LOGGER.info("Client session opened, id: {}", session.id());
        RuntimeContext.setClusterTime(timestamp);
        RuntimeContext.getSessionContext().setCurrentSession(session);

        if(!initialized)
        {
            clockUpdater.init(timestamp);
            initialized = true;
        }
    }

    @Override
    public void onSessionClose(final ClientSession session, final long timestamp, final CloseReason closeReason)
    {
        RuntimeContext.setClusterTime(timestamp);
        RuntimeContext.getSubscribedSessions().removeSession(session);
    }

    @Override
    public void onSessionMessage(
        final ClientSession session,
        final long timestamp,
        final DirectBuffer buffer,
        final int offset,
        final int length,
        final Header header)
    {
        RuntimeContext.setClusterTime(timestamp);
        RuntimeContext.getSessionContext().setCurrentSession(session);

        gateway.route(buffer, offset, length);
    }

    @Override
    public void onTimerEvent(final long correlationId, final long timestamp)
    {
        LOGGER.info("Timer Event");
        RuntimeContext.setClusterTime(timestamp);

        clockUpdater.onTimerEvent(correlationId, timestamp);
    }

    @Override
    public void onTakeSnapshot(final ExclusivePublication snapshotPublication)
    {
        LOGGER.info("Take Snapshot");
    }

    @Override
    public void onRoleChange(final Cluster.Role newRole)
    {
        LOGGER.info("Role change: {}", newRole);

        RuntimeContext.setClusterRole(newRole);
    }

    @Override
    public void onTerminate(final Cluster cluster)
    {
        LOGGER.info("Terminating");
    }
}
