package digital.exchange.rest.server.marketdata.app.endpoints.trades;

import java.time.OffsetDateTime;

public record Trade(
        long seqNum,
        OffsetDateTime timestamp,
        long takerAccountId,
        long takerOrderId,
        String takerOrderSide,
        long makerAccountId,
        long makerOrderId,
        String makerOrderSide,
        long price,
        long qty
)
{
}
