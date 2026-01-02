"use client";

import { useState } from "react";
import { addOrder } from "@/lib/ingressRestApi";

export default function OrderEntryForm() {
  const [price, setPrice] = useState("");
  const [qty, setQty] = useState("");
  const [orderType, setOrderType] = useState<"LIMIT" | "MARKET">("LIMIT");
  const [loading, setLoading] = useState(false);
  const [lastResult, setLastResult] = useState<string | null>(null);

  async function submit(side: "BUY" | "SELL") {
    setLoading(true);
    setLastResult(null);

    try {
      const res = await addOrder(
        "BTC",
        "USDC",
        1,
        side,
        orderType,
        Number(price),
        Number(qty)
      );

      setLastResult(res.response);
    } catch (err) {
      console.error(err);
      setLastResult("ERROR");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div
      style={{
        border: "1px solid #222",
        padding: 12,
        borderRadius: 6,
        backgroundColor: "#111",
        color: "#ccc",
        fontFamily: "monospace",
        display: "flex",
        flexDirection: "column",
        gap: 12,
      }}
    >
      <h3 style={{ margin: 0, fontSize: 16 }}>Place Order</h3>

      {/* Order type selector */}
      <div style={{ display: "flex", gap: 8 }}>
        {["LIMIT", "MARKET"].map((type) => (
          <button
            key={type}
            onClick={() => setOrderType(type as "LIMIT" | "MARKET")}
            style={{
              flex: 1,
              padding: 8,
              cursor: "pointer",
              borderRadius: 4,
              border: orderType === type ? "2px solid #555" : "1px solid #222",
              backgroundColor: orderType === type ? "#222" : "#111",
              color: "#ccc",
              fontWeight: "bold",
            }}
          >
            {type}
          </button>
        ))}
      </div>

      <input
        placeholder="Price"
        value={price}
        onChange={(e) => setPrice(e.target.value)}
        style={{
          padding: "8px 10px",
          borderRadius: 4,
          border: "1px solid #222",
          backgroundColor: "#222",
          color: "#eee",
        }}
      />

      <input
        placeholder="Quantity"
        value={qty}
        onChange={(e) => setQty(e.target.value)}
        style={{
          padding: "8px 10px",
          borderRadius: 4,
          border: "1px solid #222",
          backgroundColor: "#222",
          color: "#eee",
        }}
      />

      <div style={{ display: "flex", gap: 8 }}>
        <button
          onClick={() => submit("BUY")}
          disabled={loading}
          style={{
            flex: 1,
            background: "#26a69a",
            color: "#111",
            border: "none",
            padding: 10,
            cursor: "pointer",
            borderRadius: 4,
            fontWeight: "bold",
          }}
        >
          BUY
        </button>

        <button
          onClick={() => submit("SELL")}
          disabled={loading}
          style={{
            flex: 1,
            background: "#ef5350",
            color: "#111",
            border: "none",
            padding: 10,
            cursor: "pointer",
            borderRadius: 4,
            fontWeight: "bold",
          }}
        >
          SELL
        </button>
      </div>

      {lastResult && (
        <div style={{ fontSize: 12, opacity: 0.8 }}>
          Result: {lastResult}
        </div>
      )}
    </div>
  );
}
