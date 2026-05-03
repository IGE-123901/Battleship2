package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Frigate.
 * Author: manue
 * Date: 2026-04-28
 * Time: 12:00
 * Cyclomatic Complexity:
 * - constructor: 4
 * - getSize: 1
 * - stillFloating: 2
 * - getPositions: 1
 * - getTopMostPos: 2
 * - getBottomMostPos: 2
 * - getLeftMostPos: 2
 * - getRightMostPos: 2
 */
public class FrigateTest {

	private Frigate frigate;

	@BeforeEach
	void setUp() {
		frigate = new Frigate(Compass.NORTH, new Position(5, 5));
	}

	@AfterEach
	void tearDown() {
		frigate = null;
	}

	/**
	 * Test for the constructor with NORTH bearing.
	 * Cyclomatic Complexity: 4
	 */
	@Test
	void testConstructor1() {
		assertNotNull(frigate, "Error: Frigate instance should not be null.");
		assertEquals("Fragata", frigate.getCategory(), "Error: Frigate category should be 'Fragata'.");
		assertEquals(Compass.NORTH, frigate.getBearing(), "Error: Frigate bearing should be NORTH.");
		assertEquals(4, frigate.getSize(), "Error: Frigate size should be 4.");

		List<IPosition> positions = frigate.getPositions();
		assertEquals(4, positions.size(), "Error: Frigate should have exactly 4 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: First position is incorrect for NORTH.");
		assertEquals(new Position(6, 5), positions.get(1), "Error: Second position is incorrect for NORTH.");
		assertEquals(new Position(7, 5), positions.get(2), "Error: Third position is incorrect for NORTH.");
		assertEquals(new Position(8, 5), positions.get(3), "Error: Fourth position is incorrect for NORTH.");
	}

	/**
	 * Test for the constructor with SOUTH bearing.
	 */
	@Test
	void testConstructor2() {
		frigate = new Frigate(Compass.SOUTH, new Position(5, 5));
		List<IPosition> positions = frigate.getPositions();

		assertNotNull(frigate, "Error: Frigate instance should not be null.");
		assertEquals(4, positions.size(), "Error: Frigate should have exactly 4 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: First position is incorrect for SOUTH.");
		assertEquals(new Position(6, 5), positions.get(1), "Error: Second position is incorrect for SOUTH.");
		assertEquals(new Position(7, 5), positions.get(2), "Error: Third position is incorrect for SOUTH.");
		assertEquals(new Position(8, 5), positions.get(3), "Error: Fourth position is incorrect for SOUTH.");
	}

	/**
	 * Test for the constructor with EAST bearing.
	 */
	@Test
	void testConstructor3() {
		frigate = new Frigate(Compass.EAST, new Position(5, 5));
		List<IPosition> positions = frigate.getPositions();

		assertNotNull(frigate, "Error: Frigate instance should not be null.");
		assertEquals(4, positions.size(), "Error: Frigate should have exactly 4 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: First position is incorrect for EAST.");
		assertEquals(new Position(5, 6), positions.get(1), "Error: Second position is incorrect for EAST.");
		assertEquals(new Position(5, 7), positions.get(2), "Error: Third position is incorrect for EAST.");
		assertEquals(new Position(5, 8), positions.get(3), "Error: Fourth position is incorrect for EAST.");
	}

	/**
	 * Test for the constructor with WEST bearing.
	 */
	@Test
	void testConstructor4() {
		frigate = new Frigate(Compass.WEST, new Position(5, 5));
		List<IPosition> positions = frigate.getPositions();

		assertNotNull(frigate, "Error: Frigate instance should not be null.");
		assertEquals(4, positions.size(), "Error: Frigate should have exactly 4 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: First position is incorrect for WEST.");
		assertEquals(new Position(5, 6), positions.get(1), "Error: Second position is incorrect for WEST.");
		assertEquals(new Position(5, 7), positions.get(2), "Error: Third position is incorrect for WEST.");
		assertEquals(new Position(5, 8), positions.get(3), "Error: Fourth position is incorrect for WEST.");
	}

	/**
	 * Test for the getSize method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testGetSize() {
		assertEquals(4, frigate.getSize(), "Error: Frigate size should be 4.");
	}

	/**
	 * Test for the stillFloating method (all positions intact).
	 * Cyclomatic Complexity: 2
	 */
	@Test
	void testStillFloating1() {
		assertTrue(frigate.stillFloating(), "Error: Frigate should still be floating when no positions are hit.");
	}

	/**
	 * Test for the stillFloating method (some positions hit).
	 */
	@Test
	void testStillFloating2() {
		frigate.getPositions().get(0).shoot();
		frigate.getPositions().get(1).shoot();
		frigate.getPositions().get(2).shoot();
		assertTrue(frigate.stillFloating(), "Error: Frigate should still be floating when not all positions are hit.");
	}

	/**
	 * Test for the stillFloating method (all positions hit).
	 */
	@Test
	void testStillFloating3() {
		frigate.getPositions().forEach(IPosition::shoot);
		assertFalse(frigate.stillFloating(), "Error: Frigate should not be floating when all positions are hit.");
	}

	/**
	 * Test for the getPositions method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testGetPositions() {
		List<IPosition> positions = frigate.getPositions();
		assertNotNull(positions, "Error: Positions list should not be null.");
		assertEquals(4, positions.size(), "Error: Positions list should have 4 elements.");
	}

	/**
	 * Test for the getTopMostPos method (NORTH bearing).
	 * Cyclomatic Complexity: 2
	 */
	@Test
	void testGetTopMostPos1() {
		assertEquals(5, frigate.getTopMostPos(), "Error: The topmost position should be 5 for NORTH bearing.");
	}

	/**
	 * Test for the getTopMostPos method (EAST bearing).
	 */
	@Test
	void testGetTopMostPos2() {
		frigate = new Frigate(Compass.EAST, new Position(5, 5));
		assertEquals(5, frigate.getTopMostPos(), "Error: The topmost position should be 5 for EAST bearing.");
	}

	/**
	 * Test for the getBottomMostPos method (NORTH bearing).
	 * Cyclomatic Complexity: 2
	 */
	@Test
	void testGetBottomMostPos1() {
		assertEquals(8, frigate.getBottomMostPos(), "Error: The bottommost position should be 8 for NORTH bearing.");
	}

	/**
	 * Test for the getBottomMostPos method (EAST bearing).
	 */
	@Test
	void testGetBottomMostPos2() {
		frigate = new Frigate(Compass.EAST, new Position(5, 5));
		assertEquals(5, frigate.getBottomMostPos(), "Error: The bottommost position should be 5 for EAST bearing.");
	}

	/**
	 * Test for the getLeftMostPos method (NORTH bearing).
	 * Cyclomatic Complexity: 2
	 */
	@Test
	void testGetLeftMostPos1() {
		assertEquals(5, frigate.getLeftMostPos(), "Error: The leftmost position should be 5 for NORTH bearing.");
	}

	/**
	 * Test for the getLeftMostPos method (EAST bearing).
	 */
	@Test
	void testGetLeftMostPos2() {
		frigate = new Frigate(Compass.EAST, new Position(5, 5));
		assertEquals(5, frigate.getLeftMostPos(), "Error: The leftmost position should be 5 for EAST bearing.");
	}

	/**
	 * Test for the getRightMostPos method (NORTH bearing).
	 * Cyclomatic Complexity: 2
	 */
	@Test
	void testGetRightMostPos1() {
		assertEquals(5, frigate.getRightMostPos(), "Error: The rightmost position should be 5 for NORTH bearing.");
	}

	/**
	 * Test for the getRightMostPos method (EAST bearing).
	 */
	@Test
	void testGetRightMostPos2() {
		frigate = new Frigate(Compass.EAST, new Position(5, 5));
		assertEquals(8, frigate.getRightMostPos(), "Error: The rightmost position should be 8 for EAST bearing.");
	}

	/**
	 * Test for the constructor with invalid input (null).
	 */
	@Test
	void testConstructorInvalidInput() {
		assertThrows(NullPointerException.class, () -> new Frigate(null, new Position(5, 5)),
				"Error: NullPointerException should be thrown for null bearing.");
		assertThrows(NullPointerException.class, () -> new Frigate(Compass.NORTH, null),
				"Error: NullPointerException should be thrown for null position.");
	}
}