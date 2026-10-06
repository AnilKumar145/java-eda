# Learning Outcomes: Unit 3.1 - Database Fundamentals

By the end of this unit, you will be able to:

1. **Explain Relational Architecture**: Understand relations, tuples, attributes, relational integrity, primary keys, candidate keys, and foreign keys.
2. **Author ANSI SQL DDL & DML**: Write production-grade `CREATE TABLE`, `ALTER TABLE`, `INSERT`, `UPDATE`, `DELETE`, and `SELECT` statements with constraints (`NOT NULL`, `CHECK`, `UNIQUE`, `FOREIGN KEY REFERENCES`).
3. **Deconstruct B-Tree Indexing**: Explain the physical organization of B-Tree indexes, leaf pages, node splits, logarithmic search complexity $O(\log N)$, and know when index scans outperform full table scans.
4. **Evaluate Relational Engines**: Critically contrast PostgreSQL, SQLite, MySQL, and H2 across concurrency models, client-server vs embedded runtime, WAL durability, and type systems.
5. **Architect Java Data Layers**: Understand the role of JDBC (`java.sql`) as an abstraction layer across diverse database vendors, and how connection drivers translate Java SQL requests into vendor-specific wire protocols.
