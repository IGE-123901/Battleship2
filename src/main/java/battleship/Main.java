/**
 * 
 */
package battleship;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * The type Main.
 *
 * @author britoeabreu
 * @author adrianolopes
 * @author miguelgoulao
 */
public class Main
{
	/**
	 * Main.
	 *
	 * @param args the args
	 */
	public static void main(String[] args)
    {
		DatabaseManager db = null;
		try {
			// Inicializa a Base de Dados
			db = new DatabaseManager();
			System.out.println("*** Battleship com Base de Dados  ***");

			// Exemplo de integração direta (podes também passar o 'db' para o Tasks.menu)
			rodarJogoComLogs(db);

		} catch (SQLException e) {
			System.err.println("Erro crítico na base de dados: " + e.getMessage());
		} finally {
			try {
				if (db != null) db.fechar();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Versão simplificada do menu que integra a gravação na BD
	 */
	private static void rodarJogoComLogs(DatabaseManager db) throws SQLException {
		IFleet myFleet = Fleet.createRandom();
		Game game = new Game(myFleet);
		Scanner in = new Scanner(System.in);

		System.out.println("Comandos disponíveis: 'simula', 'lista', 'desisto'");
		System.out.print("> ");

		while (in.hasNext()) {
			String cmd = in.next();
			if (cmd.equalsIgnoreCase("desisto")) break;

			if (cmd.equalsIgnoreCase("simula")) {
				while (game.getRemainingShips() > 0) {
					// O método randomEnemyFire() já retorna o JSON dos tiros (Passo A.5/A.6)
					String shotsJson = game.randomEnemyFire();

					// Guardamos o JSON na BD
					db.guardarJogada(shotsJson, "Navios restantes: " + game.getRemainingShips());

					game.printMyBoard(true, false);
					System.out.println("Jogada gravada na BD!");
				}
				System.out.println("Jogo terminado!");
			}
			else if (cmd.equalsIgnoreCase("lista")) {
				db.listarJogadas();
			}
			else {
				System.out.println("Comando não reconhecido nesta versão com log.");
			}
			System.out.print("> ");
		}
		System.out.println("Bons ventos!");
	}
}