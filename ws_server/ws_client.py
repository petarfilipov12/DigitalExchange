import asyncio, websockets

async def test():
    async with websockets.connect("ws://localhost:8980") as ws:
        await ws.send('{"ORDER_BOOK":["BTCUSDC"], "TRADES":["BTCUSDC"]}')
        async for msg in ws:
            print(msg)

asyncio.run(test())

