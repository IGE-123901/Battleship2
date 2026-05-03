package battleship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.*;

/**
 * Shot
 *
 * @author Your Name
 * Date: 20/02/2026
 * Time: 19:39
 */
public class Move implements IMove {

	// --- REFABRICAÇÃO: Extract Constant para as strings duplicadas ---
	private static final String STR_TIRO = " tiro";
	private static final String STR_PLURAL = "s";
	private static final String STR_REPETIDO = " repetido";

	//-------------------------------------------------------------------
	private final int number;
	private final List<IPosition> shots;
	private final List<IGame.ShotResult> shotResults;

	//-------------------------------------------------------------------
	public Move(int moveNumber, List<IPosition> moveShots, List<IGame.ShotResult> moveResults) {
		this.number = moveNumber;
		this.shots = moveShots;
		this.shotResults = moveResults;
	}

	@Override
	public String toString() {
		return "Move{" +
				"number=" + number +
				", shots=" + shots.size() +
				", results=" + shotResults.size() +
				'}';
	}

	@Override
	public int getNumber() {
		return this.number;
	}

	@Override
	public List<IPosition> getShots() {
		return this.shots;
	}

	@Override
	public List<IGame.ShotResult> getShotResults() {
		return this.shotResults;
	}

	/**
	 * Processes the results of enemy fire on the game board, analyzing the outcomes of shots,
	 * such as valid shots, repeated shots, missed shots, hits on ships, and sunk ships. It can
	 * also display a detailed summary of the shot results if verbose mode is activated.
	 *
	 * @param verbose a boolean indicating whether a detailed summary should be printed to the console
	 *                for the processed enemy fire data.
	 * @return a JSON-formatted string that encapsulates the results, including counts of valid shots,
	 *         repeated shots, missed shots, shots outside the game board, and details of hits and
	 *         sunk ships.
	 */
	/**
	 * REFABRICAÇÃO: O método processEnemyFire foi reduzido (Extract Method).
	 * Agora atua apenas como o maestro que chama os métodos mais pequenos.
	 */
	@Override
	public String processEnemyFire(boolean verbose) {

		int validShots = 0;
		int repeatedShots = 0;
		int missedShots = 0;

		Map<String, Integer> sunkBoatsCount = new HashMap<>();
		Map<String, Integer> hitsPerBoat = new HashMap<>();

		for (IGame.ShotResult result : this.shotResults) {
			if (!result.valid()) {
				continue;
			}

			if (result.repeated())
				repeatedShots++;
			else {
				validShots++;
				if (result.ship() == null)
					missedShots++;
				else{
					String boatName = result.ship().getCategory();
					hitsPerBoat.put(boatName, hitsPerBoat.getOrDefault(boatName, 0) + 1);
					if (result.sunk())
						sunkBoatsCount.put(boatName, sunkBoatsCount.getOrDefault(boatName, 0) + 1);
				}
			}
		}

		int outsideShots = Game.NUMBER_SHOTS - validShots - repeatedShots;

		if (verbose) {
			printVerboseSummary(validShots, repeatedShots, missedShots, outsideShots, sunkBoatsCount, hitsPerBoat);
		}

		Map<String, Object> response = buildJsonResponseMap(validShots, outsideShots, repeatedShots, missedShots, sunkBoatsCount, hitsPerBoat);

		return serializeToJson(response);
	}

	// --- MÉTODOS EXTRAÍDOS (EXTRACT METHOD) ---

	// O método principal fica agora super limpo e apenas delega tarefas
	private void printVerboseSummary(int validShots, int repeatedShots, int missedShots, int outsideShots,
									 Map<String, Integer> sunkBoatsCount, Map<String, Integer> hitsPerBoat) {
		StringBuilder output = new StringBuilder();

		if (validShots == 0 && repeatedShots > 0) {
			appendRepeatedShotsOnly(output, repeatedShots);
		} else {
			appendMixedShotsSummary(output, validShots, repeatedShots, missedShots, sunkBoatsCount, hitsPerBoat);
		}

		appendOutsideShots(output, outsideShots);

		System.out.println("Jogada nº" + this.number + " -> " + output);
	}

	// --- DECOMPOSE CONDITIONAL / EXTRACT METHOD ---

	private void appendRepeatedShotsOnly(StringBuilder output, int repeatedShots) {
		output.append(repeatedShots).append(STR_TIRO)
				.append(repeatedShots > 1 ? STR_PLURAL : "")
				.append(STR_REPETIDO)
				.append(repeatedShots > 1 ? STR_PLURAL : "");
	}

	private void appendMixedShotsSummary(StringBuilder output, int validShots, int repeatedShots, int missedShots,
										 Map<String, Integer> sunkBoatsCount, Map<String, Integer> hitsPerBoat) {
		if (validShots > 0) {
			output.append(validShots).append(STR_TIRO)
					.append(validShots > 1 ? STR_PLURAL : "")
					.append(" válido")
					.append(validShots > 1 ? STR_PLURAL : "").append(": ");
		}

		appendSunkBoats(output, sunkBoatsCount);
		appendHitsPerBoat(output, hitsPerBoat, sunkBoatsCount);
		appendMissedShots(output, missedShots, sunkBoatsCount, hitsPerBoat);
		appendRepeatedShotsSuffix(output, repeatedShots, validShots);
	}

	private void appendMissedShots(StringBuilder output, int missedShots, Map<String, Integer> sunkBoatsCount, Map<String, Integer> hitsPerBoat) {
		if (missedShots > 0) {
			output.append(missedShots).append(STR_TIRO)
					.append(missedShots > 1 ? STR_PLURAL : "").append(" na água");
		} else if (!sunkBoatsCount.isEmpty() || !hitsPerBoat.isEmpty()) {
			output.setLength(output.length() - 2);
		}
	}

	private void appendRepeatedShotsSuffix(StringBuilder output, int repeatedShots, int validShots) {
		if (repeatedShots > 0) {
			if (validShots > 0) {
				output.append(", ");
			}
			output.append(repeatedShots).append(STR_TIRO)
					.append(repeatedShots > 1 ? STR_PLURAL : "")
					.append(STR_REPETIDO)
					.append(repeatedShots > 1 ? STR_PLURAL : "");
		}
	}

	private void appendOutsideShots(StringBuilder output, int outsideShots) {
		if (outsideShots > 0) {
			if (!output.isEmpty()) {
				output.append(", ");
			}
			output.append(outsideShots).append(STR_TIRO)
					.append(outsideShots > 1 ? STR_PLURAL : "")
					.append(" exterior")
					.append(outsideShots > 1 ? "es" : "");
		}
	}

	// (Manténs aqui os métodos appendSunkBoats e appendHitsPerBoat que criámos na mensagem anterior)

	// --- MÉTODOS EXTRAÍDOS (EXTRACT METHOD) PARA REDUZIR A COMPLEXIDADE ---

	private void appendSunkBoats(StringBuilder output, Map<String, Integer> sunkBoatsCount) {
		if (!sunkBoatsCount.isEmpty()) {
			for (Map.Entry<String, Integer> entry : sunkBoatsCount.entrySet()) {
				String boatName = entry.getKey();
				int count = entry.getValue();
				output.append(count).append(" ").append(boatName).append(count > 1 ? STR_PLURAL : "").append(" ao fundo").append(" + ");
			}
		}
	}

	private void appendHitsPerBoat(StringBuilder output, Map<String, Integer> hitsPerBoat, Map<String, Integer> sunkBoatsCount) {
		if (!hitsPerBoat.isEmpty()) {
			for (Map.Entry<String, Integer> entry : hitsPerBoat.entrySet()) {
				String boatName = entry.getKey();
				int hits = entry.getValue();
				if (!sunkBoatsCount.containsKey(boatName)) {
					output.append(hits).append(STR_TIRO).append(hits > 1 ? STR_PLURAL : "").append(" num(a) ").append(boatName).append(" + ");
				}
			}
		}
	}

	private Map<String, Object> buildJsonResponseMap(int validShots, int outsideShots, int repeatedShots, int missedShots,
													 Map<String, Integer> sunkBoatsCount, Map<String, Integer> hitsPerBoat) {
		Map<String, Object> response = new HashMap<>();
		response.put("validShots", validShots);
		response.put("outsideShots", outsideShots);
		response.put("repeatedShots", repeatedShots);
		response.put("missedShots", missedShots);

		List<Map<String, Object>> sunkBoats = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : sunkBoatsCount.entrySet()) {
			Map<String, Object> boat = new HashMap<>();
			boat.put("type", entry.getKey());
			boat.put("count", entry.getValue());
			sunkBoats.add(boat);
		}
		response.put("sunkBoats", sunkBoats);

		List<Map<String, Object>> boatHits = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : hitsPerBoat.entrySet()) {
			if (!sunkBoatsCount.containsKey(entry.getKey())) {
				Map<String, Object> boat = new HashMap<>();
				boat.put("type", entry.getKey());
				boat.put("hits", entry.getValue());
				boatHits.add(boat);
			}
		}
		response.put("hitsOnBoats", boatHits);

		return response;
	}

	private String serializeToJson(Map<String, Object> response) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		try {
			return objectMapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Erro ao serializar o JSON dos resultados da jogada", e);
		}
	}
}