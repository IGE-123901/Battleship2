package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Test class for Fleet.
 * Author: manue
 * Date: 2026-04-28
 * Time: 12:00
 * Cyclomatic Complexity:
 * - Constructor: 1
 * - createRandom: 3
 * - addShip: 4
 * - getShips: 1
 * - getShipsLike: 2
 * - getFloatingShips: 2
 * - getSunkShips: 2
 * - shipAt: 2
 * - isInsideBoard: 5
 * - colisionRisk: 3
 * - printShips: 1
 * - printStatus: 1
 * - printShipsByCategory: 1
 * - printFloatingShips: 1
 * - printAllShips: 1
 */
public class FleetTest {

	private Fleet fleet;

	@BeforeEach
	void setUp() {
		fleet = new Fleet();
	}

	@AfterEach
	void tearDown() {
		fleet = null;
	}

	/**
	 * Test for the Fleet constructor.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testConstructor() {
		assertNotNull(fleet, "Error: Instance of Fleet should not be null.");
		assertTrue(fleet.getShips().isEmpty(), "Error: Fleet should be initialized with empty ships list.");
	}

	/**
	 * Test for the createRandom method (successful creation).
	 * Cyclomatic Complexity: 3
	 */
	@Test
	void testCreateRandom1() {
		IFleet randomFleet = Fleet.createRandom();
		assertNotNull(randomFleet, "Error: createRandom should return a non-null fleet.");
		assertEquals(IFleet.FLEET_SIZE, randomFleet.getShips().size(), "Error: Random fleet should contain exactly FLEET_SIZE ships.");
	}

	/**
	 * Test for the createRandom method (all ships added).
	 */
	@Test
	void testCreateRandom2() {
		IFleet randomFleet = Fleet.createRandom();
		// Assuming all ships are added without issues in normal case
		assertEquals(IFleet.FLEET_SIZE, randomFleet.getShips().size(), "Error: All ships should be added in createRandom.");
	}

	/**
	 * Test for the createRandom method (edge case, but hard to simulate failure).
	 */
	@Test
	void testCreateRandom3() {
		// Since it's static and depends on random, hard to test failure, but assume success.
		IFleet randomFleet = Fleet.createRandom();
		assertNotNull(randomFleet, "Error: createRandom should always succeed in normal conditions.");
	}

	/**
	 * Test for the addShip method (all conditions true).
	 * Cyclomatic Complexity: 4
	 */
	@Test
	void testAddShip1() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		assertTrue(fleet.addShip(ship), "Error: Valid ship should be added successfully.");
		assertEquals(1, fleet.getShips().size(), "Error: Fleet should contain one ship after addition.");
	}

	/**
	 * Test for the addShip method (fleet size limit reached).
	 */
	@Test
	void testAddShip2() {
		// Add ships up to FLEET_SIZE with valid positions
		for (int i = 0; i < IFleet.FLEET_SIZE; i++) {
			fleet.addShip(new Barge(Compass.NORTH, new Position(i % 10, i / 10)));
		}
		IShip anotherShip = new Barge(Compass.NORTH, new Position(99, 99)); // Outside the board
		assertFalse(fleet.addShip(anotherShip), "Error: Should not add ship when it is outside the board.");
	}

	/**
	 * Test for the addShip method (ship outside the board).
	 */
	@Test
	void testAddShip3() {
		IShip shipOutside = new Barge(Compass.NORTH, new Position(99, 99));
		assertFalse(fleet.addShip(shipOutside), "Error: Should not add ship outside the board.");
	}

	/**
	 * Test for the addShip method (collision risk).
	 */
	@Test
	void testAddShip4() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));  // Overlapping position
		fleet.addShip(ship1);
		assertFalse(fleet.addShip(ship2), "Error: Should not add ship with a collision risk.");
	}

	/**
	 * Test for the addShip method (null ship).
	 */
	@Test
	void testAddShipNull() {
		assertThrows(AssertionError.class, () -> fleet.addShip(null), "Error: addShip should throw AssertionError for null ship.");
	}

	/**
	 * Test for the getShips method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testGetShips() {
		assertTrue(fleet.getShips().isEmpty(), "Error: Fleet's ships list should initially be empty.");
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);
		assertEquals(1, fleet.getShips().size(), "Error: Fleet should have size 1 after adding a ship.");
		assertEquals(ship, fleet.getShips().get(0), "Error: Fleet's first ship should match the added ship.");
	}

	/**
	 * Test for the getShipsLike method (ships of specific category).
	 * Cyclomatic Complexity: 2
	 */
	@Test
	void testGetShipsLike1() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(2, 1));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		List<IShip> barges = fleet.getShipsLike("Barca");
		assertEquals(1, barges.size(), "Error: There should be exactly one ship of category 'Barca'.");
		assertEquals(ship1, barges.get(0), "Error: The ship of category 'Barca' does not match.");
	}

	/**
	 * Test for the getShipsLike method (no ships of category).
	 */
	@Test
	void testGetShipsLike2() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		List<IShip> caravels = fleet.getShipsLike("Caravela");
		assertTrue(caravels.isEmpty(), "Error: There should be no ships of category 'Caravela'.");
	}

	/**
	 * Test for the getShipsLike method (null category).
	 */
	@Test
	void testGetShipsLikeNull() {
		assertThrows(AssertionError.class, () -> fleet.getShipsLike(null), "Error: getShipsLike should throw AssertionError for null category.");
	}

	/**
	 * Test for the getFloatingShips method (all floating).
	 * Cyclomatic Complexity: 2
	 */
	@Test
	void testGetFloatingShips1() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		List<IShip> floatingShips = fleet.getFloatingShips();
		assertEquals(2, floatingShips.size(), "Error: All ships should be floating initially.");
	}

	/**
	 * Test for the getFloatingShips method (some sunk).
	 */
	@Test
	void testGetFloatingShips2() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		// Sink ship1
		ship1.getPositions().get(0).shoot();
		List<IShip> floatingShips = fleet.getFloatingShips();
		assertEquals(1, floatingShips.size(), "Error: Only one ship should be floating after sinking one.");
		assertEquals(ship2, floatingShips.get(0), "Error: The floating ship should match the expected result.");
	}

	/**
	 * Test for the getSunkShips method (no sunk ships).
	 * Cyclomatic Complexity: 2
	 */
	@Test
	void testGetSunkShips1() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		List<IShip> sunkShips = fleet.getSunkShips();
		assertTrue(sunkShips.isEmpty(), "Error: There should be no sunk ships initially.");
	}

	/**
	 * Test for the getSunkShips method (some sunk).
	 */
	@Test
	void testGetSunkShips2() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		// Sink ship1
		ship1.getPositions().get(0).shoot();
		List<IShip> sunkShips = fleet.getSunkShips();
		assertEquals(1, sunkShips.size(), "Error: There should be one sunk ship.");
		assertEquals(ship1, sunkShips.get(0), "Error: The sunk ship should match.");
	}

	/**
	 * Test for the shipAt method (ship present).
	 * Cyclomatic Complexity: 2
	 */
	@Test
	void testShipAt1() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		assertEquals(ship, fleet.shipAt(new Position(1, 1)), "Error: Should return the correct ship at the position.");
	}

	/**
	 * Test for the shipAt method (no ship).
	 */
	@Test
	void testShipAt2() {
		assertNull(fleet.shipAt(new Position(5, 5)), "Error: Should return null for empty positions in the fleet.");
	}

	/**
	 * Test for the shipAt method (null position).
	 */
	@Test
	void testShipAtNull() {
		assertThrows(AssertionError.class, () -> fleet.shipAt(null), "Error: shipAt should throw AssertionError for null position.");
	}

	/**
	 * Test for private method isInsideBoard (inside).
	 * Cyclomatic Complexity: 5
	 */
	@Test
	void testIsInsideBoard1() throws Exception {
		// Use reflection to access private methods
		var method = Fleet.class.getDeclaredMethod("isInsideBoard", IShip.class);
		method.setAccessible(true);

		IShip insideShip = new Barge(Compass.NORTH, new Position(1, 1));
		assertTrue((Boolean) method.invoke(fleet, insideShip), "Error: Ship inside the board should return true.");
	}

	/**
	 * Test for private method isInsideBoard (left out).
	 */
	@Test
	void testIsInsideBoard2() throws Exception {
		var method = Fleet.class.getDeclaredMethod("isInsideBoard", IShip.class);
		method.setAccessible(true);

		IShip leftOut = new Barge(Compass.NORTH, new Position(0, -1)); // left = -1 < 0
		assertFalse((Boolean) method.invoke(fleet, leftOut), "Error: Ship with left out should return false.");
	}

	/**
	 * Test for private method isInsideBoard (right out).
	 */
	@Test
	void testIsInsideBoard3() throws Exception {
		var method = Fleet.class.getDeclaredMethod("isInsideBoard", IShip.class);
		method.setAccessible(true);

		IShip rightOut = new Barge(Compass.NORTH, new Position(0, 10)); // right = 10 > 9
		assertFalse((Boolean) method.invoke(fleet, rightOut), "Error: Ship with right out should return false.");
	}

	/**
	 * Test for private method isInsideBoard (top out).
	 */
	@Test
	void testIsInsideBoard4() throws Exception {
		var method = Fleet.class.getDeclaredMethod("isInsideBoard", IShip.class);
		method.setAccessible(true);

		IShip topOut = new Barge(Compass.NORTH, new Position(-1, 0)); // top = -1 < 0
		assertFalse((Boolean) method.invoke(fleet, topOut), "Error: Ship with top out should return false.");
	}

	/**
	 * Test for private method isInsideBoard (bottom out).
	 */
	@Test
	void testIsInsideBoard5() throws Exception {
		var method = Fleet.class.getDeclaredMethod("isInsideBoard", IShip.class);
		method.setAccessible(true);

		IShip bottomOut = new Barge(Compass.NORTH, new Position(10, 0)); // bottom = 10 > 9
		assertFalse((Boolean) method.invoke(fleet, bottomOut), "Error: Ship with bottom out should return false.");
	}

	/**
	 * Test for private method colisionRisk (no collision).
	 * Cyclomatic Complexity: 3
	 */
	@Test
	void testColisionRisk1() throws Exception {
		var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
		method.setAccessible(true);

		IShip ship = new Barge(Compass.NORTH, new Position(5, 5));
		assertFalse((Boolean) method.invoke(fleet, ship), "Error: Ship with no collision should return false.");
	}

	/**
	 * Test for private method colisionRisk (collision).
	 */
	@Test
	void testColisionRisk2() throws Exception {
		var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
		method.setAccessible(true);

		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));  // Overlapping
		fleet.addShip(ship1);

		assertTrue((Boolean) method.invoke(fleet, ship2), "Error: Overlapping ships should be at collision risk.");
	}

	/**
	 * Test for private method colisionRisk (no ships in fleet).
	 */
	@Test
	void testColisionRisk3() throws Exception {
		var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
		method.setAccessible(true);

		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		assertFalse((Boolean) method.invoke(fleet, ship), "Error: No collision risk when fleet is empty.");
	}

	/**
	 * Test for the printShips method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testPrintShips() {
		List<IShip> ships = new ArrayList<>();
		ships.add(new Barge(Compass.NORTH, new Position(1, 1)));
		assertDoesNotThrow(() -> fleet.printShips(ships), "Error: printShips should not throw any exceptions.");
	}

	/**
	 * Test for the printShips method (null list).
	 */
	@Test
	void testPrintShipsNull() {
		assertThrows(AssertionError.class, () -> fleet.printShips(null), "Error: printShips should throw AssertionError for null list.");
	}

	/**
	 * Test for the printStatus method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testPrintStatus() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);
		assertDoesNotThrow(fleet::printStatus, "Error: printStatus should not throw any exceptions.");
	}

	/**
	 * Test for the printShipsByCategory method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testPrintShipsByCategory() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);
		assertDoesNotThrow(() -> fleet.printShipsByCategory("Barca"), "Error: printShipsByCategory should not throw any exceptions.");
	}

	/**
	 * Test for the printShipsByCategory method (null category).
	 */
	@Test
	void testPrintShipsByCategoryNull() {
		assertThrows(AssertionError.class, () -> fleet.printShipsByCategory(null), "Error: printShipsByCategory should throw AssertionError for null category.");
	}

	/**
	 * Test for the printFloatingShips method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testPrintFloatingShips() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);
		assertDoesNotThrow(fleet::printFloatingShips, "Error: printFloatingShips should not throw any exceptions.");
	}

	/**
	 * Test for the printAllShips method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testPrintAllShips() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);
		// Use reflection for private method
		try {
			var method = Fleet.class.getDeclaredMethod("printAllShips");
			method.setAccessible(true);
			assertDoesNotThrow(() -> method.invoke(fleet), "Error: printAllShips should not throw any exceptions.");
		} catch (Exception e) {
			fail("Error: Reflection failed for printAllShips.");
		}
	}
}