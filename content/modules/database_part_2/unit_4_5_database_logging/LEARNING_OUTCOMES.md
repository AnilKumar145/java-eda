# Learning Outcomes: Unit 4.5 - Database Logging and Diagnostics

By the end of this unit, you will be able to:

1. **Configure Hibernate SQL Logging**: Fine-tune Logback/Log4j2 configurations for `org.hibernate.SQL` and `org.hibernate.orm.jdbc.bind` to inspect generated SQL and bound query parameters without compromising sensitive patient PII.
2. **Profile Statement Execution Latency**: Intercept JDBC calls and measure execution times to identify latency spikes and unindexed query scans.
3. **Build Slow Query Watchdogs**: Establish threshold alerting that flags queries exceeding acceptable service-level objectives (SLOs) (e.g., > 100 ms).
4. **Monitor HikariCP Connection Pools**: Retrieve real-time connection pool metrics via `HikariPoolMXBean` (active, idle, pending threads, total connections) to detect pool starvation.
5. **Diagnose Connection Leaks**: Configure `leakDetectionThreshold` to catch unclosed `Connection` or `EntityManager` instances before pool exhaustion crashes production.
6. **Implement Entity Audit Stamping**: Apply JPA entity auditing patterns to track creation timestamps, last modified timestamps, and mutation versions across business transactions.
