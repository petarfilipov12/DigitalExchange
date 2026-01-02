import { ApiCandle } from "@/lib/marketRestApi";
import {
  createChart,
  CandlestickSeries,
  ColorType,
  IChartApi,
  ISeriesApi,
  CandlestickData,
  UTCTimestamp,
} from "lightweight-charts";

export type CandleChartInstance = {
  chart: IChartApi;
  series: ISeriesApi<"Candlestick">;
};

export function createCandleChart(
  container: HTMLElement
): CandleChartInstance {
  const chart = createChart(container, {
    width: container.clientWidth,
    height: 400,
    layout: {
      textColor: "#eee", // light text
      background: {
        type: ColorType.Solid,
        color: "#111", // dark background
      },
    },
    grid: {
      vertLines: { color: '#222' },
      horzLines: { color: '#222' },
    },
    timeScale: {
      timeVisible: true,
      secondsVisible: false,
      rightOffset: 5,
      barSpacing: 12,
      borderColor: '#222', // border line
    },
  });

  const series = chart.addSeries(CandlestickSeries, {
    upColor: "#26a69a",
    downColor: "#ef5350",
    borderVisible: false,
    wickUpColor: "#26a69a",
    wickDownColor: "#ef5350",
  });

  return { chart, series };
}

export function setCandleData(
  instance: CandleChartInstance,
  data: CandlestickData<UTCTimestamp>[]
) {
  instance.series.setData(data);
  instance.chart.timeScale().fitContent();
}

export function updateCandleData(
  instance: CandleChartInstance,
  candle: CandlestickData<UTCTimestamp>
) {
  instance.series.update(candle);
}

export function transformCandleForChart(
  raw: ApiCandle
): CandlestickData<UTCTimestamp> {
  return {
      time: Math.floor(raw.startTimestamp / 1000) as UTCTimestamp,
      open: raw.open,
      high: raw.high,
      low: raw.low,
      close: raw.close,
    }
}

export function transformCandlesForChart(
  raw: ApiCandle[]
): CandlestickData<UTCTimestamp>[] {
  return raw
    .map((c) => (transformCandleForChart(c)))
    .sort((a, b) => a.time - b.time); // ascending order
}
