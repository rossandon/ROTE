# Summary

This projects implements a stock exchange inspired from the "LMAX Architecture" article by Martin Fowler: https://martinfowler.com/articles/lmax.html.

Kafka is used in the place of the "Disruptor".

# Roadmap

- Context persistence to S3
- HTTP API layer to send orders, fetch balances, etc.
- Market data feed via Kafka + WebSockets
- Deployment via AWS CDK (Cloud Development Kit)
- Frontend to HTTP API & market data (see order book)
- Performance testing