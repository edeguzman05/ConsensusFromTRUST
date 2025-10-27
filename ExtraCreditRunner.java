import java.io.*;
import java.util.*;

public class ExtraCreditRunner {

    // Parameter options
    private static final double[] P_GRAPH = {0.1, 0.2, 0.3};
    private static final double[] P_MALICIOUS = {0.15, 0.30, 0.45};
    private static final double[] P_TX = {0.01, 0.05, 0.10};
    private static final int[] NUM_ROUNDS = {10, 20};

    public static void main(String[] args) throws IOException, InterruptedException {

        // Output CSV file
        File csvFile = new File("ExtraCreditResults.csv");
        PrintWriter writer = new PrintWriter(csvFile);
        writer.println("MaxMalicious,p_graph,p_txDistribution,numRounds");

        // Loop over all 54 combinations
        for (double p_graph : P_GRAPH) {
            for (double p_tx : P_TX) {
                for (int numRounds : NUM_ROUNDS) {
                    int maxMaliciousTolerated = 0;
                    for (double p_malicious : P_MALICIOUS) {

                        // Run the simulation
                        ProcessBuilder pb = new ProcessBuilder(
                                "java", "Simulation",
                                String.valueOf(p_graph),
                                String.valueOf(p_malicious),
                                String.valueOf(p_tx),
                                String.valueOf(numRounds)
                        );
                        pb.redirectErrorStream(true);
                        Process process = pb.start();

                        // Read simulation output
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        boolean allNodesAgree = true;
                        Map<Integer, Set<Integer>> nodeTx = new HashMap<>();

                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (line.isEmpty()) continue;

                            // Parse lines like "Transaction ids that Node 58 believes consensus on:"
                            if (line.startsWith("Transaction ids that Node")) {
                                String[] parts = line.split(" ");
                                int nodeId = Integer.parseInt(parts[4]);
                                nodeTx.put(nodeId, new HashSet<>());
                                continue;
                            }

                            // Parse numeric transaction IDs only
                            if (line.matches("-?\\d+")) {
                                int txId = Integer.parseInt(line);
                                // Add to last node added
                                int lastNode = Collections.max(nodeTx.keySet());
                                nodeTx.get(lastNode).add(txId);
                            }
                        }

                        process.waitFor();

                        // Check if all nodes agree
                        Set<Integer> consensusTx = null;
                        for (Set<Integer> txs : nodeTx.values()) {
                            if (consensusTx == null) {
                                consensusTx = new HashSet<>(txs);
                            } else if (!consensusTx.equals(txs)) {
                                allNodesAgree = false;
                                break;
                            }
                        }

                        if (allNodesAgree) {
                            maxMaliciousTolerated = (int) (p_malicious * 100);
                        }
                    }

                    // Write result for this combination
                    writer.println(maxMaliciousTolerated + "," + p_graph + "," + p_tx + "," + numRounds);
                    writer.flush();
                }
            }
        }

        writer.close();
        System.out.println("Extra credit CSV generated: ExtraCreditResults.csv");
    }
}
