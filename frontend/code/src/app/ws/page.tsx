"use client";

import { useState } from "react";
import { useWebSocket } from "@/hooks/useWebsocket";

export default function WebSocketPage() {
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
      setMessages((prev) => [
        ...prev,
        JSON.stringify(msg, null, 2),
      ]);
    },
  });

  return (
    <main style={{ padding: 20 }}>
      <h1>WebSocket Debug</h1>

      <div style={{ marginBottom: 12 }}>
        <input
          value={symbol}
          onChange={(e) => setSymbol(e.target.value)}
          placeholder="Symbol (e.g. BTCUSDC)"
          style={{ padding: 6, marginRight: 8 }}
        />

        <button
          onClick={connect}
          disabled={status === "connecting" || status === "open"}
        >
          Connect
        </button>

        <button
          onClick={disconnect}
          disabled={status !== "open"}
          style={{ marginLeft: 8 }}
        >
          Disconnect
        </button>
      </div>

      <p>Status: {status}</p>

      <pre
        style={{
          background: "#111",
          color: "#0f0",
          padding: 12,
          height: 400,
          overflow: "auto",
        }}
      >
        {messages.join("\n\n")}
      </pre>
    </main>
  );
}
