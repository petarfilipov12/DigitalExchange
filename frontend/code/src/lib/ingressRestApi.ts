
export type Order = {
  id: number;
  orderSide: string;
  orderType: string;
  price: number;
  qty: number;
  filled: number;
};

export type ApiResponse = {
    response: string;
    order: Order;
} | {
    response: string;
    order?: never;
};


export async function addOrder(
  base: string,
  quote: string,
  accountId: number, 
  orderSide: string, 
  orderType: string, 
  price: number,
  qty: number,
): Promise<ApiResponse> {
  const url = `${process.env.NEXT_PUBLIC_REST_INGRESS_ENDPOINT}/add_order`;

  const body = JSON.stringify({
      base,
      quote,
      accountId,
      orderSide,
      orderType,
      price,
      qty,
    });

  console.log("addOrder body:", body);

  const data = await sendRequest<ApiResponse>(url, body);

  return data;
}

async function sendRequest<T>(url: string, body: any): Promise<T> {
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
