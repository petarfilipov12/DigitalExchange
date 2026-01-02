"use client";

import {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useRef,
} from "react";
import { fetchOrderBook } from "@/lib/marketRestApi";
import { OrderBook, OrderBookSide } from "@/lib/orderBook";
import {
  createOrderBookChart,
  setOrderBookData,
} from "@/lib/orderBookChartDriver";

export type OrderBookUpdateMsg = {
  type: "orderBook";
  side: "ORDER_SIDE_BUY" | "ORDER_SIDE_SELL";
  price: string;
  qtyChange: string;
};

export type OrderBookChartHandle = {
  onOrderBookUpdate: (msg: OrderBookUpdateMsg) => void;
};

const OrderBookChart = forwardRef<OrderBookChartHandle>((_, ref) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const chartRef =
    useRef<ReturnType<typeof createOrderBookChart> | null>(null);
  const orderBookRef = useRef(new OrderBook());
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

  useImperativeHandle(ref, () => ({
    onOrderBookUpdate(msg) {
      if (!chartRef.current) return;

      const side: OrderBookSide =
        msg.side === "ORDER_SIDE_BUY" ? "bids" : "asks";

      const price = Number(msg.price);
      const qtyChange = Number(msg.qtyChange);
      if (!Number.isFinite(price) || !Number.isFinite(qtyChange)) return;

      orderBookRef.current.applyDelta(
        price,
        qtyChange,
        side
      );

      scheduleChartUpdate();
    },
  }));

  useEffect(() => {
    if (!containerRef.current) return;

    const chart = createOrderBookChart(containerRef.current);
    chartRef.current = chart;

    let cancelled = false;

    async function loadSnapshot() {
      try {
        const snapshot = await fetchOrderBook("BTC", "USDC", 20);
        if (cancelled) return;

        orderBookRef.current.loadSnapshot(snapshot);
        setOrderBookData(chart, orderBookRef.current.getSnapshot());
      } catch (err) {
        console.error("Failed to fetch order book", err);
      }
    }

    loadSnapshot();

    return () => {
      cancelled = true;
      chart.chart.dispose();
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

OrderBookChart.displayName = "OrderBookChart";
export default OrderBookChart;
