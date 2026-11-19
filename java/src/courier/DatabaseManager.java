package courier;

import java.sql.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * DatabaseManager — Singleton that holds the SQLite connection.
 * Initialises the schema on first run.
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;

    // Resolve path relative to the project root (two levels up from java/src/courier/)
    private static final String DB_URL = "jdbc:sqlite:database/courier_tracking.db";

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] SQLite JDBC driver not found. Add sqlite-jdbc jar to classpath.");
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("[ERROR] Cannot connect to database: " + e.getMessage());
            System.exit(1);
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeDatabase() {
        // Check if tables already exist
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getTables(null, null, "customers", null);
            if (rs.next()) {
                rs.close();
                return; // Tables already created
            }
            rs.close();
        } catch (SQLException e) {
            // proceed to create
        }

        // Execute schema.sql
        executeSqlFile("sql/schema.sql");
        System.out.println("[INFO] Database schema initialised.");

        // Optionally load sample data
        File sampleData = new File("sql/sample_data.sql");
        if (sampleData.exists()) {
            executeSqlFile("sql/sample_data.sql");
            System.out.println("[INFO] Sample data loaded.");
        }
    }

    private void executeSqlFile(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.startsWith("--") && !trimmed.isEmpty()) {
                    sb.append(line).append("\n");
                }
            }
            String[] statements = sb.toString().split(";");
            try (Statement stmt = connection.createStatement()) {
                for (String s : statements) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[WARN] SQL file not found: " + filePath);
        } catch (SQLException e) {
            System.err.println("[WARN] SQL execution error: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[INFO] Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error closing connection: " + e.getMessage());
        }
    }
}
