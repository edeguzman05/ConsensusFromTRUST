import java.util.*;

/* CompliantNode refers to a node that follows the rules (not malicious) */
public class CompliantNode implements Node {

    private boolean[] followees; // which nodes we follow
    private Set<Transaction> pendingTransactions; // transactions we currently hold
    private Map<Integer, Set<Integer>> receivedFrom; // txID -> followees who sent it
    private Set<Integer> blacklistedFollowees; // suspected malicious followees
    private int numRounds; // total rounds in simulation
    private int currentRound; // current round number

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.pendingTransactions = new HashSet<>();
        this.receivedFrom = new HashMap<>();
        this.blacklistedFollowees = new HashSet<>();
        this.numRounds = numRounds;
        this.currentRound = 0;
    }

    @Override
    public void setFollowees(boolean[] followees) {
        this.followees = Arrays.copyOf(followees, followees.length);
    }

    @Override
    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        if (pendingTransactions != null) {
            this.pendingTransactions.addAll(pendingTransactions);
            for (Transaction tx : pendingTransactions) {
                receivedFrom.put(tx.id, new HashSet<>()); // track which followees sent each tx
            }
        }
    }

    @Override
    public Set<Transaction> getProposals() {
        currentRound++;

        // On the last round, freeze consensus using threshold
        if (currentRound == numRounds) {
            Set<Transaction> consensusTx = new HashSet<>();
            int threshold = Math.max(1, (followees.length - blacklistedFollowees.size()) / 2); // majority
            for (Transaction tx : pendingTransactions) {
                Set<Integer> senders = receivedFrom.getOrDefault(tx.id, new HashSet<>());
                if (senders.size() >= threshold) {
                    consensusTx.add(tx);
                }
            }
            return consensusTx;
        }

        // Otherwise, broadcast everything we currently have
        return new HashSet<>(pendingTransactions);
    }

    @Override
    public void receiveCandidates(ArrayList<Integer[]> candidates) {
        if (candidates == null) return;

        Map<Integer, Set<Integer>> thisRound = new HashMap<>();

        for (Integer[] pair : candidates) {
            int txID = pair[0];
            int sender = pair[1];

            if (sender < 0 || sender >= followees.length) continue;
            if (!followees[sender] || blacklistedFollowees.contains(sender)) continue;

            // Track which followee sent this tx
            thisRound.computeIfAbsent(sender, k -> new HashSet<>()).add(txID);
            receivedFrom.computeIfAbsent(txID, k -> new HashSet<>()).add(sender);

            pendingTransactions.add(new Transaction(txID));
        }

        // Identify potentially malicious followees
        for (int sender = 0; sender < followees.length; sender++) {
            if (!followees[sender] || blacklistedFollowees.contains(sender)) continue;

            Set<Integer> txsSent = thisRound.getOrDefault(sender, new HashSet<>());

            // If a followee sends txs that hardly anyone else has seen, blacklist them
            int suspiciousCount = 0;
            for (Integer txID : txsSent) {
                Set<Integer> senders = receivedFrom.getOrDefault(txID, new HashSet<>());
                if (senders.size() <= 1) suspiciousCount++;
            }

            if (suspiciousCount > txsSent.size() / 2) {
                blacklistedFollowees.add(sender);
            }
        }
    }
}
