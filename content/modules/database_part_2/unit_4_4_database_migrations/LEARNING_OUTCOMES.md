# Learning Outcomes: Unit 4.4 - Database Migrations

By the end of this unit, you will be able to:

1. **Explain the Schema Drift Problem**: Articulate why manual DDL modifications across developer environments, staging, and production clusters inevitably cause production outages and data corruption.
2. **Deconstruct Flyway Migration Architecture**: Detail the execution lifecycle of Flyway: baseline, validate, migrate, info, and repair.
3. **Parse and Structure Migration Scripts**: Apply strict Flyway naming conventions: `V{Version}__{Description}.sql` for versioned migrations, `U{Version}__{Description}.sql` for undo migrations, and `R__{Description}.sql` for repeatable migrations.
4. **Implement Checksum Verification**: Calculate and validate 32-bit CRC or SHA-256 script checksums against the `flyway_schema_history` metadata table to detect unauthorized script modifications.
5. **Manage Rollbacks and Repairs**: Explain why enterprise systems avoid automatic rollback scripts and instead favor forward-fix migrations and zero-downtime expand-and-contract patterns.
6. **Integrate Migrations into Spring Boot & CI/CD**: Understand how Flyway runs automatically on application startup within Maven/Gradle builds and deployment pipelines.
