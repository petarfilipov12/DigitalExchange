// lib/orderBookChart.ts
import * as echarts from "echarts";
import { OrderBook } from "@/lib/marketRestApi";

export type OrderBookChartInstance = {
  chart: echarts.ECharts;
};

export function createOrderBookChart(
  container: HTMLDivElement
): OrderBookChartInstance {
  const chart = echarts.init(container);

  chart.setOption({
    backgroundColor: "#111",
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "cross",
      },
      textStyle: { color: "#eee" },
    },
    grid: {
      left: 60,
      right: 20,
      top: 20,
      bottom: 40,
    },
    xAxis: {
      type: "value",
      name: "Price",
      scale: true,
      axisLine: { lineStyle: { color: "#222" } },
      splitLine: { lineStyle: { color: "#222" } },
      axisLabel: { color: "#eee" },
      nameTextStyle: { color: "#eee" },
    },
    yAxis: {
      type: "value",
      name: "Qty",
      scale: true,
      axisLine: { lineStyle: { color: "#222" } },
      splitLine: { lineStyle: { color: "#222" } },
      axisLabel: { color: "#eee" },
      nameTextStyle: { color: "#eee" },
    },
    series: [
      {
        name: "Bids",
        type: "line",
        step: "end",
        areaStyle: {},
        emphasis: { focus: "series" },
        data: [],
        itemStyle: { color: "#26a69a" },
      },
      {
        name: "Asks",
        type: "line",
        step: "start",
        areaStyle: {},
        emphasis: { focus: "series" },
        data: [],
        itemStyle: { color: "#ef5350" },
      },
    ],
  });

  return { chart };
}

export function setOrderBookData(
  instance: OrderBookChartInstance,
  orderBook: OrderBook
) {
  const bids = [...orderBook.bids]
    .sort((a, b) => b.price - a.price)
    .map((b) => [b.price, b.qty]);

  const asks = [...orderBook.asks]
    .sort((a, b) => a.price - b.price)
    .map((a) => [a.price, a.qty]);

  instance.chart.setOption(
    {
      series: [
        { data: bids },
        { data: asks },
      ],
    },
    { notMerge: false, lazyUpdate: true }
  );
}
