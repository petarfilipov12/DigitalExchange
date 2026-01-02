"use client";

import { useState } from "react";
import { fetchCandles, ApiCandle } from "@/lib/marketRestApi";

export default function Home() {
  const [candles, setCandles] = useState<ApiCandle[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadCandles = async () => {
    try {
      setLoading(true);
      setError(null);

      const data = await fetchCandles("BTC", "USDC", "1m", 10);
      setCandles(data);
    } catch (err: any) {
      console.error(err);
      setError(err.message ?? "Failed to fetch candles");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main style={{ padding: 20 }}>
      <h1>REST Candles</h1>

      <button
        onClick={loadCandles}
        disabled={loading}
        style={{
          padding: "8px 16px",
          marginBottom: 12,
          cursor: "pointer",
        }}
      >
        {loading ? "Loading..." : "Fetch candles"}
      </button>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <pre
        style={{
          background: "#111",
          color: "#0f0",
          padding: 12,
          overflow: "auto",
        }}
      >
        {JSON.stringify(candles, null, 2)}
      </pre>
    </main>
  );
}
