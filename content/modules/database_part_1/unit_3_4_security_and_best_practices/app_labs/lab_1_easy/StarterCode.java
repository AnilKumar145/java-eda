import java.sql.*;

public class StarterCode {

    public static class PrescriptionLedger {

        public void initSchema(Connection conn) throws SQLException {
            // TODO: Create pharmacy_inventory, dispense_audit, and insurance_claims tables
        }

        public boolean dispenseMedication(
            Connection conn,
            String mrn,
            String medCode,
            int units,
            boolean simulateInsuranceFailure
        ) throws SQLException {
            // TODO: Execute atomic dispense transaction with Savepoint for insurance stage
            return false;
        }

        public int getInventory(Connection conn, String medCode) throws SQLException {
            // TODO: Query remaining inventory
            return 0;
        }
    }
}
