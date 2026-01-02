"use client";

import {
  CandlestickData,
  UTCTimestamp,
} from "lightweight-charts";
import {
  createCandleChart,
  setCandleData,
  updateCandleData,
  transformCandlesForChart, 
} from "@/lib/candleChartDriver";
import { fetchCandles } from "@/lib/marketRestApi";
import {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useRef,
} from "react";

export type TradeMsg = {
  type: "trade";
  price: string;
  timestamp: string;
};

export type CandlesChartHandle = {
  onTrade: (trade: TradeMsg) => void;
};

const CandlesChart = forwardRef<CandlesChartHandle>((_, ref) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const chartRef = useRef<ReturnType<typeof createCandleChart> | null>(null);
  const lastCandleRef = useRef<CandlestickData<UTCTimestamp> | null>(null);

  useImperativeHandle(ref, () => ({
    onTrade(trade) {
      if (!chartRef.current) return;
      
      const price = Number(trade.price);
      const timestampMs = Number(trade.timestamp);
      const candleTime =
        Math.floor(timestampMs / 60000) * 60;

      const last = lastCandleRef.current;

      if (!last || candleTime > last.time) {
        const candle: CandlestickData<UTCTimestamp> = {
          time: candleTime as UTCTimestamp,
          open: price,
          high: price,
          low: price,
          close: price,
        };
        lastCandleRef.current = candle;
        updateCandleData(chartRef.current!, candle);
      } else {
        last.high = Math.max(last.high, price);
        last.low = Math.min(last.low, price);
        last.close = price;
        updateCandleData(chartRef.current!, last);
      }
    },
  }));

  useEffect(() => {
    if (!containerRef.current) return;

    const chart = createCandleChart(containerRef.current);
    chartRef.current = chart;

    let cancelled = false;

    async function loadCandles() {
        try {
          const raw_data = await fetchCandles("BTC", "USDC", "1m", 100);
          if (cancelled) return;
          console.log("Raw API candles:", raw_data);

          const candles = transformCandlesForChart(raw_data);
          setCandleData(chart, candles);

          lastCandleRef.current = candles.length > 0 ? candles[candles.length - 1] : null;
        } catch (err) {
          console.error("Failed to fetch candles", err);
          return;
        }
    }

    loadCandles();

    return () => {
      cancelled = true;
      chart.chart.remove();
    };
  }, []);

  return (
    <div
      ref={containerRef}
      style={{ 
        border: "1px solid #222",
        borderRadius: 6,
        width: "100%", 
        height: "100%" 
      }}
    />
  );
});

CandlesChart.displayName = "CandlesChart";
export default CandlesChart;
