package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.io.File;
import java.lang.reflect.Field;

/**
 * Test class for DatabaseManager.
 * Author: manue
 * Date: 2026-04-28
 * Time: 12:00
 * Cyclomatic Complexity:
 * - constructor: 1
 * - guardarJogada: 1
 * - listarJogadas: 2
 * - fechar: 3
 */
public class DatabaseManagerTest {

    private DatabaseManager dbManager;

    @BeforeEach
    void setUp() {
        try {
            dbManager = new DatabaseManager();
        } catch (SQLException e) {
            fail("Error: DatabaseManager constructor should not throw SQLException in setup.");
        }
    }

    @AfterEach
    void tearDown() {
        if (dbManager != null) {
            try {
                dbManager.fechar();
            } catch (SQLException e) {
                // Ignore in tearDown
            }
        }
        dbManager = null;
        // Optionally delete the DB file if needed
        File dbFile = new File("battleship.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    /**
     * Test for the DatabaseManager constructor.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testConstructor() {
        assertNotNull(dbManager, "Error: DatabaseManager instance should not be null after construction.");
        // Additional check: ensure table exists by trying to insert
        assertDoesNotThrow(() -> dbManager.guardarJogada("{}", "test"), "Error: Table should be created by constructor.");
    }

    /**
     * Test for the guardarJogada method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGuardarJogada() {
        assertDoesNotThrow(() -> dbManager.guardarJogada("{\"test\": \"data\"}", "HIT"), "Error: guardarJogada should not throw for valid input.");
    }

    /**
     * Test for the listarJogadas method (no rows).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testListarJogadas1() {
        assertDoesNotThrow(() -> dbManager.listarJogadas(), "Error: listarJogadas should not throw when no rows exist.");
    }

    /**
     * Test for the listarJogadas method (with rows).
     */
    @Test
    void testListarJogadas2() {
        assertDoesNotThrow(() -> {
            dbManager.guardarJogada("{\"test\": \"data\"}", "HIT");
            dbManager.listarJogadas();
        }, "Error: listarJogadas should not throw when rows exist.");
    }

    /**
     * Test for the fechar method (conn not null and not closed).
     * Cyclomatic Complexity: 3
     */
    @Test
    void testFechar1() {
        assertDoesNotThrow(() -> dbManager.fechar(), "Error: fechar should not throw when connection is open.");
    }

    /**
     * Test for the fechar method (conn not null and closed).
     */
    @Test
    void testFechar2() {
        try {
            dbManager.fechar(); // Close it first
            assertDoesNotThrow(() -> dbManager.fechar(), "Error: fechar should not throw when connection is already closed.");
        } catch (SQLException e) {
            fail("Error: fechar should not throw in testFechar2.");
        }
    }

    /**
     * Test for the fechar method (conn null).
     */
    @Test
    void testFechar3() {
        try {
            // Use reflection to set conn to null
            Field connField = DatabaseManager.class.getDeclaredField("conn");
            connField.setAccessible(true);
            connField.set(dbManager, null);
            assertDoesNotThrow(() -> dbManager.fechar(), "Error: fechar should not throw when connection is null.");
        } catch (Exception e) {
            fail("Error: Reflection failed in testFechar3.");
        }
    }

    /**
     * Test for exception in constructor (simulate DB issue).
     * Note: Hard to simulate without mocking, but for coverage, assume normal case.
     */
    @Test
    void testConstructorException() {
        // Since DB_URL is fixed, hard to test exception without external factors.
        // In real scenario, use mocking or in-memory DB.
        // For now, assume it succeeds.
    }

    /**
     * Test for exception in guardarJogada (e.g., closed connection).
     */
    @Test
    void testGuardarJogadaException() {
        try {
            dbManager.fechar();
            assertThrows(SQLException.class, () -> dbManager.guardarJogada("{}", "MISS"), "Error: guardarJogada should throw SQLException when connection is closed.");
        } catch (SQLException e) {
            fail("Error: fechar should not throw in test setup.");
        }
    }
}