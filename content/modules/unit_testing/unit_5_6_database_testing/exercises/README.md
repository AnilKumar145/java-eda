# Unit 5.6 Exercises: Database Testing

## Overview
These exercises test your mastery of database testing with in-memory H2:
- Establishing in-memory H2 connections (`jdbc:h2:mem:...`)
- Executing DDL schemas and transactional rollbacks
- Testing CRUD repository methods and unique constraint rejections

## How to Run the Exercises

### Compile and Run Starter Code:
```bash
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/unit_testing/unit_5_6_database_testing/exercises/DatabaseTestingExercises.java
java -ea -cp "lib/*;.temp_bin" DatabaseTestingExercises
```

### Compile and Run Reference Solution:
```bash
javac -cp "lib/*;.temp_bin" -d .temp_bin content/modules/unit_testing/unit_5_6_database_testing/exercises/solutions/DatabaseTestingSolutions.java
java -ea -cp "lib/*;.temp_bin" DatabaseTestingSolutions
```
