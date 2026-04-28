package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Scanner;

/**
 * Test class for interface IMove.
 * Author: ${Dinis Silva}
 * Date: 2026-04-28 17:39
 * Cyclomatic Complexity:
 * - readMove(): 2
 */
public class IMoveTest {

    @BeforeEach
    public void setUp() {
        // Não é necessária instância para testar um método estático de interface
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void readMove1() {
        // Caminho 1 (CC=1): numShots = 0 (o ciclo for não executa)
        Scanner sc = new Scanner("0");
        Move result = IMove.readMove(1, sc);

        assertAll(
                () -> assertNotNull(result, "Error: expected a non-null Move object"),
                () -> assertEquals(1, result.getNumber(), "Error: expected move number to be 1"),
                () -> assertTrue(result.getShots().isEmpty(), "Error: expected shots list to be empty")
        );
    }

    @Test
    public void readMove2() {
        // Caminho 2 (CC=2): numShots > 0 (o ciclo for executa)
        Scanner sc = new Scanner("2 0 0 1 1");
        Move result = IMove.readMove(2, sc);

        assertAll(
                () -> assertNotNull(result, "Error: expected a non-null Move object"),
                () -> assertEquals(2, result.getNumber(), "Error: expected move number to be 2"),
                () -> assertEquals(2, result.getShots().size(), "Error: expected 2 shots in the list"),
                () -> assertEquals(0, result.getShots().getFirst().getRow(), "Error: expected row of first shot to be 0")
        );
    }
}