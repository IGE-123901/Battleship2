package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Scanner;

/**
 * Test class for Game.
 * Author: ${user.name}
 * Date: 2026-04-28
 * Cyclomatic Complexity:
 * - constructor: 1
 * - getMyFleet / getAlienMoves / getAlienFleet / getMyMoves: 1
 * - getRepeatedShots / getInvalidShots / getHits / getSunkShips / getRemainingShips: 1
 * - printMyBoard / printAlienBoard / over: 1
 * - jsonShots: 2
 * - repeatedShot: 3
 * - fireShots: 2
 * - fireSingleShot: 5
 * - readEnemyFire: 4
 * - randomEnemyFire: 4
 * - printBoard: 6
 */
public class GameTest {

	private Game game;
	private Fleet myFleet;

	@BeforeEach
	void setUp() {
		myFleet = new Fleet();
		game = new Game(myFleet);
	}

	@AfterEach
	void tearDown() {
		game = null;
		myFleet = null;
	}

	// ================== CONSTRUTOR E GETTERS BÁSICOS (CC = 1) ==================

	@Test
	void constructorAndGetters() {
		assertAll(
				() -> assertNotNull(game, "Error: Game instance should not be null"),
				() -> assertNotNull(game.getMyFleet(), "Error: myFleet should not be null"),
				() -> assertNotNull(game.getAlienMoves(), "Error: AlienMoves should not be null"),
				() -> assertNotNull(game.getAlienFleet(), "Error: AlienFleet should not be null"),
				() -> assertNotNull(game.getMyMoves(), "Error: MyMoves should not be null"),
				() -> assertEquals(0, game.getInvalidShots(), "Error: expected 0 invalid shots initially"),
				() -> assertEquals(0, game.getRepeatedShots(), "Error: expected 0 repeated shots initially"),
				() -> assertEquals(0, game.getHits(), "Error: expected 0 hits initially"),
				() -> assertEquals(0, game.getSunkShips(), "Error: expected 0 sunk ships initially")
		);
	}

	@Test
	void getRemainingShips1() {
		// Adicionamos dois navios para testar o counter de remaining
		Ship ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		Ship ship2 = new Frigate(Compass.EAST, new Position(5, 5));
		myFleet.addShip(ship1);
		myFleet.addShip(ship2);

		assertEquals(2, game.getRemainingShips(), "Error: expected 2 remaining ships");

		ship2.sink();
		assertEquals(1, game.getRemainingShips(), "Error: expected 1 remaining ship after one sinks");
	}

	// ================== FIRESINGLESHOT (CC = 5) ==================

	@Test
	void fireSingleShot1() {
		// Caminho 1: Posição fora do tabuleiro
		Position invalidPos = new Position(-1, 5);
		IGame.ShotResult r = game.fireSingleShot(invalidPos, false);
		assertFalse(r.valid(), "Error: shot outside board should be invalid");
		assertEquals(1, game.getInvalidShots(), "Error: invalid shots counter should increase");
	}

	@Test
	void fireSingleShot2() {
		// Caminho 2: Tiro repetido (isRepeated explícito)
		Position validPos = new Position(2, 3);
		IGame.ShotResult r = game.fireSingleShot(validPos, true);
		assertTrue(r.repeated(), "Error: shot should be flagged as repeated");
		assertEquals(1, game.getRepeatedShots(), "Error: repeated counter should increase");
	}

	@Test
	void fireSingleShot3() {
		// Caminho 3: Água (Posição válida, mas sem navio)
		Position validPos = new Position(9, 9);
		IGame.ShotResult r = game.fireSingleShot(validPos, false);
		assertTrue(r.valid(), "Error: shot should be valid");
		assertNull(r.ship(), "Error: expected null ship for water hit");
	}

	@Test
	void fireSingleShot4() {
		// Caminho 4: Hit (Acerta, mas não afunda)
		Ship s = new Frigate(Compass.NORTH, new Position(0,0)); // Fragata tem mais que 1 de tamanho
		myFleet.addShip(s);
		IGame.ShotResult r = game.fireSingleShot(new Position(0,0), false);

		assertNotNull(r.ship(), "Error: should hit a ship");
		assertFalse(r.sunk(), "Error: ship should not be sunk yet");
		assertEquals(1, game.getHits(), "Error: hits counter should increase");
	}

	@Test
	void fireSingleShot5() {
		// Caminho 5: Sink (Afunda o navio)
		Ship s = new Barge(Compass.NORTH, new Position(0,0));
		myFleet.addShip(s);

		// Dispara em todas as posições do navio para o afundar
		IGame.ShotResult lastResult = null;
		for (IPosition p : s.getPositions()) {
			lastResult = game.fireSingleShot(p, false);
		}

		assertTrue(lastResult.sunk(), "Error: ship should be marked as sunk");
		assertEquals(1, game.getSunkShips(), "Error: sunk counter should increase");
	}
	// ================== FIRESHOTS E REPEATEDSHOT (CC = 2 E CC = 3) ==================

	@Test
	void fireShots1() {
		// Exceção de tamanho (só passa 1 tiro quando deveria exigir 3)
		List<IPosition> badList = List.of(new Position(0,0));
		assertThrows(IllegalArgumentException.class, () -> game.fireShots(badList), "Error: should throw exception for wrong size");
	}

	@Test
	void fireShots2() {
		// Caminho normal
		List<IPosition> validList = List.of(new Position(0,0), new Position(0,1), new Position(0,2));
		game.fireShots(validList);
		assertEquals(1, game.getAlienMoves().size(), "Error: should register 1 move");
	}

	@Test
	void repeatedShot1() {
		// Nenhum move feito
		assertFalse(game.repeatedShot(new Position(0,0)), "Error: shouldn't be repeated on empty board");
	}

	@Test
	void repeatedShot2() {
		// Está na lista de moves
		game.fireShots(List.of(new Position(0,0), new Position(0,1), new Position(0,2)));
		assertTrue(game.repeatedShot(new Position(0,0)), "Error: should be recognized as repeated");
	}

	@Test
	void repeatedShot3() {
		// Não está na lista
		game.fireShots(List.of(new Position(0,0), new Position(0,1), new Position(0,2)));
		assertFalse(game.repeatedShot(new Position(9,9)), "Error: should not be repeated");
	}

	// ================== READENEMYFIRE (CC = 4) ==================

	@Test
	void readEnemyFire1() {
		// Formato separado por espaços: "A 3 B 4 C 5"
		Scanner sc = new Scanner("A 3 B 4 C 5");
		String json = game.readEnemyFire(sc);
		assertTrue(json.contains("\"row\""), "Error: JSON should contain rows");
	}

	@Test
	void readEnemyFire2() {
		// Formato combinado: "A3 B4 C5"
		Scanner sc = new Scanner("A3 B4 C5");
		String json = game.readEnemyFire(sc);
		assertTrue(json.contains("\"column\""), "Error: JSON should contain columns");
	}

	@Test
	void readEnemyFire3() {
		// Formato incompleto (Letra sem número a seguir)
		Scanner sc = new Scanner("A B4 C5");
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(sc), "Error: should throw for missing row");
	}

	@Test
	void readEnemyFire4() {
		// Faltam posições (só passa 2)
		Scanner sc = new Scanner("A3 B4");
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(sc), "Error: should throw for wrong amount of shots");
	}

	// ================== RANDOMENEMYFIRE E JSON (CC = 4 e CC = 2) ==================

	@Test
	void randomEnemyFire1() {
		// Testa a geração de tiros aleatórios
		String json = game.randomEnemyFire();
		assertNotNull(json, "Error: JSON should not be null");
		assertEquals(1, game.getAlienMoves().size(), "Error: should add a move to AlienMoves");
	}

	@Test
	void jsonShots1() {
		// Cobertura do método utilitário JSON
		List<IPosition> shots = List.of(new Position(0,0));
		String json = Game.jsonShots(shots);
		assertTrue(json.contains("A"), "Error: JSON should map row 0 to 'A'");
	}

	// ================== PRINTBOARDS E MÉTODOS DE CONSOLA (Cobertura de Linhas) ==================

	@Test
	void printsAndOver() {
		// Adicionar cenário completo para cobrir os ifs da consola (*, o, -, #)
		Ship s = new Barge(Compass.NORTH, new Position(0,0));
		myFleet.addShip(s);

		// Afundamos o barco (para mostrar os marcadores de afundado e adjacente)
		for (IPosition p : s.getPositions()) {
			game.fireSingleShot(p, false);
		}

		// Fazemos jogadas e chamamos os prints com e sem legendas e tiros
		game.fireShots(List.of(new Position(0,0), new Position(5,5), new Position(9,9)));

		game.printMyBoard(true, true);
		game.printMyBoard(false, false);
		game.printAlienBoard(true, true);
		game.over();
	}
}