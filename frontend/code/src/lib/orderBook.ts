// lib/orderBook.ts

export type OrderBookSide = "bids" | "asks";

export type OrderBookLevel = {
  price: number;
  qty: number;
};

export type OrderBookSnapshot = {
  bids: OrderBookLevel[];
  asks: OrderBookLevel[];
};

export class OrderBook {
  private bids: Map<number, number>;
  private asks: Map<number, number>;

  constructor() {
    this.bids = new Map();
    this.asks = new Map();
  }

  /* ---------- Snapshot ---------- */

  loadSnapshot(snapshot: OrderBookSnapshot) {
    this.bids.clear();
    this.asks.clear();

    for (const b of snapshot.bids) {
      if (b.qty > 0) this.bids.set(b.price, b.qty);
    }

    for (const a of snapshot.asks) {
      if (a.qty > 0) this.asks.set(a.price, a.qty);
    }
  }

  /* ---------- Delta update ---------- */

  applyDelta(price: number, qtyChange: number, side: OrderBookSide) {
    const book =
      side === "bids" ? this.bids : this.asks;

    const prevQty = book.get(price) ?? 0;
    const nextQty = prevQty + qtyChange;

    if (nextQty <= 0) {
      book.delete(price);
    } else {
      book.set(price, nextQty);
    }
  }

  /* ---------- Export for charts ---------- */

  getBids(): OrderBookLevel[] {
    return Array.from(this.bids.entries())
      .map(([price, qty]) => ({ price, qty }))
      .sort((a, b) => b.price - a.price);
  }

  getAsks(): OrderBookLevel[] {
    return Array.from(this.asks.entries())
      .map(([price, qty]) => ({ price, qty }))
      .sort((a, b) => a.price - b.price);
  }

  getSnapshot(): OrderBookSnapshot {
    return {
      bids: this.getBids(),
      asks: this.getAsks(),
    };
  }

  /* ---------- Optional helpers ---------- */

  getBestBid(): number | null {
    if (this.bids.size === 0) return null;
    return Math.max(...this.bids.keys());
  }

  getBestAsk(): number | null {
    if (this.asks.size === 0) return null;
    return Math.min(...this.asks.keys());
  }

  getMidPrice(): number | null {
    const bid = this.getBestBid();
    const ask = this.getBestAsk();
    if (bid === null || ask === null) return null;
    return (bid + ask) / 2;
  }
}
