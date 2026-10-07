# Unit 5.6: In-Memory Database Test Harnesses, Transaction Rollbacks, and Repository Testing

---

## Part 1: Clinical & Conceptual Motivation

In hospital inpatient pharmacy operations, automated dispensing cabinets (e.g., Pyxis, Omnicell) communicate directly with the pharmacy database to dispense medications. When a critical drug like Epinephrine or Norepinephrine is dispensed in an ICU, the inventory record must decrement atomically inside a transaction.

If tests for the pharmacy repository rely on mocked SQL statements or fake collections, critical SQL syntax errors, broken foreign key constraints, or concurrency locks will remain completely undetected until deployed to production. To test the database layer with 100% confidence, we must execute real SQL against an isolated **in-memory database harness** (such as H2), combining true SQL execution with zero side-effects.

---

## Part 2: Core Concepts & Definitions

### 1. In-Memory Database Testing (H2)
H2 is a fast, embeddable, pure-Java relational database engine. By configuring the JDBC URL as `jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1`, the database runs entirely in RAM. Tests execute raw SQL, DDL, constraints, and indexes in milliseconds without installing external database software.

### 2. The Transaction Rollback Isolation Pattern
Instead of dropping and recreating tables before every test method (which can slow down suites):
1. `setUp`: Start a transaction on the connection (`connection.setAutoCommit(false)`).
2. Execute the test and run database inserts/queries.
3. `tearDown`: Invoke `connection.rollback()`.
4. Result: The database is left completely pristine for the next test method with zero overhead!

---

## Part 3: Code Architecture & Implementation Patterns

### 1. Setting Up In-Memory H2 Connection
```java
import java.sql.*;

public class TestDatabaseHarness {
    private static final String H2_URL = "jdbc:h2:mem:pharmacy_test;DB_CLOSE_DELAY=-1";

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(H2_URL, "sa", "");
    }

    public static void initializeSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS medications (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL UNIQUE,
                    stock INT NOT NULL DEFAULT 0,
                    unit_price DECIMAL(10, 2) NOT NULL
                )
            """);
        }
    }
}
```

### 2. Repository Pattern Testing with Rollback
```java
public class MedicationRepositoryTest {
    private Connection conn;
    private MedicationRepository repo;

    @BeforeEach
    void setUp() throws SQLException {
        conn = TestDatabaseHarness.createConnection();
        conn.setAutoCommit(false); // Begin transaction
        repo = new MedicationRepository(conn);
    }

    @AfterEach
    void tearDown() throws SQLException {
        conn.rollback(); // Undo all inserts made during this test!
        conn.close();
    }

    @Test
    void testAddAndRetrieveMedication() throws SQLException {
        Medication med = repo.add("Amoxicillin 500mg", 150, 12.50);
        Medication fetched = repo.getById(med.id());
        
        assertEquals("Amoxicillin 500mg", fetched.name());
        assertEquals(150, fetched.stock());
    }
}
```

---

## Part 4: Common Pitfalls & Anti-Patterns

### 1. Testing Against Shared Staging Databases
Running automated test suites against a remote PostgreSQL staging database where another developer or CI pipeline is also running tests. Tests will overwrite each other's records and fail intermittently.
*Rule*: Unit/DAO tests must run against isolated, local in-memory engines.

### 2. Forgetting to Clean Up Connections
Failing to close statements or connections in `finally` blocks or `try-with-resources`. In long test suites, this exhausts connection pools and causes `Too many open files` OS errors.

### 3. Re-creating Schemas Repeatedly
Dropping and recreating 50 tables before every individual test method slows test suites from seconds to minutes. Use transaction rollbacks instead.

---

## Part 5: Production Patterns & Enterprise Recipes

### Pattern: Testing Database Constraint Violations
```java
@Test
void testDuplicateMedicationNameThrowsSqlIntegrityException() throws SQLException {
    repo.add("Paracetamol 650mg", 200, 5.00);

    // Second insert with the exact same name must violate UNIQUE constraint
    assertThrows(SQLException.class, () -> {
        repo.add("Paracetamol 650mg", 50, 5.00);
    }, "Duplicate medication name must trigger SQL unique constraint violation");
}
```

---

## Part 6: Hands-On Walkthrough & Analysis

Let's trace a pharmacy stock decrement test:
1. `conn.setAutoCommit(false)`
2. Insert medication with stock = 100.
3. Dispense 25 units -> stock becomes 75.
4. Verify `getById` returns 75.
5. In teardown: `conn.rollback()` restores state to empty.
6. The next test sees 0 medications in the table.

---

## Part 7: Exercises & Application Lab Preview

- **Exercises (`exercises/`)**:
  - Implement and verify H2 schema setups, medication CRUD repositories, and low-stock queries.
- **Application Lab (`app_labs/lab_1_easy/`)**:
  - Build and verify the **Pharmacy Medication Inventory Repository Test Harness**, testing SKU uniqueness, stock dispenses, reorder threshold queries, and total inventory valuations.

---

## Part 8: Key Takeaways & Review Checklist

- [ ] In-memory H2 allows running real SQL statements and verifying constraints in milliseconds.
- [ ] Use `connection.setAutoCommit(false)` and `rollback()` in teardown for instant test isolation.
- [ ] Always test constraint failures (`UNIQUE`, `NOT NULL`, `FOREIGN KEY`).
- [ ] Use try-with-resources for all JDBC statements and result sets.
