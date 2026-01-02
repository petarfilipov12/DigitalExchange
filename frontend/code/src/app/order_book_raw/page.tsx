"use client";

import { useEffect, useState } from "react";
import { fetchOrderBook, OrderBook, OrderBookEntry } from "@/lib/marketRestApi";

export default function OrderBookPage() {
  const [orderBook, setOrderBook] = useState<OrderBook | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadOrderBook = async () => {
    setLoading(true);
    setError(null);

    try {
      const data = await fetchOrderBook("BTC", "USDC", 10);
      setOrderBook(data);
    } catch (err: any) {
      setError(err.message || "Failed to fetch order book");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrderBook();
  }, []);

  return (
    <main style={{ padding: 20 }}>
      <h1>Order Book</h1>
      {loading && <p>Loading...</p>}
      {error && <p style={{ color: "red" }}>{error}</p>}

      {orderBook && (
        <div style={{ display: "flex", gap: 40 }}>
          <div>
            <h2>Bids</h2>
            <table>
              <thead>
                <tr>
                  <th>Price</th>
                  <th>Qty</th>
                </tr>
              </thead>
              <tbody>
                {orderBook.bids.map((entry: OrderBookEntry, i) => (
                  <tr key={i}>
                    <td>{entry.price}</td>
                    <td>{entry.qty}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div>
            <h2>Asks</h2>
            <table>
              <thead>
                <tr>
                  <th>Price</th>
                  <th>Qty</th>
                </tr>
              </thead>
              <tbody>
                {orderBook.asks.map((entry: OrderBookEntry, i) => (
                  <tr key={i}>
                    <td>{entry.price}</td>
                    <td>{entry.qty}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      <button onClick={loadOrderBook} style={{ marginTop: 20 }}>
        Refresh
      </button>
    </main>
  );
}
