# Lesson 3.1: Relational Architecture and Database Fundamentals

---

## 1. Conceptual Foundation

At its core, a **relational database** organizes information into structured tables (mathematically termed *relations*). Each table is composed of rows (*tuples* or records) and columns (*attributes* or fields). 

Unlike unstructured key-value stores or document databases, relational databases enforce a **strict mathematical contract**:
- **Domain Integrity**: Every column has a specific datatype (e.g., `BIGINT`, `VARCHAR(100)`, `TIMESTAMP`, `BOOLEAN`). Values must strictly conform to these types.
- **Entity Integrity**: Every row in a table must be uniquely identifiable. This is guaranteed by a **Primary Key** (`PRIMARY KEY`), which enforces uniqueness and disallows `NULL` values.
- **Referential Integrity**: Relationships between tables are governed by **Foreign Keys** (`FOREIGN KEY`). A foreign key ensures that a reference in one table must point to an existing, valid row in another table.

```
+-----------------------------------------------------------------------+
|                           PATIENTS (Parent Table)                     |
|  +--------------------+---------------------+----------------------+  |
|  | id (PRIMARY KEY)   | full_name           | date_of_birth        |  |
|  +--------------------+---------------------+----------------------+  |
|  | 1001               | Sarah Connor        | 1985-05-12           |  |
|  | 1002               | John Doe            | 1990-11-23           |  |
|  +---------+----------+---------------------+----------------------+  |
|            |                                                          |
|            | 1-to-Many Relationship                                   |
|            v                                                          |
|  +---------+----------+---------------------+----------------------+  |
|  | id (PRIMARY KEY)   | patient_id (FK)     | systolic_bp          |  |
|  +--------------------+---------------------+----------------------+  |
|  | 5001               | 1001                | 120                  |  |
|  | 5002               | 1001                | 124                  |  |
|  +--------------------+---------------------+----------------------+  |
|                        VITALS_RECORDS (Child Table)                   |
+-----------------------------------------------------------------------+
```

Structured Query Language (**SQL**) serves as the universal declarative language to communicate with relational engines. In declarative programming, you declare *what* data you need, and the database optimizer calculates the most efficient algorithmic plan for *how* to retrieve it.

---

## 2. Architecture & Internal Mechanics

Relational database management systems (RDBMS) are complex distributed systems designed around two fundamental components: the **Query Execution Engine** and the **Storage & Buffer Engine**.

### The Anatomy of an RDBMS Query

```
+------------------------------------------------------------------------+
| Client Application (Java JDBC Application)                            |
+-----------------------------------+------------------------------------+
                                    | SQL Query String
                                    v
+------------------------------------------------------------------------+
| 1. SQL Parser & Lexer     -> Validates syntax, generates AST           |
| 2. Semantic Analyzer      -> Checks catalog (tables, columns, types)   |
| 3. Query Optimizer        -> Generates execution plans (Cost-Based)     |
| 4. Execution Engine       -> Executes operations (Scan, Join, Filter)  |
| 5. Buffer Pool (RAM)      -> Caches 8KB/16KB disk pages in memory       |
| 6. Storage Engine & WAL   -> Writes to Write-Ahead Log (WAL) & Disk     |
+------------------------------------------------------------------------+
```

### B-Tree Index Mechanics
How does a database find 1 patient out of 100 million records in less than 2 milliseconds? 
Without an index, the engine must perform a **Sequential Scan (Full Table Scan)**, reading every single disk block from start to end with time complexity $O(N)$.

An **Index** creates a secondary, sorted data structure—predominantly a **Balanced Tree (B+ Tree)**:
1. **Root Page**: The starting node containing keys and page pointers.
2. **Branch (Internal) Pages**: Intermediate navigation nodes directing searches down the tree.
3. **Leaf Pages**: The lowest level containing keys and physical row pointers (**RowID** or Tuple IDs) pointing to disk pages. Leaf nodes are linked doubly in order, enabling hyper-fast range scans (`WHERE age BETWEEN 30 AND 50`).

Search time in a B+ Tree is $O(\log N)$. For a table of 10,000,000 records with a branching factor of 500, a lookup requires only **3 to 4 disk page reads**!

---

## 3. Real-World Code Implementation & Patterns

### 1. Production DDL: Creating Clean Relational Schemas
```sql
CREATE TABLE IF NOT EXISTS patients (
    patient_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    mrn VARCHAR(64) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    date_of_birth DATE NOT NULL,
    blood_group VARCHAR(5) CHECK (blood_group IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS medical_encounters (
    encounter_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    attending_physician VARCHAR(150) NOT NULL,
    department VARCHAR(80) NOT NULL,
    admission_time TIMESTAMP WITH TIME ZONE NOT NULL,
    discharge_time TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_encounter_patient 
        FOREIGN KEY (patient_id) 
        REFERENCES patients(patient_id) 
        ON DELETE RESTRICT
);

-- Indexing foreign keys for rapid join lookups
CREATE INDEX idx_encounters_patient_id ON medical_encounters(patient_id);
```

### 2. Relational Schema Model in Java
Java domain entities mirror relational schema tables:

```java
import java.time.LocalDate;
import java.time.Instant;

public record PatientRecord(
    Long patientId,
    String mrn,
    String fullName,
    LocalDate dateOfBirth,
    String bloodGroup,
    Instant createdAt
) {
    public PatientRecord {
        if (mrn == null || mrn.isBlank()) {
            throw new IllegalArgumentException("MRN must not be empty");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name must not be empty");
        }
    }
}
```

---

## 4. Failure Modes & Edge Cases

| Failure Mode | Root Cause | Engineering Solution |
| :--- | :--- | :--- |
| **Missing Foreign Key Index** | Joins and cascade checks trigger sequential table scans on the child table. | Explicitly create B-Tree indexes on all foreign key columns. |
| **Index Over-Engineering** | Adding indexes on every column slows down `INSERT`, `UPDATE`, and `DELETE` due to tree rebalancing. | Index only query filter columns (`WHERE`), join keys (`ON`), and sorting targets (`ORDER BY`). |
| **Dangling References** | Child rows left behind when parent rows are deleted. | Enforce `FOREIGN KEY ... ON DELETE RESTRICT` or `ON DELETE CASCADE`. |
| **Table Lock Contention** | Unindexed `UPDATE` or `DELETE` forces table-level locks instead of row-level locks. | Ensure `WHERE` clauses target indexed columns so only relevant row locks are acquired. |

---

## 5. Performance & Optimization: Database Engines Compared

| Feature | PostgreSQL | SQLite | MySQL (InnoDB) | H2 Database |
| :--- | :--- | :--- | :--- | :--- |
| **Architecture** | Client-Server RDBMS | Embedded File Library | Client-Server RDBMS | Embedded / In-Memory Java |
| **Concurrency** | Multi-Version Concurrency (MVCC) | Single-Writer / Multiple-Readers | MVCC & Row-Level Locking | In-Memory MVCC |
| **Write Durability** | Robust WAL, fsync tuning | WAL Mode or Rollback Journal | Redo Log & Doublewrite Buffer | Optional disk persistence |
| **Java Integration** | PostgreSQL JDBC Driver | SQLite JDBC (JNI native wrapper) | MySQL Connector/J | Pure Java embedded driver |
| **Best Use Case** | High-scale enterprise data | Edge, mobile, local CLI apps | Web apps, read-heavy workloads | Fast integration testing & prototypes |

---

## 6. Industry Best Practices & Production Patterns

1. **Surrogate Primary Keys vs Natural Keys**: Use auto-generated surrogate keys (`BIGINT GENERATED ALWAYS AS IDENTITY` or `UUIDv7`) for primary keys. Natural keys (like social security numbers or phone numbers) change over time and cause expensive cascading foreign key updates.
2. **Always Index Foreign Keys**: Relational engines do *not* automatically create indexes on foreign key columns. Omitting them destroys join performance.
3. **Use Explicit Datatypes**: Store dates in `DATE` or `TIMESTAMP WITH TIME ZONE`, monetary values in `NUMERIC(12,2)`, and integers in `INTEGER` or `BIGINT`. Never use `VARCHAR` for dates or currencies.

---

## 7. Healthcare Domain Case Study: Hospital Patient Registry

In a hospital network, patient safety requires absolute referential integrity. When an Emergency Department physician prescribes a medication, the order must link without ambiguity to an active Medical Record Number (MRN). If a patient record could be deleted while active prescription records existed, pharmacists would be unable to verify drug allergies or dosing history. 

By enforcing relational constraints (`FOREIGN KEY ... ON DELETE RESTRICT`), the database itself guarantees that no patient with active clinical records can ever be dropped from the system.

---

## 8. Hands-On Verification & Knowledge Check

1. Why does an unindexed foreign key column degrade performance during `DELETE` operations on the parent table?
2. What is the Big-O time complexity of searching a B+ Tree index with $N$ entries, and why?
3. What is the fundamental difference in deployment architecture between PostgreSQL and an embedded database like SQLite or H2?
