import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;

/**
 * Unit 3.3 Exercises: Working with PostgreSQL and Relational Stores - Solutions
 */
public class PostgreSqlRelationalSolutions {

    public static final String JDBC_URL = "jdbc:h2:mem:unit33db_sol;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";

    public static String buildPostgreSqlUrl(String host, int port, String database, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:postgresql://").append(host).append(":").append(port).append("/").append(database);
        if (params != null && !params.isEmpty()) {
            sb.append("?");
            List<String> queryParams = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                queryParams.add(entry.getKey() + "=" + entry.getValue());
            }
            sb.append(String.join("&", queryParams));
        }
        return sb.toString();
    }

    public static Integer extractNullableInteger(ResultSet rs, String columnName) throws SQLException {
        int val = rs.getInt(columnName);
        return rs.wasNull() ? null : val;
    }

    public static BigDecimal aggregateRadiationDose(List<BigDecimal> doses) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal d : doses) {
            sum = sum.add(d);
        }
        return sum.setScale(2, RoundingMode.HALF_UP);
    }

    public static int executeBatchInsert(Connection conn, List<String> mrnList) throws SQLException {
        String sql = "INSERT INTO test_batch_patients (mrn) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String mrn : mrnList) {
                pstmt.setString(1, mrn);
                pstmt.addBatch();
            }
            int[] results = pstmt.executeBatch();
            int total = 0;
            for (int r : results) {
                total += (r >= 0 ? r : 1);
            }
            return total;
        }
    }

    public static void main(String[] args) throws Exception {
        // Test 1: URL Builder
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sslmode", "verify-full");
        params.put("connectTimeout", "5");
        String url = buildPostgreSqlUrl("db.hospital.org", 5432, "clinical", params);
        assert url != null && url.startsWith("jdbc:postgresql://db.hospital.org:5432/clinical?") : "URL start mismatch";
        assert url.contains("sslmode=verify-full") && url.contains("connectTimeout=5") : "Query parameters missing";
        System.out.println("Exercise 1 passed: " + url);

        // Test 2: Safe Nullable Integer
        try (Connection conn = DriverManager.getConnection(JDBC_URL, "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE test_nulls (id INT, hr INT)");
            stmt.execute("INSERT INTO test_nulls VALUES (1, NULL), (2, 80)");

            try (ResultSet rs = stmt.executeQuery("SELECT hr FROM test_nulls ORDER BY id ASC")) {
                rs.next();
                Integer nullHr = extractNullableInteger(rs, "hr");
                assert nullHr == null : "Expected null for first row, got " + nullHr;

                rs.next();
                Integer validHr = extractNullableInteger(rs, "hr");
                assert validHr != null && validHr == 80 : "Expected 80 for second row";
                System.out.println("Exercise 2 passed: safe NULL extraction verified.");
            }

            // Test 3: BigDecimal Aggregation
            List<BigDecimal> doses = List.of(
                new BigDecimal("1.25"),
                new BigDecimal("2.505"),
                new BigDecimal("0.75")
            );
            BigDecimal total = aggregateRadiationDose(doses);
            assert new BigDecimal("4.51").compareTo(total) == 0 : "Expected 4.51, got " + total;
            System.out.println("Exercise 3 passed: BigDecimal total = " + total);

            // Test 4: Batch Insert
            stmt.execute("CREATE TABLE test_batch_patients (mrn VARCHAR(50))");
            List<String> mrns = List.of("MRN-A", "MRN-B", "MRN-C", "MRN-D", "MRN-E");
            int inserted = executeBatchInsert(conn, mrns);
            assert inserted == 5 : "Expected 5 rows inserted in batch, got " + inserted;
            System.out.println("Exercise 4 passed: batch inserted " + inserted + " rows.");

            System.out.println("All Unit 3.3 Solution Exercises Passed Successfully!");
        }
    }
}
