# Learning Outcomes: Unit 3.2 - Java Database Connectivity (JDBC)

By the end of this unit, you will be able to:

1. **Understand JDBC Architecture**: Deconstruct the Java Database Connectivity layer, the role of `java.sql` interfaces, and how Type-4 pure Java network drivers establish socket connections to relational database servers.
2. **Master `Connection`, `Statement`, and `PreparedStatement`**: Differentiate raw statements from compiled parameterized statements, explain server-side statement compilation and caching, and eliminate SQL injection vulnerabilities.
3. **Navigate and Extract Data with `ResultSet`**: Master cursor iteration with `rs.next()`, typed accessors (`getInt()`, `getString()`, `getTimestamp()`, `getBigDecimal()`), and inspect schema dynamically via `ResultSetMetaData`.
4. **Control ACID Transaction Boundaries**: Disable autocommit (`conn.setAutoCommit(false)`), atomically execute multi-table mutations, execute programmatic `conn.commit()`, and roll back gracefully on runtime failures (`conn.rollback()`).
5. **Implement Connection Pooling Fundamentals**: Explain why physical socket handshakes (TCP 3-way handshake + TLS + DB authentication) are costly, and how object pooling amortizes connection creation overhead in high-throughput enterprise systems.
