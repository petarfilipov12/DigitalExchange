"use client";

import { useState } from "react";
import { useWebSocket } from "@/hooks/useWebsocket";

export default function MarketPage() {
  const [symbol, setSymbol] = useState("BTCUSDC");

  const [messages, setMessages] = useState<string[]>([]);

  const { connect, disconnect, status, socket } = useWebSocket({
    onOpen: (ws) => {
      ws.send(
        JSON.stringify({
          TRADES: [symbol],
          ORDER_BOOK: [symbol]
        })
      );
    },
    onMessage: (msg) => {
      // if (msg.type === "trade") candleStore.applyTrade(msg);
      // if (msg.type === "orderBook") orderBookStore.applyDelta(msg);
    },
  });

  return (
    <main style={{ padding: 20 }}>
      <h1>MarketPage</h1>
    </main>
  );
}
