"use client";

import { useRef, useEffect } from "react";
import CandlesChart, {
  CandlesChartHandle,
} from "./candlesChart";
import OrderBookChart, {
  OrderBookChartHandle,
} from "./orderBookChart";
import OrderEntryForm from "./orderEntryForm";
import TradesTable, { TradesHandle } from "./tradesTable";
import { useWebSocket } from "@/hooks/useWebsocket";

export default function MarketPage() {
  const candlesRef = useRef<CandlesChartHandle>(null);
  const orderBookRef = useRef<OrderBookChartHandle>(null);
  const tradesRef = useRef<TradesHandle>(null);

  const ws = useWebSocket({
    onOpen(ws) {
      ws.send(
        JSON.stringify({
          TRADES: ["BTCUSDC"],
          ORDER_BOOK: ["BTCUSDC"],
        })
      );
    },
    onMessage(msg) {
      if (msg.type === "trade") {
        candlesRef.current?.onTrade(msg);
        tradesRef.current?.onTrade(msg);
      }

      if (msg.type === "orderBook") {
        orderBookRef.current?.onOrderBookUpdate(msg);
      }
    },
  });

  useEffect(() => {
    ws.connect();
    return ws.disconnect;
  }, []);

  return (
  <main
    style={{
      display: "grid",
      gridTemplateColumns: "3fr 2fr",
      gap: 6,
      height: "auto",
    }}
  >
    {/* LEFT COLUMN */}
    <div
      style={{
        display: "grid",
        gridTemplateRows: "1fr 1fr",
        gap: 6,
        minHeight: 0,
      }}
    >
      <CandlesChart ref={candlesRef} />
      <OrderBookChart ref={orderBookRef} />
    </div>

    {/* RIGHT COLUMN */}
      <div
        style={{
          display: "grid",
          gridTemplateRows: "2fr 1fr",
          gap: 6,
          minHeight: 0,
        }}
      >
        <TradesTable ref={tradesRef} />
        <OrderEntryForm />
      </div>
  </main>
);

}
