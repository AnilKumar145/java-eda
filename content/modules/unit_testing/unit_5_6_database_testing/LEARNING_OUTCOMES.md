# Learning Outcomes: Unit 5.6 - Database Testing

By completing this unit, students will be able to:

1. **Configure In-Memory Database Test Fixtures**:
   - Set up in-memory H2 database connections (`jdbc:h2:mem:...`) to test real SQL queries and constraints without depending on physical PostgreSQL servers.
   - Execute DDL schema creation and drops cleanly between tests.

2. **Implement Transaction Rollback Isolation**:
   - Wrap test executions in database transactions that always execute `connection.rollback()` upon test completion.
   - Guarantee zero side-effect bleed across test cases without dropping and recreating tables repeatedly.

3. **Verify Repository and DAO Pattern Operations**:
   - Test CRUD operations: creating entities, primary-key retrieval, custom query filtering, and inventory updates.
   - Assert database constraint enforcement: duplicate unique keys throwing `SQLException`, not-null violations, and foreign key rules.
