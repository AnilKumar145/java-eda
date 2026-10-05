# Java — Real-Time Product Engineering

## Unit 1: Threads

### Unit 1.1: Threading Fundamentals
- What is a thread?
- Process vs thread
- Java thread model
- Main thread
- Creating threads
- Extending `Thread`
- Implementing `Runnable`
- `start()` vs `run()`
- Thread lifecycle

### Unit 1.2: Thread Management
- Thread naming
- Thread priorities
- `sleep()`
- `join()`
- `interrupt()`
- Daemon threads
- Checking thread state
- `Thread.State`

### Unit 1.3: Thread Synchronization
- Shared resources
- Race conditions
- Critical sections
- `synchronized` methods
- `synchronized` blocks
- Intrinsic locks and monitors
- `volatile`
- Atomic variables

### Unit 1.4: Thread Communication
- Inter-thread communication
- `wait()`
- `notify()`
- `notifyAll()`
- Producer–Consumer pattern
- Shared-state problems
- Modern thread communication approaches

### Unit 1.5: Thread Pools
- Why thread pools?
- `ExecutorService`
- `Executors`
- Fixed thread pool
- Cached thread pool
- Scheduled executor
- `Callable`
- `Future`
- `submit()`
- `shutdown()`
- Exception handling


## Unit 2: Concurrency

### Unit 2.1: Concurrency Fundamentals
- Concurrency vs parallelism
- Multithreading vs multiprocessing
- Java concurrency model
- Shared-memory concurrency
- Thread safety
- Immutability
- Thread-safe design

### Unit 2.2: Java Concurrency Utilities
- `java.util.concurrent`
- `Lock`
- `ReentrantLock`
- `ReadWriteLock`
- `ReentrantReadWriteLock`
- `Semaphore`
- `CountDownLatch`
- `CyclicBarrier`
- `Phaser`

### Unit 2.3: Concurrent Collections
- Why concurrent collections?
- `ConcurrentHashMap`
- `CopyOnWriteArrayList`
- `BlockingQueue`
- `ArrayBlockingQueue`
- `LinkedBlockingQueue`
- Thread-safe collection strategies

### Unit 2.4: Atomic Operations and Memory
- Atomic variables
- `AtomicInteger`
- `AtomicLong`
- `AtomicReference`
- Compare-and-swap (CAS)
- `volatile`
- Java Memory Model basics
- Visibility
- Atomicity
- Ordering

### Unit 2.5: Advanced Task Execution
- `Callable`
- `Future`
- `CompletableFuture`
- Asynchronous task execution
- Chaining asynchronous operations
- Combining futures
- Exception handling
- Timeouts
- Cancellation

### Unit 2.6: Concurrency Problems and Patterns
- Race conditions
- Deadlocks
- Livelocks
- Starvation
- Thread contention
- Producer–Consumer
- Reader–Writer
- Worker Pool
- Fork/Join framework
- Parallel streams

### Unit 2.7: Choosing the Right Concurrency Tool
- `synchronized` vs `Lock`
- `Lock` vs atomic variables
- ExecutorService vs manually creating threads
- `Future` vs `CompletableFuture`
- Concurrent collections vs synchronized collections
- Parallel streams vs ExecutorService
- Real-world application scenarios


## Unit 3: Database Programming — Part 1

### Unit 3.1: Database Fundamentals
- Relational database concepts
- SQL basics (`SELECT`, `INSERT`, `UPDATE`, `DELETE`)
- Primary keys and foreign keys
- Indexes and performance
- PostgreSQL vs SQLite vs MySQL
- Java database connectivity overview

### Unit 3.2: Java Database Connectivity (JDBC)
- JDBC architecture
- JDBC API and drivers
- Connection objects
- `Statement`
- `PreparedStatement`
- `ResultSet`
- Parameterized queries
- Commit and rollback
- Connection pooling basics

### Unit 3.3: Working with PostgreSQL
- PostgreSQL JDBC driver setup
- Connecting Java applications to PostgreSQL
- Executing queries
- Processing `ResultSet`
- Handling `NULL` values
- Data type mapping between Java and PostgreSQL
- `AutoCloseable` and try-with-resources

### Unit 3.4: Security and Best Practices
- SQL injection prevention
- `PreparedStatement` vs string concatenation
- Transaction management
- JDBC exception handling
- Connection cleanup and resource management
- Try-with-resources
- Database connection best practices


## Unit 4: Database Programming — Part 2

### Unit 4.1: ORM Fundamentals
- What is an ORM and why use it?
- JPA overview
- Hibernate overview
- JPA vs Hibernate
- Entity classes
- Defining entities with annotations
- Column types and constraints
- One-to-many relationships
- Many-to-many relationships

### Unit 4.2: JPA/Hibernate Persistence Context
- EntityManager
- Persistence context
- Creating and persisting entities
- Finding and retrieving entities
- Updating and deleting entities
- JPQL basics
- Filtering, ordering, and joining
- Lazy vs eager loading
- Entity lifecycle

### Unit 4.3: Advanced ORM Features
- Relationship configurations
- Cascade operations
- `mappedBy`
- `@JoinColumn`
- Bidirectional relationships
- Many-to-many mapping
- Association/join tables
- Fetch strategies
- N+1 query problem
- Query optimization techniques

### Unit 4.4: Database Migrations
- Why migrations matter
- Flyway setup and configuration
- Creating migration scripts
- Applying migrations
- Versioning database changes
- Managing schema changes
- Rollback strategies
- Team collaboration with migrations

### Unit 4.5: Database Logging
- SQL query logging
- Hibernate SQL logging
- Analyzing slow queries
- Connection pool monitoring
- HikariCP basics
- Debugging database issues
- Monitoring database performance


## Unit 5: Unit Testing

### Unit 5.1: Testing Fundamentals
- What is software testing?
- Why unit testing matters
- Unit testing vs integration testing
- Test cases and test scenarios
- Test-driven development (TDD) basics
- Arrange, Act, Assert (AAA) pattern
- Test isolation
- Test naming and organization

### Unit 5.2: JUnit Fundamentals
- JUnit 5 overview
- Project configuration
- Test classes and test methods
- `@Test`
- Assertions
- `assertEquals`
- `assertTrue`
- `assertFalse`
- `assertNull`
- `assertThrows`
- Test lifecycle
- `@BeforeEach`
- `@AfterEach`
- `@BeforeAll`
- `@AfterAll`

### Unit 5.3: Test Data and Parameterized Testing
- Test data management
- Parameterized tests
- `@ParameterizedTest`
- `@ValueSource`
- `@CsvSource`
- `@MethodSource`
- Setup and teardown
- Reusable test utilities
- Temporary test resources

### Unit 5.4: Mocking and Isolation
- Why mocking is required
- Mockito overview
- Creating mocks
- `@Mock`
- `@InjectMocks`
- Stubbing methods
- `when()` and `thenReturn()`
- Verifying interactions
- `verify()`
- Mocking external APIs
- Mocking repositories and databases

### Unit 5.5: Exceptions and Edge Cases
- Testing expected exceptions
- `assertThrows`
- Testing invalid inputs
- Boundary-value testing
- Edge cases
- Negative test cases
- Error handling validation

### Unit 5.6: Database Testing
- Testing database operations
- Test database setup
- CRUD testing
- Transaction testing
- Rollback during tests
- Testing JPA/Hibernate entities
- Testing repository/data-access logic
- Integration testing basics

### Unit 5.7: Test Coverage and Best Practices
- Code coverage
- JaCoCo
- Measuring test coverage
- Identifying untested code
- Unit testing best practices
- Avoiding brittle tests
- Test independence
- Maintainable test suites
- CI/CD testing basics
