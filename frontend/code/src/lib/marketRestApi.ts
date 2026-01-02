export type OrderBookEntry = {
  price: number;
  qty: number;
}

export type OrderBook = {
  bids: OrderBookEntry[];
  asks: OrderBookEntry[];
}

export type ApiCandle = {
  startTimestamp: number; // ms
  endTimestamp: number; //ms
  open: number;
  high: number;
  low: number;
  close: number;
  volume: number;
};

export async function fetchOrderBook(
  base: string,
  quote: string,
  depth = 10
): Promise<OrderBook> {
  const url = `${process.env.NEXT_PUBLIC_REST_MARKETDATA_ENDPOINT}/order_book`;

  const body = JSON.stringify({
      base,
      quote,
      depth,
    });

  const dataRaw = await get<{ bids: [number, number][]; asks: [number, number][] }>(url, body);

  const data: OrderBook = {
    bids: dataRaw.bids.map(([price, qty]) => ({ price, qty })),
    asks: dataRaw.asks.map(([price, qty]) => ({ price, qty })),
  };

  return data;
}

export async function fetchCandles(
  base: string,
  quote: string,
  interval: string,
  limit = 10
): Promise<ApiCandle[]> {
  const url = `${process.env.NEXT_PUBLIC_REST_MARKETDATA_ENDPOINT}/candles`;

  const body = JSON.stringify({
      base,
      quote,
      interval,
      limit,
    });

  const res = await fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      base,
      quote,
      interval,
      limit,
    }),
  });

  const data = await get<ApiCandle[]>(url, body);

  return data;
}

async function get<T>(url: string, body: any): Promise<T> {
    const res = await fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: body,
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(`HTTP ${res.status}: ${text}`);
  }

  const data: T = await res.json();
  return data;
}
