import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Multi-Service Clinical Diagnostics Aggregator - Starter Code
 */
public class StarterCode {

    public record ServiceQuery(String serviceName, Callable<String> queryTask) {}
    public record ServiceResult(String serviceName, String status, String data) {}

    public static class DiagnosticAggregator {
        public List<ServiceResult> aggregateProfile(List<ServiceQuery> queries, long timeoutMs) {
            // TODO: Execute queries concurrently in ExecutorService, apply timeout, return results
            return new ArrayList<>();
        }
    }
}
