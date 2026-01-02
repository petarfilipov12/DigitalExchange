import time

from BotPool import BotPool

def main():
    bot_pool = BotPool()
    bot_pool.start_on_thread()

    while True:
        time.sleep(1)

if __name__ == '__main__':
    main()

