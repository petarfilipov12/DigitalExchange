"use client";

import { useEffect, useRef } from "react";
import { fetchOrderBook } from "@/lib/marketRestApi";
import { OrderBook, OrderBookSide } from "@/lib/orderBook";
import {
  createOrderBookChart,
  setOrderBookData,
} from "@/lib/orderBookChartDriver";
import { useWebSocket } from "@/hooks/useWebsocket";

export default function OrderBookPage() {
  const containerRef = useRef<HTMLDivElement>(null);
  const chartRef = useRef<ReturnType<typeof createOrderBookChart> | null>(null);
  const orderBookRef = useRef<OrderBook>(new OrderBook());
  const scheduledRef = useRef(false);

  function scheduleChartUpdate() {
    if (scheduledRef.current) return;
    scheduledRef.current = true;

    requestAnimationFrame(() => {
      if (chartRef.current) {
        setOrderBookData(
          chartRef.current,
          orderBookRef.current.getSnapshot()
        );
      }
      scheduledRef.current = false;
    });
  }

  const ws = useWebSocket({
    onOpen: (ws) => {
      ws.send(
        JSON.stringify({
          ORDER_BOOK: ["BTCUSDC"],
        })
      );
    },
    onMessage: (msg) => {
      if (msg.type !== "orderBook") return;

      console.log(msg);

      const side:OrderBookSide =
      msg.side === "ORDER_SIDE_BUY"
        ? "bids"
        : "asks";

      orderBookRef.current.applyDelta(
        Number(msg.price),
        Number(msg.qtyChange),
        side,
      );

      scheduleChartUpdate();
    },
  });

  useEffect(() => {
    if (!containerRef.current) return;

    const instance = createOrderBookChart(containerRef.current);
    chartRef.current = instance;

    async function loadOrderBook() {
      try {
        const data = await fetchOrderBook("BTC", "USDC", 20);
        orderBookRef.current.loadSnapshot(data);
        setOrderBookData(instance, orderBookRef.current.getSnapshot());
        ws.connect();
      } catch (err) {
        console.error("Failed to fetch order book", err);
      }
    }

    loadOrderBook();

    return () => {
      instance.chart.dispose();
    };
  }, []);

  return (
    <main style={{ padding: 20 }}>
      <h1>Order Book Depth</h1>
      <div
        ref={containerRef}
        style={{ width: "100%", height: 400 }}
      />
    </main>
  );
}
