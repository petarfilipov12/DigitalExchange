import time
import random
import threading

from IngressApi import IngressApi

class SimpleBot:
    def __init__(self, account_id: int, base: str, quote: str, mid_price: int, deviation_precent: float, qry_limit: int):
        self.api = IngressApi(account_id)
        self.base = base
        self.quote = quote

        deviation = self._calc_deviation(mid_price, deviation_precent)
        self.price_gen_range_start = mid_price - deviation
        self.price_gen_range_end = mid_price + deviation

        self.qty_limit = qry_limit

        self.api.add_account()
        self.api.deposit(base, 100000)
        self.api.deposit(quote, 100000)

    def loop(self):
        while True:
            self.api.add_order(
                self.base,
                self.quote,
                self._get_order_side(),
                self._get_order_type(),
                self._get_price(),
                self._get_qty()
            )

            time.sleep(1)

    def start_on_thread(self) -> threading.Thread:
        thread = threading.Thread(target=self.loop, daemon=True)
        thread.start()

        return thread

    def _calc_deviation(self, mid_price: int, deviation_precent: float) -> int:
        if deviation_precent == 0:
            return 0

        deviation = (mid_price * deviation_precent) / 100
        return int(deviation)

    def _get_order_side(self) -> str:
        return random.choice(["BUY", "SELL"])

    def _get_order_type(self) -> str:
        return "LIMIT"

    def _get_price(self):
        return random.randint(self.price_gen_range_start, self.price_gen_range_end)

    def _get_qty(self):
        return random.randint(1, self.qty_limit)