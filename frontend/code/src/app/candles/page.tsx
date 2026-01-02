"use client";

import { useEffect, useRef } from "react";
import { CandlestickData, UTCTimestamp } from "lightweight-charts";
import { fetchCandles, ApiCandle } from "@/lib/marketRestApi";
import { useWebSocket } from "@/hooks/useWebsocket";
import {
  createCandleChart,
  setCandleData,
  updateCandleData,
  transformCandlesForChart,
} from "@/lib/candleChartDriver";

export default function CandlesPage() {
  const containerRef = useRef<HTMLDivElement>(null);
  const chartInstanceRef = useRef<ReturnType<typeof createCandleChart> | null>(null);
  const lastCandleRef = useRef<CandlestickData<UTCTimestamp> | null>(null);

  const ws = useWebSocket({
        onOpen: (ws) => {
            ws.send(
            JSON.stringify({
                TRADES: ["BTCUSDC"],
              })
            );
        },
        onMessage: (trade) => {
            if (trade.type !== "trade") return;

            const price = Number(trade.price);
            const timestampMs = Number(trade.timestamp);
            const candleTime = Math.floor(timestampMs / 60000) * 60; // seconds

            const lastCandle = lastCandleRef.current;
            if (!lastCandle || candleTime > lastCandle.time) {
            // New candle
            const newCandle: CandlestickData<UTCTimestamp> = {
                time: candleTime as UTCTimestamp,
                open: price,
                high: price,
                low: price,
                close: price,
            };
            lastCandleRef.current = newCandle;
            updateCandleData(chartInstanceRef.current!, newCandle);
            } else {
            // Update existing candle
            const updatedCandle: CandlestickData<UTCTimestamp> = {
                ...lastCandle,
                high: Math.max(lastCandle.high, price),
                low: Math.min(lastCandle.low, price),
                close: price,
            };
            lastCandleRef.current = updatedCandle;
            updateCandleData(chartInstanceRef.current!, updatedCandle);
            }
        },
    });

  useEffect(() => {
    if (!containerRef.current) return;

    const instance = createCandleChart(containerRef.current);
    chartInstanceRef.current = instance;

    let cancelled = false;

    async function loadCandles() {
        try {
          const raw_data = await fetchCandles("BTC", "USDC", "1m", 100);
          if (cancelled) return;
          console.log("Raw API candles:", raw_data);

          const candles = transformCandlesForChart(raw_data);
          setCandleData(instance, candles);

          lastCandleRef.current = candles.length > 0 ? candles[candles.length - 1] : null;

          ws.connect();
        } catch (err) {
          console.error("Failed to fetch candles", err);
          return;
        }
    }

    loadCandles();

    return () => {
      cancelled = true;
      instance.chart.remove();
      ws.disconnect();
    };
  }, []);

  return (
    <main style={{ padding: 20 }}>
      <h1>Candlestick Chart</h1>
      <div ref={containerRef} style={{ width: "100%", height: "500px" }} />
    </main>
  );
}
