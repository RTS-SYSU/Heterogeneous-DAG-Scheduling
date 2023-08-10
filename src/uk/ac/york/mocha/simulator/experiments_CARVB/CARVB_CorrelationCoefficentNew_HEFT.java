package uk.ac.york.mocha.simulator.experiments_CARVB;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.util.Pair;

import uk.ac.york.mocha.simulator.allocation.empricial.OnlineFixedScheduleAllocation;
import uk.ac.york.mocha.simulator.allocation.empricial.OnlineWFDNewSimu_Base;
import uk.ac.york.mocha.simulator.entity.DirectedAcyclicGraph;
import uk.ac.york.mocha.simulator.entity.Node;
import uk.ac.york.mocha.simulator.experiments_CARVB.CARVB_CorrelationCoefficentNew_HEFT.faultType;
import uk.ac.york.mocha.simulator.generator.CacheHierarchy;
import uk.ac.york.mocha.simulator.generator.SystemGenerator;
import uk.ac.york.mocha.simulator.parameters.SystemParameters;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.Allocation;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.Hardware;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.RecencyType;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.SimuType;
import uk.ac.york.mocha.simulator.simulator.SimualtorGYY;
import uk.ac.york.mocha.simulator.simulator.Simualtor;
import uk.ac.york.mocha.simulator.simulator.Utils;

/* Number, Type, Effect */

/*
 * Show the climb effects, can we model that as pressure, i.e. if the pressure
 * is higher than the threshold then it will impact the makespan?
 */

public class CARVB_CorrelationCoefficentNew_HEFT {

	static Allocation method = Allocation.WORST_FIT;
	static int nos = 100000;

	static List<Double> speeds;

	static DecimalFormat df = new DecimalFormat("#.###");

	public static enum faultType {
		high_et, high_pathET, high_in_degree, high_out_degree, high_in_out_degree, high_pathNum, high_diff_degree,
		high_up, high_down, high_order
	}

	static int nop = 5;
	static int[] allCores = { 4 };
	static boolean print = false;

	static boolean append = false;

	// static double effectFactor[] = { -1.0, 1.0 };
	static double effectFactor[] = { 1.0 };
	// static double effectFactor[] = { -0.9, -0.8, -0.7, -0.6, -0.5, -0.4,
	// -0.3, -0.2 - 0.1, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, };

	static double[] judgementLine = { 0.1 };

	static int[] allInstanceNum = { 1 };

	static Random rng = new Random(1000);

	public static void main(String args[]) {
		faults(allCores[0], judgementLine[0], allInstanceNum[0], nop);
	}

	public static void faults(int cores, double judgement, int instanceNum, int nop) {
		final int initialSeed = 1000;

		List<Thread> runners = new ArrayList<>();

		for (int i = 0; i < nop; i++) {

			final int id = i;
			final int workload = (int) Math.ceil((double) nos / (double) nop);

			runners.add(new Thread(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < effectFactor.length; i++) {
						double effect = effectFactor[i];

						// String folderName = "result/" + "faults_new/";
						String folderName = "gyy_WCET";
						String fileName = "/random" + "_" + cores + "_" + judgement + "_" + effect + "_" + id + ".txt";
						Utils.writeResult(folderName, fileName, "", false);

						int startingSeed = initialSeed + id * workload;
						runOneThread(cores, judgement, instanceNum, startingSeed, workload, id, effect);
					}
				}
			}));
		}

		for (Thread t : runners)
			t.start();

		for (Thread t : runners)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	public static void runOneThread(int cores, double judgement, int instanceNum, int startingSeed, int workload,
			int id, double effect) {
		int seed = startingSeed;
		for (int i = 0; i < workload; i++) {
			System.out.println("No. of system: " + (i + id * workload) + " --- " + "cores: " + cores + ", effect: "
					+ effect + ", No. instance: " + instanceNum);

			SystemGenerator gen = new SystemGenerator(SystemParameters.coreNum, 1, true, true, null, seed, true, print);
			Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(instanceNum, -1,
					null, false);
			speeds = gen.generateCoresSpeed(cores, true);

			run(sys, cores, judgement, seed, print, id, effect);
			seed++;
		}

	}

	/*
	 * public static void run(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys,
	 * int cores, double judgement, int seed,
	 * boolean printm, int id, double effect) {
	 * // 第一个图的所有节点
	 * DirectedAcyclicGraph d = sys.getFirst().get(0);
	 * int nodeSize = d.getFlatNodes().size();
	 * 
	 * for (int i = 0; i < nodeSize; i++) {
	 * 
	 * List<ChangingNodeInfo> infoCaps = setUpSpecificFaults(sys.getFirst(),
	 * judgement, true, i, effect, true);
	 * long makespan1 = runOne(sys, cores, seed, print);
	 * 
	 * setUpSpecificFaults(sys.getFirst(), judgement, true, i, effect, false);
	 * long makespan2 = runTwo(sys, cores, seed, print);
	 * 
	 * ChangingNodeInfo infoCap = infoCaps.get(0);
	 * infoCap.makespanChange = (double) (makespan1 - makespan2) / (double)
	 * makespan1;
	 * // infoCap.makespanChange = (double) (makespan - makespan1); // new subtracts
	 * // old
	 * 
	 * write(infoCap, cores, judgement, id, true, effect);
	 * }
	 * 
	 * }
	 */

	public static void run(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int cores, double judgement, int seed,
			boolean printm, int id, double effect) {
		// 一个图的所有节点
		DirectedAcyclicGraph d = sys.getFirst().get(0);
		int nodeSize = d.getFlatNodes().size();

		setUpSpecificFaults(sys.getFirst(), judgement, false, -1, effect);
		long makespan1 = runOne(sys, cores, seed, print);

		for (int i = 0; i < nodeSize; i++) {
			List<ChangingNodeInfo> infoCaps = setUpSpecificFaults(sys.getFirst(), judgement, true, i, effect);
			long makespan = runTwo(sys, cores, seed, print);

			ChangingNodeInfo infoCap = infoCaps.get(0);
			infoCap.makespanChange = (double) (makespan - makespan1) / (double) makespan;

			write(infoCap, cores, judgement, id, true, effect);
		}

	}

	/*
	 * public static List<ChangingNodeInfo>
	 * setUpSpecificFaults(List<DirectedAcyclicGraph> dags, double judgement,
	 * boolean fault, int nodeIndex, double effect, boolean max_min) {
	 * 
	 * List<ChangingNodeInfo> allConfigs = new ArrayList<>();
	 * 
	 * for (DirectedAcyclicGraph d : dags)
	 * for (Node n : d.getFlatNodes())
	 * n.hasFaults = false;
	 * 
	 * if (!fault)
	 * return null;
	 * 
	 * for (DirectedAcyclicGraph d : dags) {
	 * List<Node> allNodes = new ArrayList<Node>(d.getFlatNodes());
	 * List<Node> faultNodes = new ArrayList<>();
	 * 
	 * // random chose a node for each dag
	 * if (nodeIndex < 0) {
	 * nodeIndex = rng.nextInt(d.getFlatNodes().size());
	 * }
	 * faultNodes.add(allNodes.get(nodeIndex));
	 * 
	 * ChangingNodeInfo infoCap = new ChangingNodeInfo();
	 * 
	 * for (Node n : faultNodes) {
	 * n.hasFaults = true;
	 * n.cvp.median = 0;
	 * n.cvp.range = effect;
	 * 
	 * ChangingNodeInfo info = getConfiguration(allNodes, n, judgement, effect);
	 * 
	 * for (int i = 0; i < faultType.values().length; i++) {
	 * infoCap.changingNodeByPercent[i] = infoCap.changingNodeByPercent[i] +
	 * info.changingNodeByPercent[i];
	 * infoCap.changingNodeByFlag[i] = infoCap.changingNodeByFlag[i] +
	 * info.changingNodeByFlag[i];
	 * }
	 * infoCap.changingNodeAbsolute += info.changingNodeAbsolute;
	 * 
	 * if (max_min) {
	 * n.rank_down = n.rank_down * 2;
	 * } else {
	 * n.rank_down = n.rank_down / 8;
	 * }
	 * 
	 * }
	 * 
	 * allConfigs.add(infoCap);
	 * 
	 * }
	 * 
	 * return allConfigs;
	 * }
	 */

	public static List<ChangingNodeInfo> setUpSpecificFaults(List<DirectedAcyclicGraph> dags, double judgement,
			boolean fault, int nodeIndex, double effect) {

		List<ChangingNodeInfo> allConfigs = new ArrayList<>();

		for (DirectedAcyclicGraph d : dags)
			for (Node n : d.getFlatNodes())
				n.hasFaults = false;

		if (!fault)
			return null;

		for (DirectedAcyclicGraph d : dags) {
			List<Node> allNodes = new ArrayList<Node>(d.getFlatNodes());
			List<Node> faultNodes = new ArrayList<>();

			// random chose a node for each dag
			if (nodeIndex < 0) {
				nodeIndex = rng.nextInt(d.getFlatNodes().size());
			}
			faultNodes.add(allNodes.get(nodeIndex));

			ChangingNodeInfo infoCap = new ChangingNodeInfo();

			for (Node n : faultNodes) {
				n.hasFaults = true;
				n.cvp.median = 0;
				n.cvp.range = effect;

				ChangingNodeInfo info = getConfiguration(allNodes, n, judgement, effect);

				for (int i = 0; i < faultType.values().length; i++) {
					infoCap.changingNodeByPercent[i] = infoCap.changingNodeByPercent[i] + info.changingNodeByPercent[i];
					infoCap.changingNodeByFlag[i] = infoCap.changingNodeByFlag[i] + info.changingNodeByFlag[i];
				}
				infoCap.changingNodeAbsolute += info.changingNodeAbsolute;

				n.WCET = (long) Math.round(n.WCET * 1.5);
			}

			allConfigs.add(infoCap);

		}

		return allConfigs;
	}

	public static Pair<double[], double[]> getConfigurationValue(List<Node> all, Node n, double judgement) {

		double[] configNum = new double[faultType.values().length];
		double[] configEffect = new double[faultType.values().length];

		configNum[0] = (double) n.getWCET();
		configNum[1] = (double) n.pathET;
		configNum[2] = (double) n.getParent().size();
		configNum[3] = (double) n.getChildren().size();
		configNum[4] = (double) (n.getParent().size() + n.getChildren().size());
		configNum[5] = (double) n.pathNum;
		configNum[6] = (double) n.degree_diff;
		configNum[7] = (double) n.rank_up;
		configNum[8] = (double) n.rank_down;
		configNum[9] = (double) n.topology_order;

		return new Pair<double[], double[]>(configNum, configEffect);
	}

	public static ChangingNodeInfo getConfiguration(List<Node> all, Node n, double judgement, double effect) {

		ChangingNodeInfo info = new ChangingNodeInfo();

		for (int i = 0; i < faultType.values().length; i++) {
			faultType type = faultType.values()[i];

			double value = 0;
			double max = 0;

			switch (type) {
				case high_et:
					all.sort((c1, c2) -> compareNodebyET(c1, c2, false));
					value = n.getWCET();
					max = all.get(0).getWCET();
					break;
				case high_pathET:
					all.sort((c1, c2) -> compareNodebyPathET(c1, c2, false));
					value = n.pathET;
					max = all.get(0).pathET;
					break;
				case high_in_degree:
					all.sort((c1, c2) -> compareNodebyInDegree(c1, c2, false));
					value = n.getParent().size();
					max = all.get(0).getParent().size();
					break;
				case high_out_degree:
					all.sort((c1, c2) -> compareNodebyOutDegree(c1, c2, false));
					value = n.getChildren().size();
					max = all.get(0).getChildren().size();
					break;
				case high_in_out_degree:
					all.sort((c1, c2) -> compareNodebyInAndOutDegree(c1, c2, false));
					value = n.getChildren().size() + n.getParent().size();
					max = all.get(0).getChildren().size() + all.get(0).getParent().size();
					break;
				case high_pathNum:
					all.sort((c1, c2) -> compareNodebyPathNum(c1, c2, false));
					value = n.pathNum;
					max = all.get(0).pathNum;
					break;
				case high_diff_degree:
					all.sort((c1, c2) -> compareNodebyDiffDegree(c1, c2, false));
					value = n.degree_diff;
					max = all.get(0).degree_diff;
					break;
				case high_up:
					all.sort((c1, c2) -> compareNodebyRankUp(c1, c2, false));
					value = n.rank_up;
					max = all.get(0).rank_up;
					break;
				case high_down:
					all.sort((c1, c2) -> compareNodebyRankDown(c1, c2, false));
					value = n.rank_down;
					max = all.get(0).rank_down;
					break;
				case high_order:
					all.sort((c1, c2) -> compareNodebyTopoOrder(c1, c2, false));
					value = n.topology_order;
					max = all.get(0).topology_order;
					break;
				default:
					System.err.println("Line 416: Unkown type in method compareNodes(), type: " + type.toString());
					System.exit(-1);
					break;
			}

			int indexN = all.indexOf(n);

			// configNum[i] = Double.parseDouble(df.format(1 - ((double) indexN / (double)
			// all.size())));

			/*
			 * if (effect > 0) {
			 * info.changingNodeByPercent[i] = Double.parseDouble(df.format(value / max));
			 * info.changingNodeByFlag[i] = 1 - Double.parseDouble(df.format((double) indexN
			 * / (double) all.size()));
			 * info.changingNodeAbsolute = Double
			 * .parseDouble(df.format((double) (n.getWCET() - n.getWCET() * (1 + effect))));
			 * 
			 * } else {
			 * info.changingNodeByPercent[i] = -Double.parseDouble(df.format(value / max));
			 * info.changingNodeByFlag[i] = Double.parseDouble(df.format((double) indexN /
			 * (double) all.size())) - 1;
			 * info.changingNodeAbsolute = -Double
			 * .parseDouble(df.format((double) (n.getWCET() - n.getWCET() * (1 + effect))));
			 * }
			 */

			if (effect > 0) {
				info.changingNodeByPercent[i] = Double.parseDouble(df.format(value));
				info.changingNodeByFlag[i] = 1 - Double.parseDouble(df.format((double) indexN
						/ (double) all.size()));
				info.changingNodeAbsolute = Double
						.parseDouble(df.format((double) (n.getWCET() - n.getWCET() * (1 + effect))));

			} else {
				info.changingNodeByPercent[i] = -Double.parseDouble(df.format(value));
				info.changingNodeByFlag[i] = Double.parseDouble(df.format((double) indexN /
						(double) all.size())) - 1;
				info.changingNodeAbsolute = -Double
						.parseDouble(df.format((double) (n.getWCET() - n.getWCET() * (1 + effect))));
			}

			// if (indexN <= (int) math.ceil((double) judgement * (double)
			// all.size())) {
			// if (effect > 0)
			// info.changingNodeByFlag[i] = 1; // (double)
			// else
			// info.changingNodeByFlag[i] = -1; // (double)
			// }

		}

		return info;
	}

	public static long runOne(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int cores, int seed,
			boolean print) {

		OnlineWFDNewSimu_Base.based_order_counter = 0;

		// SimualtorGYY no_fault = new SimualtorGYY(SimuType.CLOCK_LEVEL, Hardware.PROC,
		// method,
		// RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, seed, true,
		// speeds);
		Simualtor no_fault = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC,
				method, // Hardware.PROC
				RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, seed, true, speeds);
		no_fault.simulate(print);

		List<DirectedAcyclicGraph> dags = sys.getFirst();
		if (print)
			System.out.println(dags.get(dags.size() - 1).finishTime - dags.get(dags.size() - 1).startTime);

		return dags.get(dags.size() - 1).finishTime - dags.get(dags.size() - 1).startTime;
	}

	public static long runTwo(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int cores, int seed,
			boolean print) {

		OnlineFixedScheduleAllocation.execution_order_controller = 0;

		// SimualtorGYY no_fault = new SimualtorGYY(SimuType.CLOCK_LEVEL, Hardware.PROC,
		// method,
		// RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, seed, true,
		// speeds);
		Simualtor no_fault = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC,
				method, // Hardware.PROC
				RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, seed, true, speeds);
		no_fault.simulate(print);

		List<DirectedAcyclicGraph> dags = sys.getFirst();
		if (print)
			System.out.println(dags.get(dags.size() - 1).finishTime - dags.get(dags.size() - 1).startTime);

		return dags.get(dags.size() - 1).finishTime - dags.get(dags.size() - 1).startTime;
	}

	public static int compareNodebySensitivity(Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Double.compare(c1.sensitivity, c2.sensitivity);
		else
			return -Double.compare(c1.sensitivity, c2.sensitivity);
	}

	public static int compareNodebyPathET(Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Long.compare(c1.pathET, c2.pathET);
		else
			return -Long.compare(c1.pathET, c2.pathET);
	}

	public static int compareNodebyPathNum(Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Long.compare(c1.pathNum, c2.pathNum);
		else
			return -Long.compare(c1.pathNum, c2.pathNum);
	}

	public static int compareNodebyET(Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Long.compare(c1.getWCET(), c2.getWCET());
		else
			return -Long.compare(c1.getWCET(), c2.getWCET());
	}

	public static int compareNodebyOutDegree(Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Integer.compare(c1.getChildren().size(), c2.getChildren().size());
		else
			return -Integer.compare(c1.getChildren().size(), c2.getChildren().size());
	}

	public static int compareNodebyInDegree(Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Integer.compare(c1.getParent().size(), c2.getParent().size());
		else
			return -Integer.compare(c1.getParent().size(), c2.getParent().size());
	}

	public static int compareNodebyInAndOutDegree(Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Integer.compare(c1.getParent().size() + c1.getChildren().size(),
					c2.getParent().size() + c2.getChildren().size());
		else
			return -Integer.compare(c1.getParent().size() + c1.getChildren().size(),
					c2.getParent().size() + c2.getChildren().size());
	}

	public static int compareNodebyDiffDegree(Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Integer.compare(c1.degree_diff, c2.degree_diff);
		else
			return -Integer.compare(c1.degree_diff, c2.degree_diff);
	}

	public static int compareNodebyRankUp(Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Long.compare(c1.rank_up, c2.rank_up);
		else
			return -Long.compare(c1.rank_up, c2.rank_up);
	}

	public static int compareNodebyRankDown(Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Long.compare(c1.rank_down, c2.rank_down);
		else
			return -Long.compare(c1.rank_down, c2.rank_down);
	}

	public static int compareNodebyTopoOrder(Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Integer.compare(c1.topology_order, c2.topology_order);
		else
			return -Integer.compare(c1.topology_order, c2.topology_order);
	}

	public static void write(ChangingNodeInfo r, int cores, double judgement, int id, boolean append, double effect) {
		String out = "";

		double[] changingNodePerc = r.changingNodeByPercent;
		double[] changingNodeFlag = r.changingNodeByFlag;
		double changingNodeAbs = r.changingNodeAbsolute;
		double makespan = r.makespanChange;

		for (int i = 0; i < changingNodePerc.length; i++) {
			out += df.format(changingNodePerc[i]) + " " + df.format(changingNodeFlag[i]) + " ";
		}

		out += df.format(changingNodeAbs) + " " + df.format(makespan);

		// String folderName = "result/" + "faults_new/";
		String folderName = "gyy_WCET";
		String fileName = "/random" + "_" + cores + "_" + judgement + "_" + effect + "_" + id + ".txt";

		Utils.writeResult(folderName, fileName, out, append);
	}
}

class ChangingNodeInfo {
	int test = faultType.values().length;
	double[] changingNodeByPercent = new double[faultType.values().length];
	double[] changingNodeByFlag = new double[faultType.values().length];
	double changingNodeAbsolute = -1;

	double makespanChange = -1;
}