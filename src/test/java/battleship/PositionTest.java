package battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Position.
 * Author: manue
 * Date: 2026-04-28
 * Time: 12:00
 * Cyclomatic Complexity:
 * - randomPosition: 1
 * - constructor: 1
 * - constructorClassic: 1
 * - getRow: 1
 * - getColumn: 1
 * - getClassicRow: 1
 * - getClassicColumn: 1
 * - isInside: 4
 * - isAdjacentTo: 4
 * - adjacentPositions: 2
 * - isOccupied: 1
 * - isHit: 1
 * - occupy: 1
 * - shoot: 1
 * - equals: 3
 * - hashCode: 1
 * - toString: 1
 */
public class PositionTest {

	private Position position;

	@BeforeEach
	void setUp() {
		position = new Position(2, 3);
	}

	@AfterEach
	void tearDown() {
		position = null;
	}

	/**
	 * Test for the randomPosition method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testRandomPosition() {
		Position randomPos = Position.randomPosition();
		assertNotNull(randomPos, "Error: randomPosition should return a non-null position.");
		assertTrue(randomPos.isInside(), "Error: Random position should be inside the board.");
	}

	/**
	 * Test for the constructor (int, int).
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testConstructor() {
		Position pos = new Position(1, 1);
		assertNotNull(pos, "Error: Position instance should not be null.");
		assertEquals(1, pos.getRow(), "Error: Row should be 1.");
		assertEquals(1, pos.getColumn(), "Error: Column should be 1.");
		assertFalse(pos.isOccupied(), "Error: New position should not be occupied.");
		assertFalse(pos.isHit(), "Error: New position should not be hit.");
	}

	/**
	 * Test for the constructor (char, int).
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testConstructorClassic() {
		Position pos = new Position('A', 1);
		assertNotNull(pos, "Error: Position instance should not be null.");
		assertEquals(0, pos.getRow(), "Error: Row should be 0 for 'A'.");
		assertEquals(0, pos.getColumn(), "Error: Column should be 0 for 1.");
		assertFalse(pos.isOccupied(), "Error: New position should not be occupied.");
		assertFalse(pos.isHit(), "Error: New position should not be hit.");
	}

	/**
	 * Test for the getRow method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testGetRow() {
		assertEquals(2, position.getRow(), "Error: Row should be 2.");
	}

	/**
	 * Test for the getColumn method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testGetColumn() {
		assertEquals(3, position.getColumn(), "Error: Column should be 3.");
	}

	/**
	 * Test for the getClassicRow method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testGetClassicRow() {
		assertEquals('C', position.getClassicRow(), "Error: Classic row should be 'C'.");
	}

	/**
	 * Test for the getClassicColumn method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testGetClassicColumn() {
		assertEquals(4, position.getClassicColumn(), "Error: Classic column should be 4.");
	}

	/**
	 * Test for the isInside method (valid position).
	 * Cyclomatic Complexity: 4
	 */
	@Test
	void testIsInside1() {
		position = new Position(0, 0);
		assertTrue(position.isInside(), "Error: Position (0,0) should be inside.");
	}

	/**
	 * Test for the isInside method (negative row).
	 */
	@Test
	void testIsInside2() {
		position = new Position(-1, 5);
		assertFalse(position.isInside(), "Error: Position with negative row should not be inside.");
	}

	/**
	 * Test for the isInside method (negative column).
	 */
	@Test
	void testIsInside3() {
		position = new Position(5, -1);
		assertFalse(position.isInside(), "Error: Position with negative column should not be inside.");
	}

	/**
	 * Test for the isInside method (row >= BOARD_SIZE).
	 */
	@Test
	void testIsInside4() {
		position = new Position(Game.BOARD_SIZE, 5);
		assertFalse(position.isInside(), "Error: Position with row >= BOARD_SIZE should not be inside.");
	}

	/**
	 * Test for the isInside method (column >= BOARD_SIZE).
	 */
	@Test
	void testIsInside5() {
		position = new Position(5, Game.BOARD_SIZE);
		assertFalse(position.isInside(), "Error: Position with column >= BOARD_SIZE should not be inside.");
	}

	/**
	 * Test for the isAdjacentTo method (horizontally adjacent).
	 * Cyclomatic Complexity: 4
	 */
	@Test
	void testIsAdjacentTo1() {
		Position other = new Position(2, 4);
		assertTrue(position.isAdjacentTo(other), "Error: Horizontally adjacent positions should be adjacent.");
	}

	/**
	 * Test for the isAdjacentTo method (vertically adjacent).
	 */
	@Test
	void testIsAdjacentTo2() {
		Position other = new Position(3, 3);
		assertTrue(position.isAdjacentTo(other), "Error: Vertically adjacent positions should be adjacent.");
	}

	/**
	 * Test for the isAdjacentTo method (diagonally adjacent).
	 */
	@Test
	void testIsAdjacentTo3() {
		Position other = new Position(3, 4);
		assertTrue(position.isAdjacentTo(other), "Error: Diagonally adjacent positions should be adjacent.");
	}

	/**
	 * Test for the isAdjacentTo method (not adjacent).
	 */
	@Test
	void testIsAdjacentTo4() {
		Position other = new Position(4, 5);
		assertFalse(position.isAdjacentTo(other), "Error: Non-adjacent positions should not be adjacent.");
	}

	/**
	 * Test for the isAdjacentTo method (null input).
	 */
	@Test
	void testIsAdjacentToNull() {
		assertThrows(NullPointerException.class, () -> position.isAdjacentTo(null), "Error: isAdjacentTo should throw NullPointerException for null input.");
	}

	/**
	 * Test for the adjacentPositions method (with adjacents).
	 * Cyclomatic Complexity: 2
	 */
	@Test
	void testAdjacentPositions1() {
		List<IPosition> adjacents = position.adjacentPositions();
		assertNotNull(adjacents, "Error: Adjacent positions list should not be null.");
		assertFalse(adjacents.isEmpty(), "Error: There should be adjacent positions.");
	}

	/**
	 * Test for the adjacentPositions method (corner position).
	 */
	@Test
	void testAdjacentPositions2() {
		position = new Position(0, 0);
		List<IPosition> adjacents = position.adjacentPositions();
		assertNotNull(adjacents, "Error: Adjacent positions list should not be null.");
		// Corner has 3 adjacents
		assertEquals(3, adjacents.size(), "Error: Corner position should have 3 adjacent positions.");
	}

	/**
	 * Test for the isOccupied method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testIsOccupied() {
		assertFalse(position.isOccupied(), "Error: New position should not be occupied.");
		position.occupy();
		assertTrue(position.isOccupied(), "Error: Position should be occupied after occupy().");
	}

	/**
	 * Test for the isHit method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testIsHit() {
		assertFalse(position.isHit(), "Error: New position should not be hit.");
		position.shoot();
		assertTrue(position.isHit(), "Error: Position should be hit after shoot().");
	}

	/**
	 * Test for the occupy method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testOccupy() {
		position.occupy();
		assertTrue(position.isOccupied(), "Error: Position should be occupied after occupy().");
	}

	/**
	 * Test for the shoot method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testShoot() {
		position.shoot();
		assertTrue(position.isHit(), "Error: Position should be hit after shoot().");
	}

	/**
	 * Test for the equals method (equal positions).
	 * Cyclomatic Complexity: 3
	 */
	@Test
	void testEquals1() {
		Position same = new Position(2, 3);
		assertTrue(position.equals(same), "Error: Equal positions should be equal.");
	}

	/**
	 * Test for the equals method (null).
	 */
	@Test
	void testEquals2() {
		assertFalse(position.equals(null), "Error: Position should not equal null.");
	}

	/**
	 * Test for the equals method (different object).
	 */
	@Test
	void testEquals3() {
		Object other = new Object();
		assertFalse(position.equals(other), "Error: Position should not equal non-Position object.");
	}

	/**
	 * Test for the equals method (different position).
	 */
	@Test
	void testEquals4() {
		Position other = new Position(2, 4);
		assertFalse(position.equals(other), "Error: Different positions should not be equal.");
	}

	/**
	 * Test for the equals method (self).
	 */
	@Test
	void testEquals5() {
		assertTrue(position.equals(position), "Error: Position should equal itself.");
	}

	/**
	 * Test for the hashCode method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testHashCode() {
		Position same = new Position(2, 3);
		assertEquals(position.hashCode(), same.hashCode(), "Error: Hash codes should be equal for equal positions.");
	}

	/**
	 * Test for the toString method.
	 * Cyclomatic Complexity: 1
	 */
	@Test
	void testToString() {
		String expected = "C4";
		assertEquals(expected, position.toString(), "Error: String representation should be 'C4'.");
	}
}