"use client";

import { forwardRef, useImperativeHandle, useRef, useState } from "react";

export type TradeMsg = {
  type: "trade";
  price: string;
  qty: string;
  timestamp: string;
};

export type TradesHandle = {
  onTrade: (trade: TradeMsg) => void;
};

type Trade = {
  price: number;
  qty: number;
  timestamp: number;
};

const TradesTable = forwardRef<TradesHandle>((_, ref) => {
  const [trades, setTrades] = useState<Trade[]>([]);
  const tradesRef = useRef<Trade[]>([]);
  const lastPriceRef = useRef<number | null>(null);

  useImperativeHandle(ref, () => ({
    onTrade(tradeMsg: TradeMsg) {
      const price = Number(tradeMsg.price);
      const trade: Trade = {
        price,
        qty: Number(tradeMsg.qty),
        timestamp: Number(tradeMsg.timestamp),
      };

      tradesRef.current = [trade, ...tradesRef.current].slice(0, 15);
      setTrades([...tradesRef.current]);
      lastPriceRef.current = price;
    },
  }));

  return (
    <div
      style={{
        border: "1px solid #222",
        borderRadius: 6,
        overflow: "hidden",
        height: "100%",
        display: "grid",
        gridTemplateRows: "auto 1fr",
        backgroundColor: "#111",
        color: "#ccc",
        fontFamily: "monospace",
      }}
    >
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "1fr 1fr 1fr",
          padding: "6px 8px",
          fontWeight: "bold",
          borderBottom: "1px solid #222",
          backgroundColor: "#111",
          color: "#ccc",
        }}
      >
        <div>Price</div>
        <div>Qty</div>
        <div>Time</div>
      </div>

      {/* Trades */}
      <div style={{ overflowY: "auto" }}>
        {trades.length === 0 && (
          <div style={{ padding: 8, opacity: 0.6 }}>Waiting for trades...</div>
        )}

        {trades.map((t, i) => {
          const lastPrice = i === trades.length - 1 ? null : trades[i + 1].price;
          let color = "#ccc";
          if (lastPrice !== null) {
            color = t.price > lastPrice ? "#26a69a" : t.price < lastPrice ? "#ef5350" : "#ccc";
          }

          return (
            <div
              key={i}
              style={{
                display: "grid",
                gridTemplateColumns: "1fr 1fr 1fr",
                padding: "6px 8px",
                fontSize: 13,
                borderBottom: "1px solid #222",
                color,
              }}
            >
              <div>{t.price}</div>
              <div>{t.qty}</div>
              <div>{new Date(t.timestamp).toLocaleTimeString("en-GB")}</div>
            </div>
          );
        })}
      </div>
    </div>
  );
});

TradesTable.displayName = "TradesTable";
export default TradesTable;
