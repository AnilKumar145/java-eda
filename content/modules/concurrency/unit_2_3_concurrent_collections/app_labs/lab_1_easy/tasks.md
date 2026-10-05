# Lab 1 Tasks: Hospital Incident Audit Stream

## Task 1: Incident Record and Queue Initialization
- Implement `Incident` with `id`, `wardId`, `severity`, and `description`.
- In `IncidentAuditStream`, maintain a `BlockingQueue<Incident>` for ingesting incidents with a configurable capacity.
- Implement `boolean submitIncident(Incident incident, long timeoutMs)` using `offer()` with timeout.

## Task 2: Atomic Ward Counter Aggregation
- Maintain a `ConcurrentHashMap<String, Integer>` tracking total incident count per ward.
- Implement `void processIncident(Incident incident)` that updates the ward count using `ConcurrentHashMap.merge(wardId, 1, Integer::sum)`.
- Implement `int getWardCount(String wardId)` returning 0 if the ward has no incidents.
- Implement `Map<String, Integer> getAllWardCounts()` returning an unmodifiable copy or direct map view.

## Task 3: Observers and Safe Notification
- Maintain a `CopyOnWriteArrayList<IncidentObserver>` storing registered audit listeners.
- Implement `void registerObserver(IncidentObserver observer)`.
- Implement `void notifyObservers(Incident incident)` safely iterating without `ConcurrentModificationException`.
