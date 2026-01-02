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
import digital.exchange.sbe.me.input.*;
import digital.exchange.me.app.order.Order;
import digital.exchange.me.app.ReturnEnum;
import digital.exchange.me.utils.SbeMeInUtils;
import org.agrona.ExpandableDirectByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link ClientResponder} interface which returns SBE encoded results to the client
 */
public class ClientResponder
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientResponder.class);

    private final ExpandableDirectByteBuffer buffer = new ExpandableDirectByteBuffer(1024);

    private final MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();

    private final CommandResponseEncoder commandResponseEncoder = new CommandResponseEncoder();
    private final CommandResponseOrderEncoder commandResponseOrderEncoder = new CommandResponseOrderEncoder();


    public void response(final ReturnEnum result, final long seqNum)
    {
        commandResponseEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .response(SbeMeInUtils.convertReturnEnumToSbeResponseEnum(result))
                .seqNum(seqNum);

        RuntimeContext.getSessionContext().replyCurrentSession(buffer, 0,
                MessageHeaderEncoder.ENCODED_LENGTH + commandResponseEncoder.encodedLength());
    }

    public void response(final ReturnEnum result, final Order order, final long seqNum)
    {
        commandResponseOrderEncoder.wrapAndApplyHeader(buffer, 0, messageHeaderEncoder)
                .response(SbeMeInUtils.convertReturnEnumToSbeResponseEnum(result))
                .seqNum(seqNum);

        SbeMeInUtils.convertOrderToSbeOrderEncoder(commandResponseOrderEncoder.order(), order);

        RuntimeContext.getSessionContext().replyCurrentSession(buffer, 0,
                MessageHeaderEncoder.ENCODED_LENGTH + commandResponseOrderEncoder.encodedLength());
    }

}
