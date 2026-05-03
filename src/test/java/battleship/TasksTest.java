package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Tasks.
 * Author: Alex
 * Date: 2026-04-28 15:53
 * Cyclomatic Complexity:
 * - constructor: 1
 * - menu(): 11
 * - menuHelp(): 1
 * - buildFleet(Scanner): 3
 * - readShip(Scanner): 1
 * - readPosition(Scanner): 1
 * - readClassicPosition(Scanner): 4
 * - fecharBD(DatabaseManager): 2
 */
public class TasksTest {

	private static final String GOODBYE_MESSAGE = "Bons ventos!";
	private static final String HELP_HEADER = "AJUDA DO MENU";
	private static final String DEFAULT_ERROR = "Que comando é esse??? Repete ...";
	private static final String HISTORY_HEADER = "HISTÓRICO DE JOGADAS";
	private static final String STATUS_PREFIX = "Estado da Frota:";
	private static final String TIME_PREFIX = ">>> Tempo gasto a processar esta jogada:";
	private static final String SCOREBOARD_EMPTY = "Ainda não há jogos registados.";
	private static final String SCOREBOARD_HEADER = "SCOREBOARD";
	private static final String LEGEND = "LEGENDA";

	private Tasks tasks;
	private InputStream originalIn;
	private PrintStream originalOut;
	private PrintStream originalErr;

	@BeforeEach
	void setUp() {
		tasks = new Tasks() { };
		originalIn = System.in;
		originalOut = System.out;
		originalErr = System.err;
	}

	@AfterEach
	void tearDown() {
		Tasks.resetExitHandler();
		System.setIn(originalIn);
		System.setOut(originalOut);
		System.setErr(originalErr);
		tasks = null;
	}

	@Test
	void constructor() {
		assertAll(
				() -> assertNotNull(tasks, "Error: expected Tasks instance to be created but it was null."),
				() -> assertInstanceOf(Tasks.class, tasks, "Error: expected an instance of Tasks but got a different class.")
		);
	}

	@Test
	void menu1() {
		assertMenuOutput("desisto", GOODBYE_MESSAGE);
	}

	@Test
	void menu2() {
		assertMenuOutput("ajuda desisto", HELP_HEADER, GOODBYE_MESSAGE);
	}

	@Test
	void menu3() {
		assertMenuOutput("xyz desisto", DEFAULT_ERROR, GOODBYE_MESSAGE);
	}

	@Test
	void menu4() {
		assertMenuOutput("gerafrota desisto", LEGEND, GOODBYE_MESSAGE);
	}

	@Test
	void menu5() {
		assertMenuOutput("lefrota " + validBargeFleetTokens() + " estado mapa tiros desisto", STATUS_PREFIX, LEGEND, GOODBYE_MESSAGE);
	}

	@Test
	void menu6() {
		String input = "lefrota " + validBargeFleetTokens() + System.lineSeparator()
				+ "rajada A1 B2 C3" + System.lineSeparator()
				+ "desisto";
		assertMenuOutput(input, TIME_PREFIX, GOODBYE_MESSAGE);
	}

	@Test
	void menu7() {
		assertMenuOutput("historico desisto", HISTORY_HEADER, GOODBYE_MESSAGE);
	}

	@Test
	void menu8() {
		String output = assertDoesNotThrow(() -> runMenu("scoreboard desisto"), "Error: menu should not throw when showing the scoreboard.");
		assertTrue(output.contains(SCOREBOARD_EMPTY) || output.contains(SCOREBOARD_HEADER),
				"Error: expected scoreboard output to show either an empty-state message or the scoreboard table, but got: " + output);
	}

	@Test
	void menu9() {
		assertMenuOutput("estado mapa tiros rajada simula historico scoreboard desisto", HISTORY_HEADER, SCOREBOARD_HEADER, GOODBYE_MESSAGE);
	}

	@Test
	void menu10() {
		String input = String.join(System.lineSeparator(),
				"lefrota " + validBargeFleetTokens(),
				"rajada A1 A3 A5",
				"rajada A7 A9 C1",
				"rajada C3 C5 C7",
				"rajada C9 E1 B2"
		) + System.lineSeparator() + "desisto";

		ExitCaptureResult result = runMenuCapturingExit(input, false);
		assertAll(
				() -> assertTrue(result.exitObserved.get(), "Error: expected the rajada branch to call System.exit(0) when all ships are sunk."),
				() -> assertTrue(result.output.contains(TIME_PREFIX), "Error: expected the rajada run to process at least one move but got: " + result.output)
		);
	}

	@Test
	void menu11() {
		String input = String.join(System.lineSeparator(),
				"lefrota " + validBargeFleetTokens(),
				"simula",
				"desisto"
		) + System.lineSeparator();

		ExitCaptureResult result = runMenuCapturingExit(input, true);
		assertAll(
				() -> assertTrue(result.exitObserved.get(), "Error: expected the simula branch to call System.exit(0) when the game finishes."),
				() -> assertTrue(result.output.contains(TIME_PREFIX), "Error: expected the simulation to process at least one move but got: " + result.output)
		);
	}

	@Test
	void menu12() {
		assertMenuOutput("status desisto", GOODBYE_MESSAGE);
	}

	@Test
	void menu13() {
		assertMenuOutput("mapa desisto", GOODBYE_MESSAGE);
	}

	@Test
	void menu14() {
		assertMenuOutput("tiros desisto", GOODBYE_MESSAGE);
	}

	@Test
	void menu15() {
		assertMenuOutput("rajada desisto", GOODBYE_MESSAGE);
	}

	@Test
	void menu16() {
		assertMenuOutput("simula desisto", GOODBYE_MESSAGE);
	}

	@Test
	void menu17() {
		String input = String.join(System.lineSeparator(),
				"lefrota " + validBargeFleetTokens(),
				"rajada A1 B2 C3",
				"rajada A4 A6 A8",
				"desisto"
		) + System.lineSeparator();
		assertMenuOutput(input, TIME_PREFIX, STATUS_PREFIX, GOODBYE_MESSAGE);
	}

	@Test
	void menu18() {
		String input = String.join(System.lineSeparator(),
				"lefrota " + validBargeFleetTokens(),
				"mapa",
				"desisto"
		) + System.lineSeparator();
		assertMenuOutput(input, LEGEND, GOODBYE_MESSAGE);
	}

	@Test
	void menu19() {
		String input = String.join(System.lineSeparator(),
				"lefrota " + validBargeFleetTokens(),
				"tiros",
				"desisto"
		) + System.lineSeparator();
		assertMenuOutput(input, LEGEND, GOODBYE_MESSAGE);
	}

	@Test
	void menu20() {
		String input = String.join(System.lineSeparator(),
				"lefrota " + validBargeFleetTokens(),
				"estado",
				"desisto"
		) + System.lineSeparator();
		assertMenuOutput(input, STATUS_PREFIX, GOODBYE_MESSAGE);
	}

	@Test
	void menu21() throws Exception {
		String input = String.join(System.lineSeparator(),
				"lefrota " + validBargeFleetTokens(),
				"historico",
				"desisto"
		) + System.lineSeparator();
		assertMenuOutput(input, HISTORY_HEADER, GOODBYE_MESSAGE);
	}

	@Test
	void menu22() {
		String input = String.join(System.lineSeparator(),
				"gerafrota",
				"rajada A1 A3 A5",
				"desisto"
		) + System.lineSeparator();
		assertMenuOutput(input, TIME_PREFIX, GOODBYE_MESSAGE);
	}

	@Test
	void menu23() {
		String input = String.join(System.lineSeparator(),
				"gerafrota",
				"estado",
				"mapa",
				"tiros",
				"desisto"
		) + System.lineSeparator();
		assertMenuOutput(input, STATUS_PREFIX, LEGEND, GOODBYE_MESSAGE);
	}

	@Test
	void menuHelp() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream capture = new PrintStream(out, true, StandardCharsets.UTF_8);
		PrintStream previousOut = System.out;
		try {
			System.setOut(capture);
			Tasks.menuHelp();
		} finally {
			System.setOut(previousOut);
		}

		String output = out.toString(StandardCharsets.UTF_8);
		assertAll(
				() -> assertTrue(output.contains(HELP_HEADER), "Error: expected help output to contain the help header but got: " + output),
				() -> assertTrue(output.contains("gerafrota"), "Error: expected help output to mention 'gerafrota' but got: " + output),
				() -> assertTrue(output.contains("desisto"), "Error: expected help output to mention 'desisto' but got: " + output)
		);
	}

	@Test
	void buildFleet1() {
		try (Scanner scanner = new Scanner(validBargeFleetTokens())) {
			Fleet fleet = Tasks.buildFleet(scanner);
			assertAll(
					() -> assertNotNull(fleet, "Error: expected a Fleet instance but got null."),
					() -> assertEquals(Fleet.FLEET_SIZE, fleet.getShips().size(), "Error: expected a full fleet of 11 ships but got " + fleet.getShips().size()),
					() -> assertEquals(new Position(0, 0), fleet.getShips().get(0).getPosition(), "Error: expected the first ship to be at A1 but it was not."),
					() -> assertEquals(new Position(4, 0), fleet.getShips().get(fleet.getShips().size() - 1).getPosition(), "Error: expected the last ship to be at E1 but it was not.")
			);
		}
	}

	@Test
	void buildFleet2() {
		String input = "foo 9 9 n " + validBargeFleetTokens();
		try (Scanner scanner = new Scanner(input)) {
			Fleet fleet = Tasks.buildFleet(scanner);
			assertAll(
					() -> assertEquals(Fleet.FLEET_SIZE, fleet.getShips().size(), "Error: expected the invalid ship kind to be skipped while still filling the fleet."),
					() -> assertEquals(new Position(0, 0), fleet.getShips().get(0).getPosition(), "Error: expected the first valid ship to be added after the invalid token."),
					() -> assertEquals(new Position(4, 0), fleet.getShips().get(fleet.getShips().size() - 1).getPosition(), "Error: expected the fleet to finish with the last valid ship.")
			);
		}
	}

	@Test
	void buildFleet3() {
		String input = "barca 0 0 n barca 0 0 n " + validBargeFleetTokensWithoutFirst();
		try (Scanner scanner = new Scanner(input)) {
			Fleet fleet = Tasks.buildFleet(scanner);
			assertAll(
					() -> assertEquals(Fleet.FLEET_SIZE, fleet.getShips().size(), "Error: expected the duplicate ship to be rejected while the remaining ships completed the fleet."),
					() -> assertEquals(new Position(0, 0), fleet.getShips().get(0).getPosition(), "Error: expected the first ship to remain at the initial position."),
					() -> assertEquals(new Position(0, 2), fleet.getShips().get(1).getPosition(), "Error: expected the second stored ship to be the first non-duplicate position.")
			);
		}
	}

	@Test
	void readShip1() {
		try (Scanner scanner = new Scanner("fragata 2 2 e")) {
			Ship ship = Tasks.readShip(scanner);
			assertAll(
					() -> assertInstanceOf(Frigate.class, ship, "Error: expected a Frigate instance but got a different ship type."),
					() -> assertEquals("Fragata", ship.getCategory(), "Error: expected category 'Fragata' but got " + ship.getCategory()),
					() -> assertEquals(Compass.EAST, ship.getBearing(), "Error: expected bearing EAST but got " + ship.getBearing()),
					() -> assertEquals(new Position(2, 2), ship.getPosition(), "Error: expected ship position (2,2) but got " + ship.getPosition())
			);
		}
	}

	@Test
	void readShip2() {
		try (Scanner scanner = new Scanner("barca 1 1 x")) {
			assertThrows(AssertionError.class, () -> Tasks.readShip(scanner), "Error: expected an invalid bearing to trigger an AssertionError from buildShip().");
		}
	}

	@Test
	void readPosition() {
		try (Scanner scanner = new Scanner("5 6")) {
			Position position = Tasks.readPosition(scanner);
			assertAll(
					() -> assertEquals(5, position.getRow(), "Error: expected row 5 but got " + position.getRow()),
					() -> assertEquals(6, position.getColumn(), "Error: expected column 6 but got " + position.getColumn())
			);
		}
	}

	@Test
	void readClassicPosition1() {
		try (Scanner scanner = new Scanner("")) {
			assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(scanner), "Error: expected an empty scanner to throw IllegalArgumentException.");
		}
	}

	@Test
	void readClassicPosition2() {
		try (Scanner scanner = new Scanner("A3")) {
			IPosition position = Tasks.readClassicPosition(scanner);
			assertAll(
					() -> assertEquals('A', position.getClassicRow(), "Error: expected classic row A but got " + position.getClassicRow()),
					() -> assertEquals(3, position.getClassicColumn(), "Error: expected classic column 3 but got " + position.getClassicColumn()),
					() -> assertEquals(new Position('A', 3), position, "Error: expected the parsed position to match A3 but got " + position)
			);
		}
	}

	@Test
	void readClassicPosition3() {
		try (Scanner scanner = new Scanner("b 7")) {
			IPosition position = Tasks.readClassicPosition(scanner);
			assertAll(
					() -> assertEquals('B', position.getClassicRow(), "Error: expected classic row B but got " + position.getClassicRow()),
					() -> assertEquals(7, position.getClassicColumn(), "Error: expected classic column 7 but got " + position.getClassicColumn()),
					() -> assertEquals(new Position('B', 7), position, "Error: expected the parsed position to match B7 but got " + position)
			);
		}
	}

	@Test
	void readClassicPosition4() {
		try (Scanner scanner = new Scanner("AA3")) {
			assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(scanner), "Error: expected an invalid format to throw IllegalArgumentException.");
		}
	}

	@Test
	void fecharBD1() throws Exception {
		Method method = Tasks.class.getDeclaredMethod("fecharBD", DatabaseManager.class);
		method.setAccessible(true);
		assertDoesNotThrow(() -> method.invoke(null, new Object[]{null}), "Error: expected fecharBD(null) to do nothing without throwing.");
	}

	@Test
	void fecharBD2() throws Exception {
		DatabaseManager db = new DatabaseManager();
		Method method = Tasks.class.getDeclaredMethod("fecharBD", DatabaseManager.class);
		method.setAccessible(true);
		method.invoke(null, db);

		Field connField = DatabaseManager.class.getDeclaredField("conn");
		connField.setAccessible(true);
		Connection connection = (Connection) connField.get(db);

		assertAll(
				() -> assertNotNull(connection, "Error: expected the DatabaseManager connection to exist before closing it."),
				() -> assertTrue(connection.isClosed(), "Error: expected the DatabaseManager connection to be closed after fecharBD().")
		);
	}

	@Test
	void fecharBD3() throws Exception {
		class ThrowingDatabaseManager extends DatabaseManager {
			ThrowingDatabaseManager() throws Exception {
				super();
			}

			@Override
			public void fechar() throws java.sql.SQLException {
				throw new java.sql.SQLException("Forced failure for test coverage");
			}
		}

		ThrowingDatabaseManager db = new ThrowingDatabaseManager();
		Method method = Tasks.class.getDeclaredMethod("fecharBD", DatabaseManager.class);
		method.setAccessible(true);
		PrintStream previousErr = System.err;
		ByteArrayOutputStream errContent = new ByteArrayOutputStream();
		PrintStream captureErr = new PrintStream(errContent, true, StandardCharsets.UTF_8);

		try {
			System.setErr(captureErr);
			assertDoesNotThrow(() -> method.invoke(null, db), "Error: expected fecharBD() to catch SQLException thrown by DatabaseManager.fechar().");
		} finally {
			System.setErr(previousErr);
		}
	}

	private void assertMenuOutput(String input, String... expectedFragments) {
		String output = assertDoesNotThrow(() -> runMenu(input), "Error: menu should not throw for input: " + input);
		for (String fragment : expectedFragments) {
			assertTrue(output.contains(fragment), "Error: expected menu output to contain '" + fragment + "' but got: " + output);
		}
	}

	private String runMenu(String input) {
		ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		ByteArrayOutputStream errContent = new ByteArrayOutputStream();
		InputStream previousIn = System.in;
		PrintStream previousOut = System.out;
		PrintStream previousErr = System.err;
		PrintStream captureOut = new PrintStream(outContent, true, StandardCharsets.UTF_8);
		PrintStream captureErr = new PrintStream(errContent, true, StandardCharsets.UTF_8);

		try {
			System.setIn(inContent);
			System.setOut(captureOut);
			System.setErr(captureErr);
			Tasks.menu();
		} finally {
			System.setIn(previousIn);
			System.setOut(previousOut);
			System.setErr(previousErr);
		}

		return outContent.toString(StandardCharsets.UTF_8);
	}

	private ExitCaptureResult runMenuCapturingExit(String input, boolean keepInterrupting) {
		ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		ByteArrayOutputStream errContent = new ByteArrayOutputStream();
		InputStream previousIn = System.in;
		PrintStream previousOut = System.out;
		PrintStream previousErr = System.err;
		AtomicBoolean exitObserved = new AtomicBoolean(false);
		AtomicReference<Throwable> failure = new AtomicReference<>();
		PrintStream captureOut = new PrintStream(outContent, true, StandardCharsets.UTF_8);
		PrintStream captureErr = new PrintStream(errContent, true, StandardCharsets.UTF_8);

		Tasks.setExitHandler(status -> exitObserved.set(true));

		Thread worker = new Thread(() -> {
			try {
				Tasks.menu();
			} catch (Throwable t) {
				failure.set(t);
			}
		}, "tasks-menu-test-thread");

		Thread interrupter = null;
		try {
			System.setIn(inContent);
			System.setOut(captureOut);
			System.setErr(captureErr);

			worker.start();

			if (keepInterrupting) {
				interrupter = new Thread(() -> {
					while (worker.isAlive()) {
						worker.interrupt();
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							return;
						}
					}
				}, "tasks-menu-interrupter");
				interrupter.setDaemon(true);
				interrupter.start();
			}

			long deadline = System.currentTimeMillis() + 30000;
			while (worker.isAlive() && System.currentTimeMillis() < deadline) {
				worker.join(100);
			}
			if (worker.isAlive()) {
				worker.interrupt();
				fail("Error: menu did not terminate within the expected time while trying to cover the exit branch.");
			}

			if (failure.get() != null) {
				throw new AssertionError("Error: unexpected failure while running menu.", failure.get());
			}

			return new ExitCaptureResult(outContent.toString(StandardCharsets.UTF_8), exitObserved);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AssertionError("Error: interrupted while waiting for the menu test to finish.", e);
		} finally {
			if (interrupter != null) {
				interrupter.interrupt();
			}
			worker.interrupt();
			System.setIn(previousIn);
			System.setOut(previousOut);
			System.setErr(previousErr);
			Tasks.resetExitHandler();
		}
	}

	private static final class ExitCaptureResult {
		private final String output;
		private final AtomicBoolean exitObserved;

		private ExitCaptureResult(String output, AtomicBoolean exitObserved) {
			this.output = output;
			this.exitObserved = exitObserved;
		}
	}

	private static String validBargeFleetTokens() {
		return String.join(" ",
				"barca", "0", "0", "n",
				"barca", "0", "2", "n",
				"barca", "0", "4", "n",
				"barca", "0", "6", "n",
				"barca", "0", "8", "n",
				"barca", "2", "0", "n",
				"barca", "2", "2", "n",
				"barca", "2", "4", "n",
				"barca", "2", "6", "n",
				"barca", "2", "8", "n",
				"barca", "4", "0", "n"
		);
	}

	private static String validBargeFleetTokensWithoutFirst() {
		return String.join(" ",
				"barca", "0", "2", "n",
				"barca", "0", "4", "n",
				"barca", "0", "6", "n",
				"barca", "0", "8", "n",
				"barca", "2", "0", "n",
				"barca", "2", "2", "n",
				"barca", "2", "4", "n",
				"barca", "2", "6", "n",
				"barca", "2", "8", "n",
				"barca", "4", "0", "n"
		);
	}
}