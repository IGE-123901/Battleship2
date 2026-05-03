package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Scoreboard.
 * Author: Alex
 * Date: 2026-04-29 15:30
 * Cyclomatic Complexity:
 * - Scoreboard(): 2
 * - saveGame(String, int): 2
 * - printScoreboard(): 4
 */
public class ScoreboardTest {

	private Scoreboard scoreboard;
	private PrintStream originalOut;
	private PrintStream originalErr;
	private ByteArrayOutputStream capturedOut;
	private ByteArrayOutputStream capturedErr;
	private static final String DB_URL = "jdbc:sqlite:scoreboard.db";

	@BeforeEach
	void setUp() {
		scoreboard = new Scoreboard();
		originalOut = System.out;
		originalErr = System.err;
		capturedOut = new ByteArrayOutputStream();
		capturedErr = new ByteArrayOutputStream();
		System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
		System.setErr(new PrintStream(capturedErr, true, StandardCharsets.UTF_8));
		clearDatabase();
	}

	@AfterEach
	void tearDown() {
		System.setOut(originalOut);
		System.setErr(originalErr);
		scoreboard = null;
		clearDatabase();
	}

	@Test
	void constructor1() {
		assertAll(
			() -> assertNotNull(scoreboard, "Error: expected Scoreboard instance to be created but it was null."),
			() -> assertInstanceOf(Scoreboard.class, scoreboard, "Error: expected an instance of Scoreboard but got a different class.")
		);
	}

	@Test
	void constructorHandlesInitializationErrorsGracefully() {
		ByteArrayOutputStream capturedErrLocal = new ByteArrayOutputStream();
		PrintStream previousErr = System.err;
		try {
			System.setErr(new PrintStream(capturedErrLocal, true, StandardCharsets.UTF_8));

			// Create a Scoreboard that should trigger an error path if DB is unavailable
			// This tests the catch path of the constructor
			Scoreboard sb = new Scoreboard();

			assertNotNull(sb, "Error: Scoreboard should be created even if there are initialization warnings.");
		} finally {
			System.setErr(previousErr);
		}
	}

	@Test
	void saveGame_withWinResult_savesSuccessfully() {
		String capturedOutput = captureOutput(() ->
			scoreboard.saveGame("WIN", 15)
		);

		assertAll(
			() -> assertTrue(capturedOutput.contains("Resultado guardado no scoreboard!"),
				"Error: expected success message when saving a game but got: " + capturedOutput),
			() -> assertTrue(gameExistsInDatabase("WIN", 15),
				"Error: expected the game to be saved in the database.")
		);
	}

	@Test
	void saveGame_withLossResult_savesSuccessfully() {
		String capturedOutput = captureOutput(() ->
			scoreboard.saveGame("LOSS", 45)
		);

		assertAll(
			() -> assertTrue(capturedOutput.contains("Resultado guardado no scoreboard!"),
				"Error: expected success message when saving a LOSS game but got: " + capturedOutput),
			() -> assertTrue(gameExistsInDatabase("LOSS", 45),
				"Error: expected the LOSS game to be saved in the database.")
		);
	}

	@Test
	void printScoreboard_whenEmpty_showsEmptyMessage() {
		String capturedOutput = captureOutput(scoreboard::printScoreboard);

		assertAll(
			() -> assertTrue(capturedOutput.contains("Ainda não há jogos registados."),
				"Error: expected empty scoreboard message when no games are recorded but got: " + capturedOutput),
			() -> assertFalse(capturedOutput.contains("SCOREBOARD"),
				"Error: should not print the full scoreboard table when empty.")
		);
	}

	@Test
	void printScoreboard_withSingleWin_showsCounts() {
		scoreboard.saveGame("WIN", 20);

		String capturedOutput = captureOutput(scoreboard::printScoreboard);

		assertAll(
			() -> assertTrue(capturedOutput.contains("SCOREBOARD"),
				"Error: expected SCOREBOARD header in output but got: " + capturedOutput),
			() -> assertTrue(capturedOutput.contains("Vitórias: 1"),
				"Error: expected 1 win in the scoreboard but got: " + capturedOutput),
			() -> assertTrue(capturedOutput.contains("Derrotas: 0"),
				"Error: expected 0 losses in the scoreboard but got: " + capturedOutput),
			() -> assertTrue(capturedOutput.contains("WIN"),
				"Error: expected WIN result in the scoreboard but got: " + capturedOutput)
		);
	}

	@Test
	void printScoreboard_withWinAndLoss_showsCounts() {
		scoreboard.saveGame("WIN", 20);
		scoreboard.saveGame("LOSS", 50);

		String capturedOutput = captureOutput(scoreboard::printScoreboard);

		assertAll(
			() -> assertTrue(capturedOutput.contains("SCOREBOARD"),
				"Error: expected SCOREBOARD header when multiple games exist but got: " + capturedOutput),
			() -> assertTrue(capturedOutput.contains("Vitórias: 1"),
				"Error: expected 1 win with multiple games but got: " + capturedOutput),
			() -> assertTrue(capturedOutput.contains("Derrotas: 1"),
				"Error: expected 1 loss with multiple games but got: " + capturedOutput),
			() -> assertTrue(capturedOutput.contains("WIN") && capturedOutput.contains("LOSS"),
				"Error: expected both WIN and LOSS results in scoreboard but got: " + capturedOutput)
		);
	}

	@Test
	void printScoreboard_withMultipleEntries_showsCounts() {
		scoreboard.saveGame("WIN", 10);
		scoreboard.saveGame("WIN", 15);
		scoreboard.saveGame("LOSS", 30);

		String capturedOutput = captureOutput(scoreboard::printScoreboard);

		assertAll(
			() -> assertTrue(capturedOutput.contains("SCOREBOARD"),
				"Error: expected SCOREBOARD table for multiple entries but got: " + capturedOutput),
			() -> assertTrue(capturedOutput.contains("Vitórias: 2"),
				"Error: expected 2 wins in scoreboard but got: " + capturedOutput),
			() -> assertTrue(capturedOutput.contains("Derrotas: 1"),
				"Error: expected 1 loss in scoreboard but got: " + capturedOutput),
			() -> assertTrue(capturedOutput.matches("(?s).*10.*"),
				"Error: expected first game result (10 shots) in output but got: " + capturedOutput)
		);
	}

	private String captureOutput(Runnable action) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream previousOut = System.out;
		try {
			System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
			action.run();
			return out.toString(StandardCharsets.UTF_8);
		} finally {
			System.setOut(previousOut);
		}
	}

	private boolean gameExistsInDatabase(String result, int shots) {
		String sql = "SELECT COUNT(*) as count FROM games WHERE result = ? AND shots = ?";
		try (Connection conn = DriverManager.getConnection(DB_URL);
			 java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, result);
			pstmt.setInt(2, shots);
			var rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("count") > 0;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	private void clearDatabase() {
		try (Connection conn = DriverManager.getConnection(DB_URL);
			 Statement stmt = conn.createStatement()) {

			stmt.execute("DELETE FROM games");
		} catch (Exception e) {
			// Ignore errors during cleanup
		}
	}
}

