import requests
from typing import Any

class IngressApi:
    def __init__(self, account_id: int):
        self.url: str = "http://localhost:8888"
        self.headers: dict = {
            "Content-Type": "application/json"
        }

        self.account_id = account_id

    def add_account(self) -> dict:
        url = self.url + "/add_account"
        data = {"accountId": self.account_id}

        return self._post(url, data)

    def deposit(self, symbol: str, qty: int) -> dict[str, Any]:
        url = self.url + "/deposit"
        data = {
            "accountId": self.account_id,
            "symbol": symbol,
            "qty": qty
        }

        return self._post(url, data)

    def add_order(self,
            base: str,
            quote: str,
            order_side: str,
            order_type: str,
            price: int,
            qty: int
    ) -> dict[str, Any]:
        url = self.url + "/add_order"
        data = {
            "base": base,
            "quote": quote,
            "accountId": self.account_id,
            "orderSide": order_side,
            "orderType": order_type,
            "price": price,
            "qty": qty
        }

        return self._post(url, data)

    def _post(self, url: str, data: dict[str, Any]) -> dict[str, Any]:
        response = requests.post(url, json=data, headers=self.headers)
        #response.raise_for_status()

        return response.json()
