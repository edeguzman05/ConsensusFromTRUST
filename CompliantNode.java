import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/* CompliantNode refers to a node that follows the rules (not malicious) */
public class CompliantNode implements Node {

    private boolean[] followees;
    private HashSet<Transaction> pendingTransactions;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        // constructor parameters not needed for this simple strategy
        this.pendingTransactions = new HashSet<Transaction>();
    }

    @Override
    public void setFollowees(boolean[] followees) {
        this.followees = followees;
    }

    @Override
    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        if (pendingTransactions != null) {
            this.pendingTransactions.addAll(pendingTransactions);
        }
    }

    @Override
    public Set<Transaction> getProposals() {
        // always broadcast everything we've ever seen
        return new HashSet<Transaction>(this.pendingTransactions);
    }

    @Override
    public void receiveCandidates(ArrayList<Integer[]> candidates) {
        if (candidates == null) return;

        for (Integer[] cand : candidates) {
            int txID = cand[0];
            int sender = cand[1];
            if (sender < 0 || sender >= followees.length) continue;
            if (!followees[sender]) continue;
            // accept all transactions from valid followees
            this.pendingTransactions.add(new Transaction(txID));
        }
    }
}
