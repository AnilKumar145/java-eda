import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Multi-Service Clinical Diagnostics Aggregator - Solution
 */
public class Solution {

    public record ServiceQuery(String serviceName, Callable<String> queryTask) {}
    public record ServiceResult(String serviceName, String status, String data) {}

    public static class DiagnosticAggregator {
        public List<ServiceResult> aggregateProfile(List<ServiceQuery> queries, long timeoutMs) {
            ExecutorService pool = Executors.newFixedThreadPool(Math.min(queries.size(), 8));
            Map<String, Future<String>> futureMap = new LinkedHashMap<>();

            for (ServiceQuery q : queries) {
                futureMap.put(q.serviceName(), pool.submit(q.queryTask()));
            }

            List<ServiceResult> results = new ArrayList<>();
            for (Map.Entry<String, Future<String>> entry : futureMap.entrySet()) {
                String name = entry.getKey();
                Future<String> future = entry.getValue();
                try {
                    String data = future.get(timeoutMs, TimeUnit.MILLISECONDS);
                    results.add(new ServiceResult(name, "OK", data));
                } catch (TimeoutException e) {
                    future.cancel(true);
                    results.add(new ServiceResult(name, "TIMEOUT", null));
                } catch (Exception e) {
                    results.add(new ServiceResult(name, "ERROR", e.getMessage()));
                }
            }

            pool.shutdown();
            return results;
        }
    }
}
