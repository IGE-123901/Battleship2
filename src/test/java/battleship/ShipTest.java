package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for class Ship.
 * Author: ${user.name}
 * Date: 2026-04-28
 * Cyclomatic Complexity:
 * - constructor(): 1
 * - buildShip(): 6
 * - getCategory(): 1
 * - getPositions(): 1
 * - getPosition(): 1
 * - getBearing(): 1
 * - getSize(): 1
 * - toString(): 1
 * - stillFloating(): 3
 * - occupies(): 3
 * - getTopMostPos(): 3
 * - getBottomMostPos(): 3
 * - getLeftMostPos(): 3
 * - getRightMostPos(): 3
 * - tooCloseTo(IShip): 3
 * - tooCloseTo(IPosition): 3
 * - shoot(): 3
 * - sink(): 2
 * - getAdjacentPositions(): 5
 */
public class ShipTest {

    private Ship testShip;
    private Position startPos;

    private static class ConcreteShip extends Ship {
        public ConcreteShip(String category, Compass bearing, IPosition pos, int size) {
            super(category, bearing, pos, size);
            for (int i = 0; i < size; i++) {
                // Simula navio na horizontal (colunas a somar) ou vertical
                this.positions.add(new Position(pos.getRow() + i, pos.getColumn() + i));
            }
        }
    }

    @BeforeEach
    public void setUp() {
        this.startPos = new Position(2, 2);
        this.testShip = new ConcreteShip("Fragata", Compass.NORTH, startPos, 3);
    }

    @AfterEach
    public void tearDown() {
        this.testShip = null;
        this.startPos = null;
    }

    // ================= GETTERS E CONSTRUTOR (CC = 1) =================

    @Test
    public void constructor1() {
        assertNotNull(testShip, "Error: expected a non-null Ship instance");
    }

    @Test
    public void getCategory1() {
        assertEquals("Fragata", testShip.getCategory(), "Error: wrong category");
    }

    @Test
    public void getPositions1() {
        assertEquals(3, testShip.getPositions().size(), "Error: wrong positions size");
    }

    @Test
    public void getPosition1() {
        assertEquals(startPos, testShip.getPosition(), "Error: wrong starting position");
    }

    @Test
    public void getBearing1() {
        assertEquals(Compass.NORTH, testShip.getBearing(), "Error: wrong bearing");
    }

    @Test
    public void getSize1() {
        assertEquals(3, testShip.getSize(), "Error: wrong size");
    }

    @Test
    public void toString1() {
        String expected = "[Fragata NORTH " + startPos.toString() + "]";
        assertEquals(expected, testShip.toString(), "Error: wrong toString output");
    }

    // ================= MÉTODOS ORIGINAIS (CC = 3 E CC = 6) =================

    @Test
    public void buildShip1() {
        Ship s = Ship.buildShip("barca", Compass.NORTH, startPos);
        assertInstanceOf(Barge.class, s, "Error: expected a Barge instance");
    }

    @Test
    public void buildShip2() {
        Ship s = Ship.buildShip("caravela", Compass.NORTH, startPos);
        assertInstanceOf(Caravel.class, s, "Error: expected a Caravel instance");
    }

    @Test
    public void buildShip3() {
        Ship s = Ship.buildShip("nau", Compass.NORTH, startPos);
        assertInstanceOf(Carrack.class, s, "Error: expected a Carrack instance");
    }

    @Test
    public void buildShip4() {
        Ship s = Ship.buildShip("fragata", Compass.NORTH, startPos);
        assertInstanceOf(Frigate.class, s, "Error: expected a Frigate instance");
    }

    @Test
    public void buildShip5() {
        Ship s = Ship.buildShip("galeao", Compass.NORTH, startPos);
        assertInstanceOf(Galleon.class, s, "Error: expected a Galleon instance");
    }

    @Test
    public void buildShip6() {
        Ship s = Ship.buildShip("submarino_invalido", Compass.NORTH, startPos);
        assertNull(s, "Error: expected null");
    }

    @Test
    public void stillFloating1() {
        assertTrue(testShip.stillFloating(), "Error: ship should be floating");
    }

    @Test
    public void stillFloating2() {
        testShip.sink();
        assertFalse(testShip.stillFloating(), "Error: ship should not be floating");
    }

    @Test
    public void stillFloating3() {
        Ship empty = new ConcreteShip("E", Compass.NORTH, startPos, 0);
        assertFalse(empty.stillFloating(), "Error: empty ship is not floating");
    }

    @Test
    public void occupies1() {
        assertTrue(testShip.occupies(testShip.getPositions().get(0)), "Error: should occupy");
    }

    @Test
    public void occupies2() {
        assertFalse(testShip.occupies(new Position(9, 9)), "Error: should not occupy");
    }

    @Test
    public void occupies3() {
        Ship empty = new ConcreteShip("E", Compass.NORTH, startPos, 0);
        assertFalse(empty.occupies(startPos), "Error: empty ship occupies nothing");
    }

    // ================= BOUNDARY GETTERS (CC = 3 cada) =================
    // Testam o if, o salto do if, e o bypass do ciclo (tamanho 0 ou 1)

    @Test
    public void getTopMostPos1() {
        // Original: (2,2), (3,3), (4,4). Não entra no IF porque 3 < 2 é falso.
        assertEquals(2, testShip.getTopMostPos(), "Error: top most should be 2");
    }

    @Test
    public void getTopMostPos2() {
        // Substitui a posição no índice 1 por uma nova Posição com row = 1. Entra no IF.
        testShip.getPositions().set(1, new Position(1, 3));
        assertEquals(1, testShip.getTopMostPos(), "Error: top most should be 1");
    }

    @Test
    public void getTopMostPos3() {
        // Tamanho 1 não executa o ciclo for.
        Ship small = new ConcreteShip("S", Compass.NORTH, startPos, 1);
        assertEquals(2, small.getTopMostPos(), "Error: bypass loop");
    }

    @Test
    public void getBottomMostPos1() {
        // Original: (2,2), (3,3), (4,4). Entra no IF porque 3 > 2 é verdadeiro.
        assertEquals(4, testShip.getBottomMostPos(), "Error: bottom most should be 4");
    }

    @Test
    public void getBottomMostPos2() {
        // Substitui as posições para que a linha seja sempre menor que 2. NÃO entra no IF.
        testShip.getPositions().set(1, new Position(1, 3));
        testShip.getPositions().set(2, new Position(0, 4));
        assertEquals(2, testShip.getBottomMostPos(), "Error: bottom most should be 2");
    }

    @Test
    public void getBottomMostPos3() {
        Ship small = new ConcreteShip("S", Compass.NORTH, startPos, 1);
        assertEquals(2, small.getBottomMostPos(), "Error: bypass loop");
    }

    @Test
    public void getLeftMostPos1() {
        // Original: (2,2), (3,3), (4,4). Não entra no IF porque 3 < 2 é falso.
        assertEquals(2, testShip.getLeftMostPos(), "Error: left most should be 2");
    }

    @Test
    public void getLeftMostPos2() {
        // Substitui a posição no índice 1 por uma com coluna = 1. Entra no IF.
        testShip.getPositions().set(1, new Position(3, 1));
        assertEquals(1, testShip.getLeftMostPos(), "Error: left most should be 1");
    }

    @Test
    public void getLeftMostPos3() {
        Ship small = new ConcreteShip("S", Compass.NORTH, startPos, 1);
        assertEquals(2, small.getLeftMostPos(), "Error: bypass loop");
    }

    @Test
    public void getRightMostPos1() {
        // Original: (2,2), (3,3), (4,4). Entra no IF porque 3 > 2 é verdadeiro.
        assertEquals(4, testShip.getRightMostPos(), "Error: right most should be 4");
    }

    @Test
    public void getRightMostPos2() {
        // Substitui para colunas menores que 2. NÃO entra no IF.
        testShip.getPositions().set(1, new Position(3, 1));
        testShip.getPositions().set(2, new Position(4, 0));
        assertEquals(2, testShip.getRightMostPos(), "Error: right most should be 2");
    }

    @Test
    public void getRightMostPos3() {
        Ship small = new ConcreteShip("S", Compass.NORTH, startPos, 1);
        assertEquals(2, small.getRightMostPos(), "Error: bypass loop");
    }

    // ================= SHOOT E SINK (CC = 3 e CC = 2) =================

    @Test
    public void shoot1() {
        testShip.shoot(testShip.getPositions().get(0)); // Atinge
        assertTrue(testShip.getPositions().get(0).isHit(), "Error: should be hit");
    }

    @Test
    public void shoot2() {
        testShip.shoot(new Position(9, 9)); // Falha
        assertFalse(testShip.getPositions().get(0).isHit(), "Error: should not be hit");
    }

    @Test
    public void shoot3() {
        Ship empty = new ConcreteShip("E", Compass.NORTH, startPos, 0);
        empty.shoot(startPos); // Bypass loop
        assertEquals(0, empty.getPositions().size(), "Error: empty bypass");
    }

    @Test
    public void sink1() {
        testShip.sink();
        assertFalse(testShip.stillFloating(), "Error: should be sunk");
    }

    @Test
    public void sink2() {
        Ship empty = new ConcreteShip("E", Compass.NORTH, startPos, 0);
        empty.sink(); // Bypass loop
        assertFalse(empty.stillFloating(), "Error: bypass loop");
    }

    // ================= ADJACÊNCIAS E TOO CLOSE TO =================

    @Test
    public void tooCloseToPos1() {
        IPosition adj = new Position(1, 2); // Adjacente a (2,2)
        assertTrue(testShip.tooCloseTo(adj), "Error: should be too close");
    }

    @Test
    public void tooCloseToPos2() {
        IPosition far = new Position(9, 9);
        assertFalse(testShip.tooCloseTo(far), "Error: should not be too close");
    }

    @Test
    public void tooCloseToPos3() {
        Ship empty = new ConcreteShip("E", Compass.NORTH, startPos, 0);
        assertFalse(empty.tooCloseTo(startPos), "Error: loop bypass");
    }

    @Test
    public void tooCloseToShip1() {
        Ship other = new ConcreteShip("Other", Compass.NORTH, new Position(1, 2), 1);
        assertTrue(testShip.tooCloseTo(other), "Error: ships should be too close");
    }

    @Test
    public void tooCloseToShip2() {
        Ship other = new ConcreteShip("Other", Compass.NORTH, new Position(9, 9), 1);
        assertFalse(testShip.tooCloseTo(other), "Error: ships are far");
    }

    @Test
    public void tooCloseToShip3() {
        Ship other = new ConcreteShip("Other", Compass.NORTH, new Position(9, 9), 0); // ship vazio
        assertFalse(testShip.tooCloseTo(other), "Error: loop bypass");
    }

    @Test
    public void getAdjacentPositions1() {
        List<IPosition> adj = testShip.getAdjacentPositions();
        assertFalse(adj.isEmpty(), "Error: should have adjacencies");
        // Valida se não contém posições do próprio navio
        assertFalse(adj.contains(testShip.getPositions().get(0)), "Error: adj cannot contain ship itself");
    }

    @Test
    public void getAdjacentPositions2() {
        Ship empty = new ConcreteShip("E", Compass.NORTH, startPos, 0);
        assertTrue(empty.getAdjacentPositions().isEmpty(), "Error: empty ship has no adjacencies");
    }
}