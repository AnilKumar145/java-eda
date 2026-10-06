# Lesson 3.3: Working with PostgreSQL and Relational Stores

---

## 1. Conceptual Foundation

PostgreSQL is widely recognized as the world's most advanced open-source relational database. For enterprise Java engineering, PostgreSQL provides rich native features beyond traditional SQL:
- Native **UUID** support for globally unique identifiers without collision risks.
- Native **JSONB** (binary JSON) for high-performance indexing of semi-structured clinical payloads.
- Arbitrary-precision **NUMERIC** arithmetic preventing floating-point inaccuracies.
- Timezone-aware temporal storage (**TIMESTAMP WITH TIME ZONE**).

Connecting Java applications to PostgreSQL is handled by the official **PostgreSQL JDBC Driver** (`org.postgresql.Driver`). The driver is a Type-4 pure Java driver that speaks the PostgreSQL frontend/backend protocol (V3.0) directly over TCP sockets.

```
+------------------------------------------------------------------------+
| Java Application (Spring Boot / Micronaut / Core Java)                 |
|   - UUID id = UUID.randomUUID();                                       |
|   - BigDecimal radiationDose = new BigDecimal("4.25");                 |
|   - Instant scanTime = Instant.now();                                  |
+-----------------------------------+------------------------------------+
                                    | PostgreSQL JDBC Driver (Type-4)
                                    v
+------------------------------------------------------------------------+
| PostgreSQL Wire Protocol V3.0 (TCP / SSL / Unix Socket)                |
+-----------------------------------+------------------------------------+
                                    |
                                    v
+------------------------------------------------------------------------+
| PostgreSQL 16+ Database Engine                                         |
|   - Table: radiology_studies                                           |
|   - Columns: id UUID, dose NUMERIC(6,2), scan_time TIMESTAMPTZ,        |
|              scanner_metadata JSONB                                    |
+------------------------------------------------------------------------+
```

---

## 2. Architecture & Internal Mechanics

### Connection URL Parameters
A production PostgreSQL JDBC URL configures security, timeouts, and connection behavior:
```
jdbc:postgresql://db.hospital.internal:5432/clinical_store?sslmode=verify-full&connectTimeout=5&socketTimeout=30&ApplicationName=ICUTelemetryService
```

Key configuration flags:
- `sslmode=verify-full`: Enforces TLS encryption and validates the server's TLS certificate against trusted CAs.
- `connectTimeout=5`: Aborts socket connection establishment if no TCP response within 5 seconds.
- `socketTimeout=30`: Terminates queries hanging longer than 30 seconds to protect against runaway queries.
- `ApplicationName=...`: Identifies the client service in PostgreSQL's administrative view (`pg_stat_activity`).

### Java to PostgreSQL Type Mapping Matrix

| PostgreSQL Type | Java Standard Type | JDBC Getter / Setter |
| :--- | :--- | :--- |
| `BIGINT` | `long` / `Long` | `rs.getLong(...)` / `pstmt.setLong(...)` |
| `VARCHAR` / `TEXT` | `String` | `rs.getString(...)` / `pstmt.setString(...)` |
| `UUID` | `java.util.UUID` | `rs.getObject(..., UUID.class)` / `pstmt.setObject(..., uuid)` |
| `NUMERIC` / `DECIMAL` | `java.math.BigDecimal` | `rs.getBigDecimal(...)` / `pstmt.setBigDecimal(...)` |
| `TIMESTAMPTZ` | `java.time.Instant` / `OffsetDateTime`| `rs.getObject(..., OffsetDateTime.class)` |
| `JSONB` / `JSON` | `String` / `PGobject` | `rs.getString(...)` / `pstmt.setObject(..., pgo)` |
| `BOOLEAN` | `boolean` / `Boolean` | `rs.getBoolean(...)` / `pstmt.setBoolean(...)` |

---

## 3. Real-World Code Implementation & Patterns

### 1. Handling NULL Values Correctly
In Java, primitive types cannot be `null`. When reading an `INTEGER` column containing `NULL`:
```java
// DANGEROUS: If heart_rate is NULL in DB, rs.getInt() returns 0!
int hr = rs.getInt("heart_rate"); // 0 could be mistaken for cardiac arrest!

// SAFE: Use wasNull() check
int rawHr = rs.getInt("heart_rate");
Integer safeHr = rs.wasNull() ? null : rawHr;

// OR SAFE: Use getObject with boxed type
Integer boxedHr = rs.getObject("heart_rate", Integer.class);
```

### 2. Working with UUID and JSONB in PostgreSQL
```java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.Instant;
import org.postgresql.util.PGobject;

public class RadiologyStudyDao {

    public void insertStudy(
        Connection conn, 
        UUID studyId, 
        String patientMrn, 
        BigDecimal radiationDoseMsv, 
        String metadataJson
    ) throws SQLException {
        String sql = """
            INSERT INTO radiology_studies (study_id, patient_mrn, radiation_dose_msv, metadata, created_at)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 1. UUID parameter
            pstmt.setObject(1, studyId);
            pstmt.setString(2, patientMrn);
            // 2. High-precision decimal
            pstmt.setBigDecimal(3, radiationDoseMsv);

            // 3. PostgreSQL JSONB mapping
            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(metadataJson);
            pstmt.setObject(4, jsonObject);

            // 4. Timestamp with time zone
            pstmt.setObject(5, Instant.now());

            pstmt.executeUpdate();
        }
    }
}
```

---

## 4. Failure Modes & Edge Cases

| Failure Mode | Root Cause | Engineering Solution |
| :--- | :--- | :--- |
| **Float Precision Loss** | Storing drug dosages or financial balances using `float`/`double`. | Always use `NUMERIC`/`DECIMAL` in SQL and `java.math.BigDecimal` in Java. |
| **Silent Zero for NULL** | Reading optional integers with `rs.getInt()` converts `NULL` to `0`. | Check `rs.wasNull()` or read via `rs.getObject(col, Integer.class)`. |
| **Missing Timezone Information** | Using `TIMESTAMP WITHOUT TIME ZONE` causes multi-facility timezone shifts. | Always use `TIMESTAMP WITH TIME ZONE` (`TIMESTAMPTZ`) in PostgreSQL schemas. |
| **Stale Sockets during Network Partition** | PostgreSQL server restarts, but Java pool holds broken TCP socket. | Configure `testOnBorrow` or `connectionTestQuery = "SELECT 1"`, and set `socketTimeout`. |

---

## 5. Performance & Optimization: High-Throughput Batch Writes

When loading 50,000 lab results, individual inserts create 50,000 network round-trips:
- Sequential: $50,000 \times 2\text{ms} = 100\text{ seconds}$.
- Batch (`addBatch()`): 500 batches of 100 records = **2.1 seconds** (a **48x speedup**)!

```java
public void insertBatch(Connection conn, List<LabResult> results) throws SQLException {
    String sql = "INSERT INTO lab_results (id, mrn, test_name, val) VALUES (?, ?, ?, ?)";
    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        int count = 0;
        for (LabResult r : results) {
            pstmt.setObject(1, r.id());
            pstmt.setString(2, r.mrn());
            pstmt.setString(3, r.testName());
            pstmt.setDouble(4, r.value());
            pstmt.addBatch();

            if (++count % 500 == 0) {
                pstmt.executeBatch(); // Flush batch every 500 records
            }
        }
        pstmt.executeBatch(); // Flush remainder
    }
}
```

---

## 6. Industry Best Practices & Production Patterns

1. **Always Use ANSI `COALESCE` for Fallbacks**:
   Instead of checking for `null` in Java application code:
   ```sql
   SELECT COALESCE(systolic, 120) AS safe_systolic FROM patient_vitals;
   ```
2. **PostgreSQL Server-Side Prepared Statement Caching**:
   Set `prepareThreshold=5` in the PostgreSQL JDBC connection string to tell the driver to cache execution plans on the server after the statement is executed 5 times.
3. **Use Explicit Schemas**:
   Always scope tables (e.g., `clinical.radiology_studies`) or set `currentSchema=clinical` to avoid search path spoofing.

---

## 7. Healthcare Domain Case Study: Radiology Imaging Store

Radiology Information Systems (RIS) manage CT and MRI scans. Each scan contains:
1. A unique DICOM Series Instance UID (UUID).
2. The measured radiation dosimetry (e.g., `4.85` mSv) which must be recorded with exact decimal precision for regulatory safety compliance.
3. Machine parameters (slice thickness, kilovoltage, gantry tilt) serialized as JSON.

Using PostgreSQL with Java JDBC allows the system to store radiation metrics in `NUMERIC(6,2)` without floating point rounding errors, while indexing scanner vendor parameters via `JSONB` for immediate retrospective safety audits.

---

## 8. Hands-On Verification & Knowledge Check

1. Why does `rs.getInt("optional_col")` pose safety hazards when dealing with nullable clinical data?
2. What are the key PostgreSQL connection string parameters that prevent Java applications from hanging indefinitely during network failures?
3. How does `PreparedStatement.addBatch()` achieve order-of-magnitude speedups compared to repeated `executeUpdate()` calls?
