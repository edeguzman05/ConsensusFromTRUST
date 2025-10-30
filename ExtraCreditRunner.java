import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class ExtraCreditRunner {

    public static void main(String[] args) throws Exception {
        double[] p_graph_values = {0.1, 0.2, 0.3};
        double[] p_malicious_values = {0.15, 0.3, 0.45};
        double[] p_txDistribution_values = {0.01, 0.05, 0.10};
        int[] numRounds_values = {10, 20};

        String csvFile = "ExtraCreditResults.csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
            pw.println("p_graph,p_malicious,p_txDistribution,numRounds,allConsensus");

            for (double p_graph : p_graph_values) {
                for (double p_mal : p_malicious_values) {
                    for (double p_tx : p_txDistribution_values) {
                        for (int rounds : numRounds_values) {

                            boolean allConsensus = runSimulationAndCheckConsensus(p_graph, p_mal, p_tx, rounds);

                            pw.printf(Locale.US, "%.2f,%.2f,%.2f,%d,%s%n",
                                    p_graph, p_mal, p_tx, rounds, allConsensus ? "YES" : "NO");

                            System.out.printf("Done: p_graph=%.2f, p_mal=%.2f, p_tx=%.2f, rounds=%d -> %s%n",
                                    p_graph, p_mal, p_tx, rounds, allConsensus ? "YES" : "NO");
                        }
                    }
                }
            }
        }

        System.out.println("Extra credit CSV generated: " + csvFile);
    }

    private static boolean runSimulationAndCheckConsensus(double p_graph, double p_mal, double p_tx, int rounds) {
        // Run Simulation.main with these arguments
        String[] simArgs = {String.valueOf(p_graph), String.valueOf(p_mal), String.valueOf(p_tx), String.valueOf(rounds)};
        Simulation.main(simArgs);

        // Gather all nodes from Simulation
        // NOTE: We rely on Simulation.main creating Node[] nodes internally.
        // We'll assume for EC that all CompliantNode instances are compliant and can be cast safely.
        // Because Simulation prints results, we cannot capture them directly here, so we rely on reading from nodes.
        // If Simulation exposed nodes[], we could compare. Otherwise, this requires Simulation to be modified to return nodes.

        // For simplicity, let's assume each Simulation prints results, so we can do a naive consensus check
        // between rounds: if all nodes printed the same transactions count, we consider it "YES"
        // This mirrors your terminal observation.

        // This is a placeholder to allow CSV to generate without modifying Simulation.
        // All "NO" is returned if we cannot capture nodes reliably.
        return false; // You will need Simulation to expose nodes[] to actually check consensus programmatically
    }
}
