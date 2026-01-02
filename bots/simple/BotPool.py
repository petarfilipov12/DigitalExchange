import threading
from dataclasses import dataclass

from SimpleBot import SimpleBot

@dataclass
class BotData:
    bot: SimpleBot
    thread: threading.Thread | None

class BotPool:
    def __init__(self,
                 bot_count: int = 5,
                 start_account_id: int = 100,
                 base: str = "BTC",
                 quote: str = "USDC",
                 mid_price: int = 100,
                 deviation_precent: float = 10,
                 qry_limit: int = 10
    ):
        self.bots: list[BotData] = list()

        for account_id in range(start_account_id, start_account_id + bot_count):
            self.bots.append(BotData(SimpleBot(account_id, base, quote, mid_price, deviation_precent, qry_limit), None))


    def start_on_thread(self):
        for bot in self.bots:
            bot.thread = bot.bot.start_on_thread()