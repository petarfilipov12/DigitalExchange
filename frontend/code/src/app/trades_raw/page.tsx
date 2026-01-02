"use client";

import { useEffect, useRef, useState } from "react";
import { useWebSocket } from "@/hooks/useWebsocket";

type TradeMsg = {
  type: "trade";
  price: string;
  qty: string;
  timestamp: string;
};

type Trade = {
  price: number;
  qty: number;
  timestamp: number;
};

export default function TradesTestPage() {
  const [trades, setTrades] = useState<Trade[]>([]);
  const tradesRef = useRef<Trade[]>([]);

  const ws = useWebSocket({
    onOpen(ws) {
      ws.send(
        JSON.stringify({
          TRADES: ["BTCUSDC"],
        })
      );
    },
    onMessage(msg) {
      if (msg.type !== "trade") return;

      const trade: Trade = {
        price: Number(msg.price),
        qty: Number(msg.qty),
        timestamp: Number(msg.timestamp),
      };

      tradesRef.current = [trade, ...tradesRef.current].slice(0, 20);
      setTrades([...tradesRef.current]);
    },
  });

  useEffect(() => {
    ws.connect();
    return ws.disconnect;
  }, []);

  return (
    <main style={{ padding: 20, maxWidth: 400 }}>
      <h2>Recent Trades (WS only)</h2>

      <div
        style={{
          marginTop: 12,
          border: "1px solid #222",
          borderRadius: 6,
          overflow: "hidden",
        }}
      >
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "1fr 1fr 1fr",
            padding: "6px 8px",
            fontWeight: "bold",
            borderBottom: "1px solid #222",
          }}
        >
          <div>Price</div>
          <div>Qty</div>
          <div>Time</div>
        </div>

        {trades.map((t, i) => (
          <div
            key={i}
            style={{
              display: "grid",
              gridTemplateColumns: "1fr 1fr 1fr",
              padding: "6px 8px",
              fontSize: 13,
              borderBottom: "1px solid #111",
            }}
          >
            <div>{t.price}</div>
            <div>{t.qty}</div>
            <div>
              {new Date(t.timestamp).toLocaleTimeString()}
            </div>
          </div>
        ))}

        {trades.length === 0 && (
          <div style={{ padding: 8, opacity: 0.6 }}>
            Waiting for trades...
          </div>
        )}
      </div>
    </main>
  );
}
