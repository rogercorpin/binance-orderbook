# Real-Time Order Book Builder for Binance API

This Java Spring Boot program utilizes the Binance API to build and maintain real-time order books for the BTC/USDT and ETH/USDT trading pairs. The program updates the order books with a depth of 50 and calculates the total volume change in USDT for each trading pair every 10 seconds.

## Instructions
**Functionality**:
   - The program initializes order books for BTC/USDT and ETH/USDT with a depth of 50.
   - It connects to the Binance WebSocket API using the provided URL and listens for order book updates.
   - On receiving an order book update, it assumes the message format to be JSON and parses it to update the order book.
   - It calculates the total volume change in USDT for each trading pair.

**Volume Change Calculation**:
   - The volume in USDT is calculated as the sum of all buy/bid quantities multiplied by their respective prices, plus the sum of sell/ask quantities multiplied by their respective prices:
     ```
     volume_USDT = sum(bid_quantity * bid_price) + sum(ask_quantity * ask_price)
     ```
   - The volume change in USDT is the difference between the values at times T and T + 10 seconds.

**Output**:
   - The order book is printed in a column format with one pair of price and quantity per line.
   - The total volume change in USDT is printed for each trading pair every 10 seconds.