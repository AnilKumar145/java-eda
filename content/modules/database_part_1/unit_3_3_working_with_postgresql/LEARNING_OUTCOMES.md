# Learning Outcomes: Unit 3.3 - Working with PostgreSQL and Relational Stores

By the end of this unit, you will be able to:

1. **Configure PostgreSQL JDBC Driver**: Understand `org.postgresql.Driver`, JDBC URL connection parameters (`sslmode`, `connectTimeout`, `socketTimeout`, `ApplicationName`), and environment-driven credentials.
2. **Map Rich PostgreSQL Datatypes to Java**: Seamlessly map PostgreSQL datatypes to Java standard types:
   - `UUID` -> `java.util.UUID`
   - `NUMERIC` / `DECIMAL` -> `java.math.BigDecimal`
   - `TIMESTAMPTZ` -> `java.time.Instant` or `java.time.OffsetDateTime`
   - `JSON` / `JSONB` -> `PGobject` or JSON String via Jackson/standard parsers
3. **Handle SQL `NULL` Safely**: Avoid silent zero coercion in Java primitive types (`getInt()` returning `0` for `NULL`) by leveraging `rs.wasNull()` or reading boxed types. Use ANSI SQL `COALESCE` for default values.
4. **Implement High-Throughput Batch Writes**: Group write statements using `PreparedStatement.addBatch()` and `executeBatch()` to optimize network utilization.
5. **Ensure Uncompromising Resource Management**: Structure multi-resource database logic with Java's try-with-resources statement to guarantee socket and memory reclamation.
