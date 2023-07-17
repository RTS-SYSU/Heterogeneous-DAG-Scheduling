package uk.ac.york.mocha.simulator.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.ac.york.mocha.simulator.parameters.SystemParameters;

public class Node implements Serializable {

	private static final long serialVersionUID = -6902741116934537381L;

	/* NodeType */
	public enum NodeType {
		SOURCE, SINK, NORMAL, SOLO
	}

	/*
	 * Node identifiers: id - node ID; dagID - the ID of its DAG; daginstNo - the
	 * instance Number of its DAG.
	 */
	private final int id;
	private final int dagID;
	private int dagInstNo = Integer.MIN_VALUE;

	/*
	 * The layer of the node in the DAG strcuture
	 */
	private final int layer;

	/*
	 * The type of the node, can be SOURCE, SINK, or Normal
	 */
	private final NodeType type;

	/*
	 * The worst-case execution time of the node
	 */
	public long WCET;
	// private long WCETinRange;

	/*
	 * A list of successors/predecessors of the node.
	 */
	private List<Node> successors;
	private List<Node> predecessors;

	public List<List<Node>> allPaths = new ArrayList<>();
	public List<Long> allPathLength = new ArrayList<>();

	public int pathNum = 0;
	public long pathET;

	// self features
	// to do
	// nomilize the wcet
	public int degree_diff;// indegree - outdegree
	public long rank_up, rank_down;// up should add itself wcet, down doesn't need to add self
	public int topology_order;
	public int change_limit = 0;

	// public long globalMaxPathET = -1;
	// public long globalMaxPathNum = -1;

	public double[] weights = new double[10];

	public double sensitivity = -1;

	/*
	 * The priority of the node
	 */
	public int priority = -1;

	/*
	 * Is the node belongs to the critical path of the DAG.
	 */
	public boolean isCritical = false;

	/*
	 * Does the node has faults in execution time?
	 */
	public boolean hasFaults = false;

	/*
	 * Simulation parameters
	 */
	public long release = -1;
	public long start = -1;
	public long finishAt = -1;
	public boolean finish = false;

	public int partition = -1;
	public int affinity = -1;

	public int delayed = -1;
	public long expectedET = -1;
	public long expectedCache = -1;
	public long[] expectedETPerCore;
	public boolean isDelayed = false;

	public double variation = 0;

	public int fixed_order = -1;
	public int fixed_allocation = -1;

	public int repeat_fixed_order = -1;
	public int repeat_fixed_allocation = -1;

	/*
	 * The variability of the node
	 */
	public RecencyProfile crp;
	public RecencyProfile crp_synth;

	public CacheVariabilityProfile cvp;

	boolean isReal;

	Random rng;

	// public List<Long> speeds = new ArrayList<>();

	public Node(int layer, NodeType type, int id, int dagID, RecencyProfile crp, Random rng) {
		this(-1, layer, type, id, dagID, crp, false, rng);
	}

	public Node(long WCET, int layer, NodeType type, int id, int dagID, RecencyProfile crp, boolean real, Random rng) {
		this.WCET = WCET;
		this.layer = layer;
		this.type = type;

		this.id = id;
		this.dagID = dagID;

		this.successors = new ArrayList<>();
		this.predecessors = new ArrayList<>();

		this.rng = rng;

		// this.crp = new CacheRecencyProfile(this, SystemParameters.coreNum,
		// 1000);

		this.crp = crp;
		this.crp_synth = new RecencyProfileSyn(crp.cache, crp.cache.coreNum, 1000);

		this.cvp = new CacheVariabilityProfile(this, SystemParameters.err_median,
				((double) this.rng.nextInt(SystemParameters.err_range + 1) / (double) 100) / 3.0, rng);

		this.isReal = real;
	}

	@Override
	public String toString() {
		return "Node " + dagID + "_" + dagInstNo + "_" + id + ", C:" + WCET + ", Fixed Order: " + fixed_order
				+ ", Fixed Alloc: " + fixed_allocation + ", has Fault: " + hasFaults;
		// return "Node " + dagID + "_" + id + ", C:" + WCET + ", in: " +
		// predecessors.size() + ", out: "
		// + successors.size() + ", in+out: " + predecessors.size() + successors.size()
		// + ", pathNum: " + pathNum;
		// return "Node " + dagID + "_" + dagInstNo + "_" + id + ", C:" + WCET + ", P:"
		// + partition + ", A:" + affinity;
	}

	public String getFullName() {
		return "N " + dagID + "_" + dagInstNo + "_" + id;
	}

	public String getShortName() {
		return "N_" + id;
	}

	public String getExeInfo() {
		return "Node " + dagID + "-" + dagInstNo + "_" + id + ": " + WCET + ", starts: " + start + ", finish: "
				+ finishAt + ", duration: " + (finishAt - start) + ", partition: " + partition + ", affinity: "
				+ affinity;
	}

	public void printExeInfo(String prefix) {
		System.out.printf(prefix
				+ " Node %2d_%2d_%2d    ---    WCET: %5d, starts: %5d, finishes: %5d, duration: %5d, partition: %2d, affinity: %2d\n",
				dagID, dagInstNo, id, WCET, start, finishAt, (finishAt - start), partition, affinity);
	}

	/*
	 * Return the WORST-CASE execution time of the node
	 */
	public long getWCET() {
		return WCET;
	}

	public void setWCET(long wCET) {
		WCET = wCET;
	}

	public int getLayer() {
		return layer;
	}

	public NodeType getType() {
		return type;
	}

	public void addChildren(Node n) {
		successors.add(n);
	}

	public List<Node> getChildren() {
		return successors;
	}

	public List<Node> getParent() {
		return predecessors;
	}

	public void addParent(Node n) {
		predecessors.add(n);
	}

	public int getDagID() {
		return dagID;
	}

	public int getId() {
		return id;
	}

	public int getDagInstNo() {
		return dagInstNo;
	}

	public void setDagInstNo(int dagInstNo) {
		this.dagInstNo = dagInstNo;
	}

	// self changes
	public void setNodeUp(long value) {
		rank_up = WCET + value;
	}

	public void setNodeDown(long value) {
		rank_down = value;
	}

	public void setDegreeDiff(int value) {
		degree_diff = value;
	}

	public void UpDate_Up() {
		long record = 0;
		for (Node pre : predecessors) {
			if (pre.rank_up > record)
				record = pre.rank_up;
		}
		this.setNodeUp(record);
	}

	public void UpDate_Down() {
		long record = 0;
		for (Node suc : successors) {
			if (suc.rank_down + suc.WCET > record)
				record = suc.rank_down + suc.WCET;
		}
		this.setNodeDown(record);
	}

	public void UpDate_Diff() {
		this.setDegreeDiff(predecessors.size() - successors.size());
	}

	// public Node deepCopy() {
	// Node copy = new Node(this.WCET, this.layer, this.type, this.id, this.dagID,
	// this.rng);
	// copy.release = release;
	// copy.start = start;
	// copy.finish = finish;
	// copy.finishAt = finishAt;
	// copy.partition = partition;
	// copy.affinity = affinity;
	// copy.delayed = delayed;
	// return copy;
	// }

}