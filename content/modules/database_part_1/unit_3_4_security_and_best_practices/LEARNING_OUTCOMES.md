# Learning Outcomes: Unit 3.4 - Database Security, Transactions, and Best Practices

By the end of this unit, you will be able to:

1. **Eliminate SQL Injection Vulnerabilities**: Explain the mechanics of tautology attacks, UNION injection, and stacked queries. Implement parameterized `PreparedStatement` queries and enforce strict whitelist validation for dynamic SQL identifiers (table and column names).
2. **Execute Multi-Stage ACID Transactions**: Configure isolation levels (`TRANSACTION_READ_COMMITTED`, `TRANSACTION_SERIALIZABLE`) and orchestrate multi-step business operations that maintain atomicity, consistency, isolation, and durability.
3. **Master Partial Rollbacks via `Savepoint`**: Create intermediate transaction savepoints (`conn.setSavepoint()`), gracefully roll back sub-operations (`conn.rollback(savepoint)`) without aborting previously committed units of work within the transaction.
4. **Deploy Enterprise Connection Pooling with HikariCP**: Configure `HikariConfig` and `HikariDataSource` with production-grade settings:
   - `maximumPoolSize`: Tuned to CPU core count and storage IOPS
   - `connectionTimeout`: Fail-fast threshold under load spikes
   - `leakDetectionThreshold`: Automated tracking of unclosed connections
5. **Diagnose and Handle `SQLException`**: Extract SQLState strings, vendor error codes, and parse chained exceptions (`e.getNextException()`) to distinguish transient lock timeouts from unrecoverable schema violations.
