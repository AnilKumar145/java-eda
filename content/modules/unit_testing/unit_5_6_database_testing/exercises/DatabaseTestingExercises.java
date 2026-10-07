import java.sql.*;
import java.util.*;

public class DatabaseTestingExercises {

    public record Medication(long id, String name, int stock, double unitPrice) {}

    public static class MedicationRepository {
        private final Connection conn;

        public MedicationRepository(Connection conn) {
            this.conn = conn;
        }

        public Medication add(String name, int stock, double unitPrice) throws SQLException {
            // TODO: Execute INSERT and return Medication with generated ID.
            throw new UnsupportedOperationException("TODO: Implement add");
        }

        public Medication getById(long id) throws SQLException {
            // TODO: SELECT medication by ID.
            throw new UnsupportedOperationException("TODO: Implement getById");
        }

        public List<Medication> findLowStock(int threshold) throws SQLException {
            // TODO: SELECT medications where stock <= threshold ORDER BY stock ASC.
            throw new UnsupportedOperationException("TODO: Implement findLowStock");
        }

        public Medication updateStock(long id, int delta) throws SQLException {
            // TODO: Retrieve existing medication. Validate stock + delta >= 0.
            // Execute UPDATE medications SET stock = ... WHERE id = ...
            throw new UnsupportedOperationException("TODO: Implement updateStock");
        }
    }

    public static void main(String[] args) {
        System.out.println("Unit 5.6 Exercises Starter ready for implementation.");
    }
}
