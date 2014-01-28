package sm.api;

import java.util.ArrayList;
import java.util.List;

public class MWBMatchingAlgorithm {

	private static final double TOL = 1e-10;
	// Number of left side nodes
	int n;

	// Number of right side nodes
	int m;

	double[][] weights;
	double minWeight;
	double maxWeight;

	// If (i, j) is in the mapping, then sMatches[i] = j and tMatches[j] = i.
	// If i is unmatched, then sMatches[i] = -1 (and likewise for tMatches).
	int[] sMatches;
	int[] tMatches;

	static final int NO_LABEL = -1;
	static final int EMPTY_LABEL = -2;

	int[] sLabels;
	int[] tLabels;

	double[] u;
	double[] v;

	double[] pi;

	List<Integer> eligibleS = new ArrayList<Integer>();
	List<Integer> eligibleT = new ArrayList<Integer>();

	public MWBMatchingAlgorithm() {
		n = -1;
		m = -1;
	}

	/**
	 * Creates a BipartiteMatcher and prepares it to run on an n x m graph. All
	 * the weights are initially set to 1.
	 */
	public MWBMatchingAlgorithm(int n, int m) {
		reset(n, m);
	}

	/**
	 * Resets the BipartiteMatcher to run on an n x m graph. The weights are all
	 * reset to 1.
	 */
	private void reset(int n, int m) {
		if (n < 0 || m < 0) {
			throw new IllegalArgumentException("Negative num nodes: " + n
					+ " or " + m);
		}
		this.n = n;
		this.m = m;

		weights = new double[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				weights[i][j] = 1;
			}
		}
		minWeight = 1;
		maxWeight = Double.NEGATIVE_INFINITY;

		sMatches = new int[n];
		tMatches = new int[m];
		sLabels = new int[n];
		tLabels = new int[m];
		u = new double[n];
		v = new double[m];
		pi = new double[m];

	}

	/**
	 * Sets the weight w<sub>ij</sub> to the given value w.
	 * 
	 * @throws IllegalArgumentException
	 *             if i or j is outside the range [0, n).
	 */

	public void setWeight(int i, int j, double w) {
		
		
		if (n == -1 || m == -1) {
			throw new IllegalStateException("Graph size not specified.");
		}
		if ((i < 0) || (i >= n)) {
			throw new IllegalArgumentException("i-value out of range: " + i);
		}
		if ((j < 0) || (j >= m)) {
			throw new IllegalArgumentException("j-value out of range: " + j);
		}
		if (Double.isNaN(w)) {
			throw new IllegalArgumentException("Illegal weight: " + w);
		}
		
		// EDITED BY JP
		w = w + 1.0; // increase the weight by 1.0 

		weights[i][j] = w;
		if ((w > Double.NEGATIVE_INFINITY) && (w < minWeight)) {
			minWeight = w;
		}
		if (w > maxWeight) {
			maxWeight = w;
		}
	}

	public Double getMaximalMatching(){
		Double res = 0.0;
		int match[] = getMatching();
		for(int i=0;i<n;i++){
			int j = match[i];
			// EDITED BY JP
			// decrease by 1.0
			if(j>=0){
				res = res + (weights[i][j] - 1.0);
			}
		}
		return res;
	}

	/**
	 * Returns a maximum-weight perfect matching relative to the weights
	 * specified with setWeight. The matching is represented as an array arr of
	 * length n, where arr[i] = j if (i,j) is in the matching.
	 */
	
	public int[] getMatching() {
		if (n == -1 || m == -1) {
			throw new IllegalStateException("Graph size not specified.");
		}
		if (n == 0) {
			return new int[0];
		}
		ensurePositiveWeights();

		// Step 0: Initialization
		eligibleS.clear();
		eligibleT.clear();
		for (Integer i = 0; i < n; i++) {
			sMatches[i] = -1;

			u[i] = maxWeight; // ambiguous on p. 205 of Lawler, but see p. 202

			// this is really first run of Step 1.0
			sLabels[i] = EMPTY_LABEL;
			eligibleS.add(i);
		}

		for (int j = 0; j < m; j++) {
			tMatches[j] = -1;

			v[j] = 0;
			pi[j] = Double.POSITIVE_INFINITY;

			// this is really first run of Step 1.0
			tLabels[j] = NO_LABEL;
		}

		while (true) {
			// Augment the matching until we can't augment any more given the
			// current settings of the dual variables.
			while (true) {
				// Steps 1.1-1.4: Find an augmenting path
				int lastNode = findAugmentingPath();
				if (lastNode == -1) {
					break; // no augmenting path
				}

				// Step 2: Augmentation
				flipPath(lastNode);
				for (int i = 0; i < n; i++)
					sLabels[i] = NO_LABEL;

				for (int j = 0; j < m; j++) {
					pi[j] = Double.POSITIVE_INFINITY;
					tLabels[j] = NO_LABEL;
				}

				// This is Step 1.0
				eligibleS.clear();
				for (int i = 0; i < n; i++) {
					if (sMatches[i] == -1) {
						sLabels[i] = EMPTY_LABEL;
						eligibleS.add(new Integer(i));
					}
				}

				eligibleT.clear();
			}

			// Step 3: Change the dual variables

			// delta1 = min_i u[i]
			double delta1 = Double.POSITIVE_INFINITY;
			for (int i = 0; i < n; i++) {
				if (u[i] < delta1) {
					delta1 = u[i];
				}
			}

			// delta2 = min_{j : pi[j] > 0} pi[j]
			double delta2 = Double.POSITIVE_INFINITY;
			for (int j = 0; j < m; j++) {
				if ((pi[j] >= TOL) && (pi[j] < delta2)) {
					delta2 = pi[j];
				}
			}

			if (delta1 < delta2) {
				// In order to make another pi[j] equal 0, we'd need to
				// make some u[i] negative.
				break; // we have a maximum-weight matching
			}

			changeDualVars(delta2);
		}

		int[] matching = new int[n];
		for (int i = 0; i < n; i++) {
			matching[i] = sMatches[i];
		}
		return matching;
	}

	/**
	 * Tries to find an augmenting path containing only edges (i,j) for which
	 * u[i] + v[j] = weights[i][j]. If it succeeds, returns the index of the
	 * last node in the path. Otherwise, returns -1. In any case, updates the
	 * labels and pi values.
	 */
	int findAugmentingPath() {
		while ((!eligibleS.isEmpty()) || (!eligibleT.isEmpty())) {
			if (!eligibleS.isEmpty()) {
				int i = ((Integer) eligibleS.get(eligibleS.size() - 1))
						.intValue();
				eligibleS.remove(eligibleS.size() - 1);
				for (int j = 0; j < m; j++) {
					// If pi[j] has already been decreased essentially
					// to zero, then j is already labeled, and we
					// can't decrease pi[j] any more. Omitting the
					// pi[j] >= TOL check could lead us to relabel j
					// unnecessarily, since the diff we compute on the
					// next line may end up being less than pi[j] due
					// to floating point imprecision.
					if ((tMatches[j] != i) && (pi[j] >= TOL)) {
						double diff = u[i] + v[j] - weights[i][j];
						if (diff < pi[j]) {
							tLabels[j] = i;
							pi[j] = diff;
							if (pi[j] < TOL) {
								eligibleT.add(new Integer(j));
							}
						}
					}
				}
			} else {
				int j = ((Integer) eligibleT.get(eligibleT.size() - 1))
						.intValue();
				eligibleT.remove(eligibleT.size() - 1);
				if (tMatches[j] == -1) {
					return j; // we've found an augmenting path
				}

				int i = tMatches[j];
				sLabels[i] = j;
				eligibleS.add(new Integer(i)); // ok to add twice
			}
		}

		return -1;
	}

	/**
	 * Given an augmenting path ending at lastNode, "flips" the path. This means
	 * that an edge on the path is in the matching after the flip if and only if
	 * it was not in the matching before the flip. An augmenting path connects
	 * two unmatched nodes, so the result is still a matching.
	 */
	void flipPath(int lastNode) {
		while (lastNode != EMPTY_LABEL) {
			int parent = tLabels[lastNode];

			// Add (parent, lastNode) to matching. We don't need to
			// explicitly remove any edges from the matching because:
			// * We know at this point that there is no i such that
			// sMatches[i] = lastNode.
			// * Although there might be some j such that tMatches[j] =
			// parent, that j must be sLabels[parent], and will change
			// tMatches[j] in the next time through this loop.
			sMatches[parent] = lastNode;
			tMatches[lastNode] = parent;

			lastNode = sLabels[parent];
		}
	}

	void changeDualVars(double delta) {
		for (int i = 0; i < n; i++) {
			if (sLabels[i] != NO_LABEL) {
				u[i] -= delta;
			}
		}

		for (int j = 0; j < m; j++) {
			if (pi[j] < TOL) {
				v[j] += delta;
			} else if (tLabels[j] != NO_LABEL) {
				pi[j] -= delta;
				if (pi[j] < TOL) {
					eligibleT.add(new Integer(j));
				}
			}
		}
	}

	/**
	 * Ensures that all weights are either Double.NEGATIVE_INFINITY, or strictly
	 * greater than zero.
	 */
	private void ensurePositiveWeights() {
		// minWeight is the minimum non-infinite weight
		if (minWeight < TOL) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					weights[i][j] = weights[i][j] - minWeight + 1;
				}
			}

			maxWeight = maxWeight - minWeight + 1;
			minWeight = 1;
		}
	}

	@SuppressWarnings("unused")
	private void printWeights() {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				System.out.print(weights[i][j] + " ");
			}
			System.out.println("");
		}
	}

}
