# Lesson 3.4: Database Security, Transactions, and Best Practices

---

## 1. Conceptual Foundation

Database engineering in production environments carries two non-negotiable responsibilities: **Information Security** and **Transactional Integrity**.

A vulnerability in the database access layer can compromise millions of confidential records, violating privacy laws such as HIPAA and GDPR. Concurrently, a bug in transaction handling can corrupt patient balances, duplicate medication orders, or lock database tables.

```
+------------------------------------------------------------------------+
|                          Attacker Payload Input                        |
|                     "' OR '1'='1'; DROP TABLE vitals; --"              |
+-----------------------------------+------------------------------------+
                                    |
                 +------------------+------------------+
                 |                                     |
                 v (VULNERABLE)                        v (SECURE)
+---------------------------------+   +---------------------------------+
| String Concatenation            |   | PreparedStatement + ? Placeholder|
| "SELECT * FROM p WHERE id = "   |   | pstmt.setString(1, input);      |
|    + input;                     |   |                                 |
|                                 |   | DB engine treats input as       |
| Parser interprets payload as    |   | purely literal character data.  |
| executable SQL command!         |   | SQL AST is never modified!      |
| CATASTROPHIC COMPROMISE         |   | ATTACK COMPLETELY NEUTRALIZED   |
+---------------------------------+   +---------------------------------+
```

---

## 2. Architecture & Internal Mechanics

### How Parameterized Queries Neutralize SQL Injection
When a query string is sent to the database via `PreparedStatement`:
1. **Compilation Phase**: The database engine parses the SQL string containing parameter placeholders (`?`) into an Abstract Syntax Tree (AST) and compiles an execution plan.
2. **Binding Phase**: The parameter values are transmitted across the wire in a separate data packet.
3. The database engine inserts values into pre-compiled AST leaf slots as **pure data literals**. It never re-parses or re-compiles the SQL command. Even if the parameter contains single quotes, semicolons, or SQL keywords (`DROP TABLE`), the engine treats them strictly as literal text characters.

### Dynamic Identifiers: The Limitation of Placeholders
SQL parameter markers (`?`) can **only** replace values. They **cannot** be used for:
- Table names: `SELECT * FROM ?` -> **SYNTAX ERROR**
- Column names: `SELECT ? FROM patients` -> Replaces column with a string literal!
- Ordering directions: `ORDER BY created_at ?` -> **SYNTAX ERROR**

When applications require dynamic sorting or table selection, you **must use strict whitelist validation**:
```java
private static final Set<String> ALLOWED_COLUMNS = Set.of("full_name", "mrn", "created_at");

public String getSafeOrderClause(String userRequestedColumn) {
    if (!ALLOWED_COLUMNS.contains(userRequestedColumn)) {
        throw new IllegalArgumentException("Unauthorized sort column: " + userRequestedColumn);
    }
    return "ORDER BY " + userRequestedColumn + " ASC";
}
```

---

## 3. Real-World Code Implementation & Patterns

### 1. Savepoint Partial Rollbacks
In complex enterprise workflows, you may wish to attempt an optional secondary action (such as sending a notification or recording a loyalty badge) without failing the primary financial or clinical transaction.

```java
public void processPrescriptionDispense(Connection conn, long rxId, long patientId) throws SQLException {
    boolean origAutoCommit = conn.getAutoCommit();
    conn.setAutoCommit(false);

    try {
        // Step 1: Mandatory Core Action - Decrement pharmacy inventory
        try (PreparedStatement stmt1 = conn.prepareStatement(
                "UPDATE inventory SET stock = stock - 1 WHERE rx_id = ? AND stock > 0")) {
            stmt1.setLong(1, rxId);
            if (stmt1.executeUpdate() == 0) {
                throw new IllegalStateException("Out of stock");
            }
        }

        // Step 2: Establish intermediate Savepoint
        Savepoint billingSavepoint = conn.setSavepoint("BillingStage");

        try {
            // Attempt automated copay deduction
            try (PreparedStatement stmt2 = conn.prepareStatement(
                    "INSERT INTO copay_deductions (patient_id, amount) VALUES (?, 25.00)")) {
                stmt2.setLong(1, patientId);
                stmt2.executeUpdate();
            }
        } catch (SQLException billingEx) {
            // Partial rollback: revert copay deduction, but KEEP inventory decrement!
            System.err.println("Copay deduction failed, rolling back to savepoint: " + billingEx.getMessage());
            conn.rollback(billingSavepoint);
        }

        // Commit transaction (inventory remains decremented!)
        conn.commit();
    } catch (Exception ex) {
        conn.rollback();
        throw ex;
    } finally {
        conn.setAutoCommit(origAutoCommit);
    }
}
```

### 2. Enterprise Connection Pooling with HikariCP
HikariCP is the fastest, most reliable connection pool in the Java ecosystem.

```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabasePoolFactory {

    public static DataSource createHikariPool(String jdbcUrl, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        // Core Sizing: pool_size = ((core_count * 2) + effective_spindle_count)
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);

        // Fail fast: throw exception if connection not acquired in 3 seconds
        config.setConnectionTimeout(3000);

        // Max lifetime before proactive recycling (30 minutes)
        config.setMaxLifetime(1800000);

        // Automated leak detection: warn if connection borrowed longer than 5 seconds
        config.setLeakDetectionThreshold(5000);

        return new HikariDataSource(config);
    }
}
```

---

## 4. Failure Modes & Edge Cases

| Failure Mode | Root Cause | Engineering Solution |
| :--- | :--- | :--- |
| **SQL Injection via String Formatting** | Using `String.format()` or `+` to embed input into SQL queries. | Ban raw string formatting for SQL. Enforce `PreparedStatement`. |
| **Connection Leak** | Forgetting to close connections in rare error branches. | Use try-with-resources and set HikariCP `leakDetectionThreshold`. |
| **Deadlock on Unordered Locks** | Thread A locks row 1 then row 2; Thread B locks row 2 then row 1. | Always acquire locks in deterministic order (e.g. sorted by primary key). |
| **Dirty Reads** | Reading uncommitted rows from concurrently active transactions. | Enforce `TRANSACTION_READ_COMMITTED` or `TRANSACTION_SERIALIZABLE`. |

---

## 5. Performance & Optimization: Connection Pool Sizing

A common fallacy is assuming: *"More connections in the pool = higher performance"*.
In reality, when connection count exceeds the database server's physical CPU core and disk I/O capacity:
1. The operating system spends more CPU time on **thread context switching** than on query execution.
2. Disk queue depths exceed cache thresholds, inducing write thrashing.

The famous PostgreSQL pool sizing formula:
$$\text{Pool Size} = (\text{CPU Cores} \times 2) + \text{Effective Spindles / Disks}$$

For an 8-core database server with an SSD array, a pool of **16 to 20 connections** will outperform a pool of 200 connections by orders of magnitude while consuming significantly less memory.

---

## 6. Industry Best Practices & Production Patterns

1. **Keep Transactions as Short as Possible**: Never perform network HTTP calls, disk writes, or slow external computations inside an open database transaction.
2. **Fail Fast with Connection Timeouts**: Set `connectionTimeout = 3000ms`. If the pool is exhausted, fail fast and return HTTP 503 instead of hanging client threads indefinitely.
3. **Always Check SQLState in Exceptions**:
   - `40001`: Serialization failure / deadlock victim (retry the transaction).
   - `23505`: Unique constraint violation (duplicate key).
   - `08006`: Connection failure.

---

## 7. Healthcare Domain Case Study: Prescription Audit Ledger

At St. Jude Metropolitan Hospital, dispensing controlled pharmaceuticals (e.g., morphine, fentanyl) requires strict dual-phase transactional guarantees:
1. The prescription inventory must be decremented atomically.
2. The prescription audit event must be committed to the tamper-evident ledger.
3. If the auxiliary insurance billing gateway times out, the system uses a **Savepoint** to roll back the billing stage and flag the prescription for retroactive batch adjudication without canceling the urgent patient medication delivery.

---

## 8. Hands-On Verification & Knowledge Check

1. Why cannot SQL parameter placeholders (`?`) be used for table or column names?
2. What is the operational benefit of configuring HikariCP's `leakDetectionThreshold`?
3. How does a `Savepoint` rollback differ from a full `conn.rollback()` call?
