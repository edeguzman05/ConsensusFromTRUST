import java.util.ArrayList;
import java.util.Set;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {
    private double p_graph;
    private double p_malicious;
    private double p_txDistribution;
    private int numRounds; 
    private boolean[] followees;
    
    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        this.p_graph = p_graph;
        this.p_malicious = p.malicious
        this.p_txDistrubution = p_txDistribution;
        this.numRounds = numRounds;
    }

    public void setFollowees(boolean[] followees) {
        this.followees = followees;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        // IMPLEMENT THIS
    }

    public Set<Transaction> getProposals() {
        // IMPLEMENT THIS
    }

    public void receiveCandidates(ArrayList<Integer[]> candidates) {
        // IMPLEMENT THIS
    }
}
