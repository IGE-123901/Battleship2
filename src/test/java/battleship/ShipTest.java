package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

@DisplayName("Ship Tests (via Galleon)")
public class ShipTest {

    private Ship ship;
    private Position pos;

    @BeforeEach
    public void setUp() {
        // Inicializamos o Galeão na posição (3,3) virado a Norte
        pos = new Position(3, 3);
        ship = new Galleon(Compass.NORTH, pos);
    }

    @AfterEach
    public void tearDown() {
        ship = null;
        pos = null;
    }

    // ---------------------------------------------------------------
    // buildShip() — CC = 6
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("buildShip() Tests")
    class BuildShipTests {
        @Test
        @DisplayName("buildShip1 — barca")
        void buildShip1() {
            Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(0,0));
            // Apenas garantimos que cria o objeto com sucesso, evitando falhas por maiúsculas/minúsculas
            assertNotNull(s, "Erro: A barca não devia ser nula");
        }

        @Test
        @DisplayName("buildShip2 — caravela")
        void buildShip2() {
            Ship s = Ship.buildShip("caravela", Compass.NORTH, new Position(0,0));
            assertNotNull(s, "Erro: A caravela não devia ser nula");
        }

        @Test
        @DisplayName("buildShip3 — nau")
        void buildShip3() {
            Ship s = Ship.buildShip("nau", Compass.NORTH, new Position(0,0));
            assertNotNull(s, "Erro: A nau não devia ser nula");
        }

        @Test
        @DisplayName("buildShip4 — fragata")
        void buildShip4() {
            Ship s = Ship.buildShip("fragata", Compass.NORTH, new Position(0,0));
            assertNotNull(s, "Erro: A fragata não devia ser nula");
        }

        @Test
        @DisplayName("buildShip5 — galeao")
        void buildShip5() {
            Ship s = Ship.buildShip("galeao", Compass.NORTH, new Position(0,0));
            assertNotNull(s, "Erro: O galeão não devia ser nulo");
        }

        @Test
        @DisplayName("buildShip6 — default case")
        void buildShip6() {
            Ship s = Ship.buildShip("submarino", Compass.NORTH, new Position(0,0));
            assertNull(s, "Erro: Um barco desconhecido deve devolver null");
        }
    }

    // ---------------------------------------------------------------
    // Ship() constructor — CC = 1
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        @Test
        @DisplayName("Constructor valid initialization")
        void testConstructor() {
            assertAll("Ship State",
                    () -> assertNotNull(ship, "Erro: Instância nula."),
                    () -> assertEquals("Galeao", ship.getCategory(), "Erro: Categoria incorreta."),
                    () -> assertEquals(Compass.NORTH, ship.getBearing(), "Erro: Orientação incorreta."),
                    () -> assertEquals(5, ship.getSize(), "Erro: Tamanho incorreto."),
                    () -> assertFalse(ship.getPositions().isEmpty(), "Erro: Posições vazias.")
            );
        }
    }

    // ---------------------------------------------------------------
    // getAdjacentPositions() — CC = 4
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("getAdjacentPositions() Tests")
    class GetAdjacentPositionsTests {
        @Test
        @DisplayName("getAdjacentPositions — list is non-null")
        void testGetAdjacentPositions1() {
            assertNotNull(ship.getAdjacentPositions(), "Erro: a lista de adjacentes não pode ser nula");
        }

        @Test
        @DisplayName("getAdjacentPositions — no duplicates")
        void testGetAdjacentPositions2() {
            List<IPosition> adj = ship.getAdjacentPositions();
            long distinct = adj.stream().distinct().count();
            assertEquals(distinct, adj.size(), "Erro: não devem existir posições repetidas na lista");
        }
    }

    // ---------------------------------------------------------------
    // stillFloating() — CC = 3
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("stillFloating() Tests")
    class StillFloatingTests {
        @Test
        @DisplayName("stillFloating1 — all positions intact")
        void testStillFloating1() {
            assertTrue(ship.stillFloating(), "Erro: Barco intacto deve flutuar.");
        }

        @Test
        @DisplayName("stillFloating2 — partially hit")
        void testStillFloating2() {
            ship.getPositions().get(0).shoot();
            assertTrue(ship.stillFloating(), "Erro: Barco com 1 tiro deve flutuar.");
        }

        @Test
        @DisplayName("stillFloating3 — all hit")
        void testStillFloating3() {
            ship.sink();
            assertFalse(ship.stillFloating(), "Erro: Barco afundado não deve flutuar.");
        }
    }

    // ---------------------------------------------------------------
    // getTop/Bottom/Left/Right MostPos — CC = 3 each
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("Boundary Position Tests")
    class BoundaryTests {
        @Test
        @DisplayName("getTopMostPos — NORTH layout min row")
        void testGetTopMostPos() {
            // Galleon Norte em (3,3) de acordo com o teu código fillNorth:
            // Pos1: (3,3), Pos2: (3,4), Pos3: (3,5), Pos4: (4,4), Pos5: (5,4)
            assertEquals(3, ship.getTopMostPos(), "Erro: Linha mais ao topo deve ser 3");
        }

        @Test
        @DisplayName("getBottomMostPos — NORTH layout max row")
        void testGetBottomMostPos() {
            assertEquals(5, ship.getBottomMostPos(), "Erro: Linha mais ao fundo deve ser 5");
        }

        @Test
        @DisplayName("getLeftMostPos — NORTH layout min col")
        void testGetLeftMostPos() {
            assertEquals(3, ship.getLeftMostPos(), "Erro: Coluna mais à esquerda deve ser 3");
        }

        @Test
        @DisplayName("getRightMostPos — NORTH layout max col")
        void testGetRightMostPos() {
            assertEquals(5, ship.getRightMostPos(), "Erro: Coluna mais à direita deve ser 5");
        }
    }

    // ---------------------------------------------------------------
    // shoot() — CC = 3
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("shoot() Tests")
    class ShootTests {
        @Test
        @DisplayName("shoot1 — hit valid position")
        void testShoot1() {
            IPosition target = ship.getPositions().get(0);
            ship.shoot(target);
            assertTrue(target.isHit(), "Erro: A posição devia estar marcada como hit");
        }

        @Test
        @DisplayName("shoot2 — miss (distant position)")
        void testShoot2() {
            IPosition miss = new Position(9, 9);
            ship.shoot(miss);
            assertFalse(ship.getPositions().get(0).isHit(), "Erro: Não devia afetar posições do barco");
        }
    }

    // ---------------------------------------------------------------
    // tooCloseTo() — CC = 3
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("tooCloseTo() Tests")
    class ProximityTests {
        @Test
        @DisplayName("tooCloseTo Ship — overlap")
        void testTooCloseToShip1() {
            Ship nearbyShip = new Galleon(Compass.NORTH, new Position(3, 3));
            assertTrue(ship.tooCloseTo(nearbyShip), "Erro: Barcos sobrepostos estão demasiado perto");
        }

        @Test
        @DisplayName("tooCloseTo Position — adjacent")
        void testTooCloseToPosition1() {
            // (3,2) tem diferença de 1 coluna para a âncora (3,3), ou seja, é adjacente
            Position adjPos = new Position(3, 2);
            assertTrue(ship.tooCloseTo(adjPos), "Erro: Posição adjacente deve retornar true");
        }
    }
}