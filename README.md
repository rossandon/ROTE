# Summary

This projects implements a stock exchange inspired from the "LMAX Architecture" article by Martin Fowler: https://martinfowler.com/articles/lmax.html.

Kafka is used in the place of the "Disruptor".

# Roadmap

- (DONE) Context persistence to S3
- (DONE) Deployment via AWS CDK (Cloud Development Kit)
- (DONE) HTTP API layer to send orders, fetch balances, etc.
- (DONE) Frontend to HTTP API & market data (see order book)
- Market data feed via Kafka + WebSockets
- Performance testing
- Investigate transactionality, idempotency & consistency