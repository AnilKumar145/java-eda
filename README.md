# Java EAD: Enterprise Concurrency & Parallel Engineering Platform

Curriculum and test harness for Multithreading, `java.util.concurrent`, Atomic CAS Operations, Fork/Join Work-Stealing, and Virtual Threads (Java 21 LTS).

---

## 1. Quick Start & Prerequisites

### Prerequisites:
- **Java 21 LTS** installed (`javac --version`, `java --version`)
- **Maven 3.9+** installed (`mvn --version`)

---

## 2. Commands to Run Exercises

Each unit includes standalone assertion test suites. Compile with `javac` and execute with assertion flags enabled (`-ea`).

### Run an Exercise (Student Starter):
```bash
# Unit 1.1: Threading Fundamentals
javac -d .temp_bin content/modules/threads/unit_1_1_threading_fundamentals/exercises/ThreadingFundamentalsExercises.java
java -ea -cp .temp_bin unit_1_1_threading_fundamentals.exercises.ThreadingFundamentalsExercises

# Unit 1.2: Thread Management
javac -d .temp_bin content/modules/threads/unit_1_2_thread_management/exercises/ThreadManagementExercises.java
java -ea -cp .temp_bin unit_1_2_thread_management.exercises.ThreadManagementExercises

# Unit 1.3: Thread Synchronization
javac -d .temp_bin content/modules/threads/unit_1_3_thread_synchronization/exercises/ThreadSynchronizationExercises.java
java -ea -cp .temp_bin unit_1_3_thread_synchronization.exercises.ThreadSynchronizationExercises

# Unit 1.4: Thread Communication
javac -d .temp_bin content/modules/threads/unit_1_4_thread_communication/exercises/ThreadCommunicationExercises.java
java -ea -cp .temp_bin unit_1_4_thread_communication.exercises.ThreadCommunicationExercises

# Unit 1.5: Thread Pools
javac -d .temp_bin content/modules/threads/unit_1_5_thread_pools/exercises/ThreadPoolsExercises.java
java -ea -cp .temp_bin unit_1_5_thread_pools.exercises.ThreadPoolsExercises

# Unit 2.1: Concurrency Fundamentals
javac -d .temp_bin content/modules/concurrency/unit_2_1_concurrency_fundamentals/exercises/JavaConcurrencyFundamentalsExercises.java
java -ea -cp .temp_bin unit_2_1_concurrency_fundamentals.exercises.JavaConcurrencyFundamentalsExercises

# Unit 2.2: Java Concurrency Utilities
javac -d .temp_bin content/modules/concurrency/unit_2_2_java_concurrency_utilities/exercises/JavaConcurrencyUtilitiesExercises.java
java -ea -cp .temp_bin unit_2_2_java_concurrency_utilities.exercises.JavaConcurrencyUtilitiesExercises

# Unit 2.3: Concurrent Collections
javac -d .temp_bin content/modules/concurrency/unit_2_3_concurrent_collections/exercises/ConcurrentCollectionsExercises.java
java -ea -cp .temp_bin unit_2_3_concurrent_collections.exercises.ConcurrentCollectionsExercises

# Unit 2.4: Atomic Operations & Memory
javac -d .temp_bin content/modules/concurrency/unit_2_4_atomic_operations_and_memory/exercises/AtomicOperationsExercises.java
java -ea -cp .temp_bin unit_2_4_atomic_operations_and_memory.exercises.AtomicOperationsExercises

# Unit 2.5: Advanced Task Execution
javac -d .temp_bin content/modules/concurrency/unit_2_5_advanced_task_execution/exercises/AdvancedTaskExecutionExercises.java
java -ea -cp .temp_bin unit_2_5_advanced_task_execution.exercises.AdvancedTaskExecutionExercises

# Unit 2.6: Concurrency Problems and Patterns
javac -d .temp_bin content/modules/concurrency/unit_2_6_concurrency_problems_and_patterns/exercises/ConcurrencyProblemsExercises.java
java -ea -cp .temp_bin unit_2_6_concurrency_problems_and_patterns.exercises.ConcurrencyProblemsExercises

# Unit 2.7: Choosing the Right Concurrency Tool
javac -d .temp_bin content/modules/concurrency/unit_2_7_choosing_the_right_concurrency_model/exercises/ChoosingConcurrencyToolExercises.java
java -ea -cp .temp_bin unit_2_7_choosing_the_right_concurrency_model.exercises.ChoosingConcurrencyToolExercises

# Unit 3.1: Database Fundamentals
javac -d .temp_bin content/modules/database_part_1/unit_3_1_database_fundamentals/exercises/DatabaseFundamentalsExercises.java
java -ea -cp .temp_bin DatabaseFundamentalsExercises

# Unit 3.2: Java Database Connectivity (JDBC)
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_1/unit_3_2_java_database_connectivity/exercises/JdbcConnectivityExercises.java
java -ea -cp "lib/*;.temp_bin" JdbcConnectivityExercises

# Unit 3.3: Working with PostgreSQL and Relational Stores
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_1/unit_3_3_working_with_postgresql/exercises/PostgreSqlRelationalExercises.java
java -ea -cp "lib/*;.temp_bin" PostgreSqlRelationalExercises

# Unit 3.4: Database Security, Transactions, and Best Practices
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_1/unit_3_4_security_and_best_practices/exercises/SecurityAndBestPracticesExercises.java
java -ea -cp "lib/*;.temp_bin" SecurityAndBestPracticesExercises

# Unit 4.1: ORM Fundamentals
javac -d .temp_bin content/modules/database_part_2/unit_4_1_orm_fundamentals/exercises/OrmFundamentalsExercises.java
java -ea -cp .temp_bin OrmFundamentalsExercises

# Unit 4.2: JPA/Hibernate Persistence Context
javac -d .temp_bin content/modules/database_part_2/unit_4_2_jpa_persistence_context/exercises/JpaPersistenceContextExercises.java
java -ea -cp .temp_bin JpaPersistenceContextExercises

# Unit 4.3: Advanced ORM Features
javac -d .temp_bin content/modules/database_part_2/unit_4_3_advanced_orm_features/exercises/AdvancedOrmFeaturesExercises.java
java -ea -cp .temp_bin AdvancedOrmFeaturesExercises

# Unit 4.4: Database Migrations
javac -d .temp_bin content/modules/database_part_2/unit_4_4_database_migrations/exercises/DatabaseMigrationsExercises.java
java -ea -cp .temp_bin DatabaseMigrationsExercises

# Unit 4.5: Database Logging and Diagnostics
javac -d .temp_bin content/modules/database_part_2/unit_4_5_database_logging/exercises/DatabaseLoggingExercises.java
java -ea -cp .temp_bin DatabaseLoggingExercises
```

### Run an Exercise Solution (Reference):
*(Available on the `main` branch under `exercises/solutions/`)*
```bash
javac -d .temp_bin content/modules/threads/unit_1_1_threading_fundamentals/exercises/solutions/ThreadingFundamentalsSolutions.java
java -ea -cp .temp_bin unit_1_1_threading_fundamentals.exercises.solutions.ThreadingFundamentalsSolutions

javac -d .temp_bin content/modules/database_part_1/unit_3_1_database_fundamentals/exercises/solutions/DatabaseFundamentalsSolutions.java
java -ea -cp .temp_bin DatabaseFundamentalsSolutions

javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_1/unit_3_2_java_database_connectivity/exercises/solutions/JdbcConnectivitySolutions.java
java -ea -cp "lib/*;.temp_bin" JdbcConnectivitySolutions

javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_1/unit_3_3_working_with_postgresql/exercises/solutions/PostgreSqlRelationalSolutions.java
java -ea -cp "lib/*;.temp_bin" PostgreSqlRelationalSolutions

javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_1/unit_3_4_security_and_best_practices/exercises/solutions/SecurityAndBestPracticesSolutions.java
java -ea -cp "lib/*;.temp_bin" SecurityAndBestPracticesSolutions

javac -d .temp_bin content/modules/database_part_2/unit_4_1_orm_fundamentals/exercises/solutions/OrmFundamentalsSolutions.java
java -ea -cp .temp_bin OrmFundamentalsSolutions

javac -d .temp_bin content/modules/database_part_2/unit_4_2_jpa_persistence_context/exercises/solutions/JpaPersistenceContextSolutions.java
java -ea -cp .temp_bin JpaPersistenceContextSolutions

javac -d .temp_bin content/modules/database_part_2/unit_4_3_advanced_orm_features/exercises/solutions/AdvancedOrmFeaturesSolutions.java
java -ea -cp .temp_bin AdvancedOrmFeaturesSolutions

javac -d .temp_bin content/modules/database_part_2/unit_4_4_database_migrations/exercises/solutions/DatabaseMigrationsSolutions.java
java -ea -cp .temp_bin DatabaseMigrationsSolutions

javac -d .temp_bin content/modules/database_part_2/unit_4_5_database_logging/exercises/solutions/DatabaseLoggingSolutions.java
java -ea -cp .temp_bin DatabaseLoggingSolutions
```

---

## 3. Commands to Run Application Labs

Each unit includes real-world Healthcare domain application labs verified via `LabTests.java`.

### Run Individual Lab Tests:
```bash
# Unit 1.1 Lab: Bedside Vitals Telemetry Poller
javac -d .temp_bin (Get-ChildItem -Path "content/modules/threads/unit_1_1_threading_fundamentals/app_labs/lab_1_easy" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -ea -cp .temp_bin unit_1_1_threading_fundamentals.app_labs.lab_1_easy.LabTests

# Unit 1.2 Lab: ICU Watchdog Supervisor
javac -d .temp_bin (Get-ChildItem -Path "content/modules/threads/unit_1_2_thread_management/app_labs/lab_1_easy" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -ea -cp .temp_bin unit_1_2_thread_management.app_labs.lab_1_easy.LabTests

# Unit 1.3 Lab: Hospital Pharmacy Dispenser Lock
javac -d .temp_bin (Get-ChildItem -Path "content/modules/threads/unit_1_3_thread_synchronization/app_labs/lab_1_easy" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -ea -cp .temp_bin unit_1_3_thread_synchronization.app_labs.lab_1_easy.LabTests

# Unit 1.4 Lab: ER Priority Triage Queue
javac -d .temp_bin (Get-ChildItem -Path "content/modules/threads/unit_1_4_thread_communication/app_labs/lab_1_easy" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -ea -cp .temp_bin unit_1_4_thread_communication.app_labs.lab_1_easy.LabTests

# Unit 1.5 Lab: Diagnostic Panel Aggregator Pool
javac -d .temp_bin (Get-ChildItem -Path "content/modules/threads/unit_1_5_thread_pools/app_labs/lab_1_easy" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -ea -cp .temp_bin unit_1_5_thread_pools.app_labs.lab_1_easy.LabTests

# Unit 2.1 Lab: Clinical Workload Router
javac -d .temp_bin (Get-ChildItem -Path "content/modules/concurrency/unit_2_1_concurrency_fundamentals/app_labs/lab_1_easy" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -ea -cp .temp_bin unit_2_1_concurrency_fundamentals.app_labs.lab_1_easy.LabTests

# Unit 2.2 Lab: Hospital Emergency Evacuation Coordinator
javac -d .temp_bin (Get-ChildItem -Path "content/modules/concurrency/unit_2_2_java_concurrency_utilities/app_labs/lab_1_easy" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -ea -cp .temp_bin unit_2_2_java_concurrency_utilities.app_labs.lab_1_easy.LabTests

# Unit 2.3 Lab: Hospital Incident Audit Stream
javac -d .temp_bin (Get-ChildItem -Path "content/modules/concurrency/unit_2_3_concurrent_collections/app_labs/lab_1_easy" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -ea -cp .temp_bin unit_2_3_concurrent_collections.app_labs.lab_1_easy.LabTests

# Unit 2.4 Lab: Patient Vital Metric Tracker
javac -d .temp_bin (Get-ChildItem -Path "content/modules/concurrency/unit_2_4_atomic_operations_and_memory/app_labs/lab_1_easy" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -ea -cp .temp_bin unit_2_4_atomic_operations_and_memory.app_labs.lab_1_easy.LabTests

# Unit 2.5 Lab: Healthcare Diagnostic Report Aggregator
javac -d .temp_bin (Get-ChildItem -Path "content/modules/concurrency/unit_2_5_advanced_task_execution/app_labs/lab_1_easy" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -ea -cp .temp_bin unit_2_5_advanced_task_execution.app_labs.lab_1_easy.LabTests

# Unit 2.6 Lab: Diagnostic Image Parallel Tile Processor
javac -d .temp_bin (Get-ChildItem -Path "content/modules/concurrency/unit_2_6_concurrency_problems_and_patterns/app_labs/lab_1_easy" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -ea -cp .temp_bin unit_2_6_concurrency_problems_and_patterns.app_labs.lab_1_easy.LabTests

# Unit 2.7 Lab: Clinical Metric Benchmark Harness
javac -d .temp_bin (Get-ChildItem -Path "content/modules/concurrency/unit_2_7_choosing_the_right_concurrency_model/app_labs/lab_1_easy" -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)
java -ea -cp .temp_bin unit_2_7_choosing_the_right_concurrency_model.app_labs.lab_1_easy.LabTests

# Unit 3.1 Lab: Hospital Patient Registry
javac -d .temp_bin content/modules/database_part_1/unit_3_1_database_fundamentals/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_1/unit_3_1_database_fundamentals/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests

# Unit 3.2 Lab: Clinic Telemetry Pipeline & Pooled Engine
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_1/unit_3_2_java_database_connectivity/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_1/unit_3_2_java_database_connectivity/app_labs/lab_1_easy/LabTests.java
java -ea -cp "lib/*;.temp_bin" LabTests

# Unit 3.3 Lab: Radiology Study Store & Structured Metrics
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_1/unit_3_3_working_with_postgresql/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_1/unit_3_3_working_with_postgresql/app_labs/lab_1_easy/LabTests.java
java -ea -cp "lib/*;.temp_bin" LabTests

# Unit 3.4 Lab: Prescription Audit Ledger & Transaction Guard
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_1/unit_3_4_security_and_best_practices/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_1/unit_3_4_security_and_best_practices/app_labs/lab_1_easy/LabTests.java
java -ea -cp "lib/*;.temp_bin" LabTests

# Unit 4.1 Lab: Inpatient Bed Allocation & Ward Registry
javac -d .temp_bin content/modules/database_part_2/unit_4_1_orm_fundamentals/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_2/unit_4_1_orm_fundamentals/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests

# Unit 4.2 Lab: Surgical Schedule & Operating Theatre Dispatcher
javac -d .temp_bin content/modules/database_part_2/unit_4_2_jpa_persistence_context/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_2/unit_4_2_jpa_persistence_context/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests

# Unit 4.3 Lab: Patient Allergy Cross-Reference & Eager Loading Hub
javac -d .temp_bin content/modules/database_part_2/unit_4_3_advanced_orm_features/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_2/unit_4_3_advanced_orm_features/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests

# Unit 4.4 Lab: Pharmacy Drug Formulary Schema Evolution Engine
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/database_part_2/unit_4_4_database_migrations/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_2/unit_4_4_database_migrations/app_labs/lab_1_easy/LabTests.java
java -ea -cp "lib/*;.temp_bin" LabTests

# Unit 4.5 Lab: ICU Diagnostic Query Profiler & Slow Statement Watchdog
javac -d .temp_bin content/modules/database_part_2/unit_4_5_database_logging/app_labs/lab_1_easy/solution/Solution.java content/modules/database_part_2/unit_4_5_database_logging/app_labs/lab_1_easy/LabTests.java
java -ea -cp .temp_bin LabTests
```

---

## 4. Run All Java Tests with a Single Command

### PowerShell One-Liner (Runs all 12 Exercise Suites and all 12 Lab Tests):
```powershell
# Run all exercise solutions:
Get-ChildItem -Path "content/modules" -Filter "*Solutions.java" -Recurse | ForEach-Object { javac -d .temp_bin (Get-ChildItem -Path $_.Directory.Parent.FullName -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName); $pkg = (Get-Content $_.FullName | Select-String "package\s+([^;]+);").Matches.Groups[1].Value; java -ea -cp .temp_bin "$pkg.$([System.IO.Path]::GetFileNameWithoutExtension($_.Name))" }

# Run all lab tests:
Get-ChildItem -Path "content/modules" -Filter "LabTests.java" -Recurse | ForEach-Object { javac -d .temp_bin (Get-ChildItem -Path $_.Directory.FullName -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName); $pkg = (Get-Content $_.FullName | Select-String "package\s+([^;]+);").Matches.Groups[1].Value; java -ea -cp .temp_bin "$pkg.LabTests" }
```

---

## 5. Web Application

To launch the interactive web platform:
```bash
cd web-app
npm install
npm run dev
# Open http://localhost:3000
```
