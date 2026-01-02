"use client";

import { useState } from "react";
import { addOrder, ApiResponse } from "@/lib/ingressRestApi";

export default function TradePage() {
  const [base, setBase] = useState("BTC");
  const [quote, setQuote] = useState("USDC");
  const [orderSide, setOrderSide] = useState<"BUY" | "SELL">("BUY");
  const [orderType, setOrderType] = useState("LIMIT");
  const [price, setPrice] = useState(100);
  const [qty, setQty] = useState(1);

  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<ApiResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function submitOrder() {
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const res = await addOrder(
        base,
        quote,
        1,
        orderSide,
        orderType,
        price,
        qty
      );
      setResult(res);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  const isBuy = orderSide === "BUY";

  return (
    <main
      style={{
        maxWidth: 360,
        margin: "40px auto",
        background: "#0b0e11",
        color: "#eaecef",
        padding: 16,
        borderRadius: 8,
      }}
    >
      <h2 style={{ marginBottom: 12 }}>
        {base}/{quote}
      </h2>

      {/* BUY / SELL TOGGLE */}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
        <button
          onClick={() => setOrderSide("BUY")}
          style={{
            background: isBuy ? "#0ECB81" : "#1e2329",
            color: isBuy ? "#000" : "#eaecef",
            padding: 10,
            fontWeight: "bold",
            borderRadius: 4,
          }}
        >
          Buy
        </button>

        <button
          onClick={() => setOrderSide("SELL")}
          style={{
            background: !isBuy ? "#F6465D" : "#1e2329",
            color: !isBuy ? "#000" : "#eaecef",
            padding: 10,
            fontWeight: "bold",
            borderRadius: 4,
          }}
        >
          Sell
        </button>
      </div>

      {/* ORDER TYPE */}
      <select
        value={orderType}
        onChange={(e) => setOrderType(e.target.value)}
        style={{ width: "100%", marginTop: 12, padding: 8 }}
      >
        <option value="LIMIT">Limit</option>
        <option value="MARKET">Market</option>
      </select>

      {/* PRICE */}
      <input
        type="number"
        value={price}
        onChange={(e) => setPrice(Number(e.target.value))}
        placeholder="Price"
        disabled={orderType === "MARKET"}
        style={{ width: "100%", marginTop: 8, padding: 8 }}
      />

      {/* QTY */}
      <input
        type="number"
        value={qty}
        onChange={(e) => setQty(Number(e.target.value))}
        placeholder="Quantity"
        style={{ width: "100%", marginTop: 8, padding: 8 }}
      />

      {/* SUBMIT */}
      <button
        onClick={submitOrder}
        disabled={loading}
        style={{
          width: "100%",
          marginTop: 12,
          padding: 12,
          fontWeight: "bold",
          background: isBuy ? "#0ECB81" : "#F6465D",
          color: "#000",
          borderRadius: 4,
        }}
      >
        {loading ? "Submitting..." : isBuy ? "Buy" : "Sell"}
      </button>

      {/* RESULT */}
      {result && (
        <pre
          style={{
            marginTop: 16,
            background: "#111",
            padding: 10,
            fontSize: 12,
            overflowX: "auto",
          }}
        >
{JSON.stringify(result, null, 2)}
        </pre>
      )}

      {error && (
        <p style={{ color: "#F6465D", marginTop: 12 }}>
          Error: {error}
        </p>
      )}
    </main>
  );
}
