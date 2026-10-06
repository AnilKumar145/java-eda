# Learning Outcomes: Unit 4.1 - ORM Fundamentals

By the end of this unit, you will be able to:

1. **Analyze the Object-Relational Impedance Mismatch**: Explain the paradigm differences between object-oriented domain graphs (encapsulation, inheritance, references) and relational algebra (tables, keys, foreign keys, normalization).
2. **Differentiate JPA and Hibernate**: Clearly explain the distinction between JPA / Jakarta Persistence as a standardized specification interface and Hibernate ORM as the concrete production runtime implementation engine.
3. **Model Production Entities**: Annotate Java classes with `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`, `@Enumerated`, and `@Temporal` to establish precise relational mapping contracts.
4. **Configure Primary Key Generators**: Compare `IDENTITY`, `SEQUENCE`, `TABLE`, and `AUTO` strategies, understanding database sequence pre-allocation and batch insertion performance implications.
5. **Establish Relational Associations**: Define unidirectional and bidirectional `@OneToMany` and `@ManyToOne` relationships with proper foreign key column mapping and defensive memory-synchronization helpers.
6. **Bridge Relational Data to Objects**: Understand hydration pipelines that transform raw relational tuples (`ResultSet`) into strongly-typed domain model instances.
