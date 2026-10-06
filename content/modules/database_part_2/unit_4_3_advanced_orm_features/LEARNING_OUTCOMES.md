# Learning Outcomes: Unit 4.3 - Advanced ORM Features

By the end of this unit, you will be able to:

1. **Configure Cascade Lifecycles**: Correctly configure `CascadeType.PERSIST`, `CascadeType.MERGE`, `CascadeType.REMOVE`, and `CascadeType.ALL` to propagate operations across aggregate boundaries.
2. **Prevent Dangling Records with Orphan Removal**: Explain the behavioral difference between `CascadeType.REMOVE` (triggered when parent is deleted) and `orphanRemoval = true` (triggered when child is unlinked from parent collection).
3. **Model Complex Many-to-Many Associations**: Map `@ManyToMany` relationships using `@JoinTable`, `@JoinColumn`, and `inverseJoinColumns`, and know when to promote join tables to first-class association entities.
4. **Contrast Fetch Strategies**: Explain default fetch types (`@ManyToOne` defaults to `EAGER`, `@OneToMany` defaults to `LAZY`) and why production architectures enforce `LAZY` fetching everywhere.
5. **Diagnose the N+1 Query Problem**: Quantify the latency and database connection saturation caused by emitting 1 query for parent records followed by N separate queries for related children.
6. **Eliminate N+1 with JOIN FETCH & Projections**: Author optimized JPQL `JOIN FETCH` queries, entity graphs, and lightweight DTO projections to load complete object graphs in a single database roundtrip.
