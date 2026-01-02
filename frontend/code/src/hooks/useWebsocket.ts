"use client";

import { useCallback, useRef, useState } from "react";

type WebSocketStatus = "idle" | "connecting" | "open" | "closed";

type UseWebSocketOptions = {
  onOpen?: (ws: WebSocket) => void;
  onMessage?: (data: any) => void;
  onClose?: () => void;
  onError?: (event: Event) => void;
};

export function useWebSocket(
  options: UseWebSocketOptions
) {
  const wsRef = useRef<WebSocket | null>(null);
  const [status, setStatus] = useState<WebSocketStatus>("idle");

  const url =
    process.env.NEXT_PUBLIC_WS_ENDPOINT || "ws://localhost:8980";

  const connect = useCallback(() => {
    if (wsRef.current) {
      wsRef.current.close();
      wsRef.current = null;
    }

    setStatus("connecting");

    const ws = new WebSocket(url);
    wsRef.current = ws;

    ws.onopen = () => {
      setStatus("open");
      options.onOpen?.(ws);
    };

    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        options.onMessage?.(data);
      } catch (err) {
        console.error("WebSocket message parse error", err);
      }
    };

    ws.onclose = () => {
      setStatus("closed");
      options.onClose?.();
    };

    ws.onerror = (event) => {
      options.onError?.(event);
    };
  }, [options, url]);

  const disconnect = useCallback(() => {
    wsRef.current?.close();
    wsRef.current = null;
    setStatus("closed");
  }, []);

  return {
    connect,
    disconnect,
    status,
    socket: wsRef.current,
  };
}
