# Lesson 3.2: Java Database Connectivity (JDBC) Architecture and Core Interfaces

---

## 1. Conceptual Foundation

Java Database Connectivity (**JDBC**) is the standard Java API that connects Java applications to relational database management systems. Defined in packages `java.sql` and `javax.sql`, JDBC is an **abstraction barrier**: it provides a uniform set of interfaces so that your application code does not need to change whether you connect to PostgreSQL, Oracle, MySQL, SQLite, or H2.

```
+------------------------------------------------------------------------+
|                      Java Application Code                             |
|         (Uses standard java.sql interfaces: Connection, etc.)          |
+-----------------------------------+------------------------------------+
                                    |
                                    v
+------------------------------------------------------------------------+
|                           JDBC API Layer                               |
|       (DriverManager, Connection, Statement, PreparedStatement, RS)    |
+-----------------------------------+------------------------------------+
                                    |
                 +------------------+------------------+
                 |                                     |
                 v                                     v
+---------------------------------+   +---------------------------------+
|   PostgreSQL JDBC Driver        |   |      H2 Database Engine         |
|   (Type-4 Pure Java Driver)     |   |    (In-Memory / Embedded)       |
+----------------+----------------+   +----------------+----------------+
                 | TCP / Wire                          | In-Process Direct
                 v                                     v
+---------------------------------+   +---------------------------------+
|   PostgreSQL Server (Port 5432) |   |    JVM Memory Storage Engine    |
+---------------------------------+   +---------------------------------+
```

### The 4 Types of JDBC Drivers
Historically, the JDBC specification defined four driver classifications:
1. **Type 1 (JDBC-ODBC Bridge)**: Translates JDBC calls to ODBC calls (obsolete, removed since Java 8).
2. **Type 2 (Native-API Driver)**: Translates JDBC calls into vendor-specific C/C++ client libraries (requires native binaries installed on every client machine).
3. **Type 3 (Network-Protocol Driver)**: Translates JDBC calls into an intermediate middleware protocol.
4. **Type 4 (Pure Java Direct Network Driver)**: Modern standard! Pure Java driver that speaks the database engine's native socket wire protocol directly (e.g., PostgreSQL JDBC Driver, MySQL Connector/J). Zero native OS dependencies required.

---

## 2. Architecture & Internal Mechanics

### Core JDBC Interfaces & Lifecycles

1. **`DriverManager` vs `DataSource`**:
   - `DriverManager`: The legacy factory for creating direct connections using JDBC URLs (`jdbc:postgresql://localhost:5432/mydb`).
   - `DataSource`: The modern, preferred enterprise interface from `javax.sql`. Decouples connection credentials and supports transparent connection pooling.

2. **`Connection`**:
   - Represents an established physical or logical network session to the database.
   - Manages transaction boundary controls (`setAutoCommit(boolean)`).

3. **`Statement` vs `PreparedStatement`**:
   - `Statement`: Executes unparameterized, static SQL strings. Highly vulnerable to SQL injection if used with dynamic strings.
   - `PreparedStatement`: Pre-compiles the SQL template on the database server. Values are bound separately via placeholders (`?`). Guarantees zero SQL injection and enables the database engine to reuse cached execution query plans.

4. **`ResultSet`**:
   - A cursor pointing to the active row of query tabular results.
   - Cursor starts *before* the first row. Calling `rs.next()` advances to the next row and returns `false` when no more rows exist.
   - Provides strongly-typed getters: `rs.getLong(1)`, `rs.getString("patient_id")`, `rs.getBigDecimal("dosage")`.

---

## 3. Real-World Code Implementation & Patterns

### 1. Robust JDBC Query with Try-with-Resources
Every JDBC resource (`Connection`, `PreparedStatement`, `ResultSet`) implements `AutoCloseable`. Failing to close them leaks database handles and sockets, eventually crashing the application.

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDao {

    private final String jdbcUrl;
    private final String user;
    private final String password;

    public PatientDao(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    public List<String> findActivePatientsByDepartment(String department) throws SQLException {
        String sql = """
            SELECT full_name 
            FROM patients 
            WHERE department = ? AND is_active = TRUE
            ORDER BY full_name ASC
            """;

        List<String> names = new ArrayList<>();

        // Try-with-resources closes ResultSet, PreparedStatement, and Connection in reverse order
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, department);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("full_name"));
                }
            }
        }
        return names;
    }
}
```

### 2. Transaction Management Pattern (Commit & Rollback)
```java
public void transferPrescription(long fromPatientId, long toPatientId, long medicationId) throws SQLException {
    String deductSql = "UPDATE patient_inventory SET quantity = quantity - 1 WHERE patient_id = ? AND med_id = ?";
    String creditSql = "UPDATE patient_inventory SET quantity = quantity + 1 WHERE patient_id = ? AND med_id = ?";

    try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password)) {
        // 1. Disable autocommit to start a manual transaction
        conn.setAutoCommit(false);

        try (PreparedStatement deductStmt = conn.prepareStatement(deductSql);
             PreparedStatement creditStmt = conn.prepareStatement(creditSql)) {

            deductStmt.setLong(1, fromPatientId);
            deductStmt.setLong(2, medicationId);
            int rowsDeducted = deductStmt.executeUpdate();
            if (rowsDeducted == 0) {
                throw new IllegalStateException("Insufficient inventory for transfer");
            }

            creditStmt.setLong(1, toPatientId);
            creditStmt.setLong(2, medicationId);
            creditStmt.executeUpdate();

            // 2. Commit transaction atomically
            conn.commit();
        } catch (Exception ex) {
            // 3. Roll back changes on any failure
            conn.rollback();
            throw new SQLException("Transaction aborted, changes rolled back", ex);
        } finally {
            // 4. Always restore autocommit state
            conn.setAutoCommit(true);
        }
    }
}
```

---

## 4. Failure Modes & Edge Cases

| Failure Mode | Symptoms | Resolution |
| :--- | :--- | :--- |
| **Connection Leak** | `"Too many connections"` or `"Connection pool exhausted"` after running under load. | Always use try-with-resources blocks to guarantee sockets are closed even when exceptions occur. |
| **Calling `rs.get*()` before `rs.next()`** | Throws `SQLException: Invalid cursor state - no current row`. | Always call `if (rs.next())` or `while (rs.next())` before reading data. |
| **`Statement` vs `PreparedStatement` Confusion** | Concatenating user strings into queries opens catastrophic SQL injection vulnerabilities. | Ban `Statement` for any queries with input parameters. Enforce `PreparedStatement`. |
| **Autocommit Accidentally Enabled** | Multi-table updates partially write to disk, leaving orphaned records if step 2 throws an error. | Explicitly invoke `conn.setAutoCommit(false)` before the first modification. |

---

## 5. Performance & Optimization: Connection Pooling Basics

Creating a physical database connection is one of the most expensive operations in software engineering:
1. TCP 3-way handshake (`SYN`, `SYN-ACK`, `ACK`).
2. TLS handshake (certificates, cipher negotiation).
3. Database server process/thread spawn.
4. Authentication verification (username/password hash verification).
5. Initial session parameter negotiation (`client_encoding`, `timezone`).

**Latency Comparison**:
- New physical connection creation: **30ms – 100ms**
- Reusing an idle connection from a pool: **< 0.1ms (100 microseconds)**

A **Connection Pool** pre-allocates a set of active connections and lends them to worker threads. When the thread finishes, calling `connection.close()` does *not* close the physical socket; instead, it returns the connection back to the pool ready for the next request.

---

## 6. Industry Best Practices & Production Patterns

1. **Always Use 1-Based Indexing**: JDBC parameter markers (`?`) and `ResultSet` columns are indexed starting from **1**, not **0**. Passing index 0 throws `SQLException`.
2. **Use Batch Execution for Bulk Inserts**: Instead of executing 1,000 individual `stmt.executeUpdate()` calls, use `stmt.addBatch()` and `stmt.executeBatch()`. This reduces network round-trips from 1,000 to 1!
3. **Inspect Metadata Dynamically**: Use `ResultSetMetaData rsmd = rs.getMetaData()` to discover column counts, types, and column names when writing generic serializers.

---

## 7. Healthcare Domain Case Study: Clinic Telemetry Ingestion Pipeline

Consider an Intensive Care Unit (ICU) central monitoring station. 50 bedside monitors emit heart rate and arterial pressure metrics every second (50 writes/sec).

If the telemetry service opened and closed a brand new database connection for each metric packet, database connection pools would exhaust within seconds, and CPU utilization on the database server would spike to 100% just managing authentication handshakes.

By using a pooled JDBC architecture with parameterized batch statements (`PreparedStatement.addBatch()`), the pipeline bundles incoming vital bursts and flushes 1,000 records in a single network transmission with sub-10 millisecond database latency.

---

## 8. Hands-On Verification & Knowledge Check

1. Why are Type-4 JDBC drivers preferred in modern enterprise deployments over Type 2 drivers?
2. What happens to physical database sockets when `connection.close()` is called on a pooled `Connection`?
3. What is the fundamental difference between `stmt.executeQuery()` and `stmt.executeUpdate()`?
