package uk.ac.york.mocha.simulator.experiments_CARVB;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

import uk.ac.york.mocha.simulator.allocation.empricial.OnlineFixedScheduleAllocation;
import uk.ac.york.mocha.simulator.allocation.empricial.OnlineWFDNewSimu_Base;
import uk.ac.york.mocha.simulator.entity.DirectedAcyclicGraph;
import uk.ac.york.mocha.simulator.entity.Node;
import uk.ac.york.mocha.simulator.generator.CacheHierarchy;
import uk.ac.york.mocha.simulator.generator.SystemGenerator;
import uk.ac.york.mocha.simulator.parameters.SystemParameters;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.Allocation;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.Hardware;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.RecencyType;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.SimuType;
import uk.ac.york.mocha.simulator.simulator.SimualtorNWC;
import uk.ac.york.mocha.simulator.simulator.Utils;


/*
 * Show the climb effects, can we model that as pressure, i.e. if the pressure
 * is higher than the threshold then it will impact the makespan?
 */

public class CARVB_BEFORE_AFTER_CC_NEW_NODE_PERCENT {

	static Allocation method1;
	static Allocation method2;
	static int nos = 50000;
	static double[] percentFactor = { -0.1, 0.1, 0.2, 0.3 };

	static DecimalFormat df = new DecimalFormat("#.###");

	public static enum faultType {
		high_et, high_pathET, high_in_degree, high_out_degree, high_in_out_degree, high_pathNum,
	}

	static int nop = 10;
	static int[] allCores = { 4 };
	static boolean print = false;

	static double effectFactor[] = { -1.0, 1.0 };
	static double[] judgementLine = { 0.1 };
	static int[] allInstanceNum = { 20 };

	public static void main(String args[]) {
		SystemParameters.utilPerTask = 1.2;

		method1 = Allocation.CACHE_AWARE_NEW;
		method2 = Allocation.CARVB;
		faults(allCores[0], judgementLine[0], allInstanceNum[0], nop);

		method1 = Allocation.CARVB;
		method2 = Allocation.CACHE_AWARE_NEW;
		faults(allCores[0], judgementLine[0], allInstanceNum[0], nop);

		SystemParameters.utilPerTask = 1.6;

		method1 = Allocation.CACHE_AWARE_NEW;
		method2 = Allocation.CARVB;
		faults(allCores[0], judgementLine[0], allInstanceNum[0], nop);

		method1 = Allocation.CARVB;
		method2 = Allocation.CACHE_AWARE_NEW;
		faults(allCores[0], judgementLine[0], allInstanceNum[0], nop);

		SystemParameters.utilPerTask = 2.0;

		method1 = Allocation.CACHE_AWARE_NEW;
		method2 = Allocation.CARVB;
		faults(allCores[0], judgementLine[0], allInstanceNum[0], nop);

		method1 = Allocation.CARVB;
		method2 = Allocation.CACHE_AWARE_NEW;
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
						for (int j = 0; j < percentFactor.length; j++) {
							double effect = effectFactor[i];
							double percent = percentFactor[j];

							String method1Name = method1.toString().equals("CARVB") ? "carvb" : "ajlr";
							String method2Name = method2.toString().equals("CARVB") ? "carvb" : "ajlr";

							String folderName = "result/" + "faults_new/";
							String fileName = "/before_after_percent_" + SystemParameters.utilPerTask + "_"
									+ method1Name + "_" + method2Name + "_" + cores + "_" + judgement + "_" + effect
									+ "_" + percent + "_" + id + ".txt";
							Utils.writeResult(folderName, fileName, "", false);

							int startingSeed = initialSeed + id * workload;
							runOneThread(cores, judgement, instanceNum, startingSeed, workload, id, effect, percent);
						}
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
			int id, double effect, double percent) {

		int seed = startingSeed;

		for (int i = 0; i < workload; i++) {
			System.out.println("No. of system: " + (i + id * workload) + " --- " + "util: "
					+ SystemParameters.utilPerTask + ", cores: " + cores + ", effect: " + effect + ", No. instance: "
					+ instanceNum + ", percent: " + percent + ", id: " + id);

			SystemGenerator gen = new SystemGenerator(SystemParameters.coreNum, 1, true, true, null, seed, true, print);
			Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(instanceNum, -1,
					null, false);

			run(sys, cores, judgement, seed, print, id, effect, percent);
			seed++;
		}
	}

	public static void run(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int cores, double judgement, int seed,
			boolean printm, int id, double effect, double percent) {

		double cc_sens = 0;

		for (int k = 0; k < SystemParameters.cc_weights.length; k++) {
			cc_sens += SystemParameters.cc_weights[k];
		}

		// ？
		for (DirectedAcyclicGraph d : sys.getFirst()) {
			for (Node n : d.getFlatNodes()) {
				n.sensitivity = 0;
				for (int k = 0; k < n.weights.length; k++) {
					n.sensitivity += n.weights[k] * SystemParameters.cc_weights[k] / cc_sens;
				}
			}
		}

		setUpSpecificFaults(sys.getFirst(), judgement, false, effect, percent);
		long makespan1 = runOne(sys, cores, seed, print);

		List<ChangingNodeInfo> infoCaps = setUpSpecificFaults(sys.getFirst(), judgement, true, effect, percent);
		long makespan = runTwo(sys, cores, seed, print);

		ChangingNodeInfo infoCap = infoCaps.get(0);
		infoCap.makespanChange = (double) (makespan - makespan1) / (double) makespan;
		write(infoCap, cores, judgement, id, true, effect, percent);
	}

	public static List<ChangingNodeInfo> setUpSpecificFaults(List<DirectedAcyclicGraph> dags, double judgement,
			boolean fault, double effect, double percent) {

		List<ChangingNodeInfo> allConfigs = new ArrayList<>();

		for (DirectedAcyclicGraph d : dags)
			for (Node n : d.getFlatNodes())
				n.hasFaults = false;

		if (!fault)
			return null;

		int faultNodeSize = percent < 0 ? 1 : (int) Math.ceil((double) dags.get(0).getFlatNodes().size() * percent);
		faultNodeSize = faultNodeSize >= dags.get(0).getFlatNodes().size() ? dags.get(0).getFlatNodes().size()
				: faultNodeSize;

//		/* Random Choose*/
//		List<Integer> faultNodeIndex = new ArrayList<Integer>();
//		for (int i = 0; i < faultNodeSize; i++) {
//			int index = rng.nextInt(faultNodeSize);
//			while (faultNodeIndex.contains(index)) {
//				index = rng.nextInt(faultNodeSize);
//			}
//			faultNodeIndex.add(index);
//		}

		for (DirectedAcyclicGraph d : dags) {
			List<Node> allNodes = new ArrayList<Node>(d.getFlatNodes());
			List<Node> faultNodes = new ArrayList<>();

//			/* Random Choose*/
//			for (int i = 0; i < faultNodeSize; i++) {
//				faultNodes.add(allNodes.get(faultNodeIndex.get(i)));
//			}

			/* Sensitivity Choose */
			List<Node> nodesBySens = new ArrayList<>(d.getFlatNodes());
			nodesBySens.sort((c1, c2) -> -Double.compare(c1.sensitivity, c2.sensitivity));
			for (int i = 0; i < faultNodeSize; i++) {
				faultNodes.add(nodesBySens.get(i));
			}

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
			}

			allConfigs.add(infoCap);
		}

		return allConfigs;
	}

	public static Pair<double[], double[]> getConfigurationValue(List<Node> all, Node n, double judgement) {
		double[] configNum = new double[6];
		double[] configEffect = new double[6];

		configNum[0] = (double) n.getWCET();
		configNum[1] = (double) n.pathET;
		configNum[2] = (double) n.getParent().size();
		configNum[3] = (double) n.getChildren().size();
		configNum[4] = (double) (n.getParent().size() + n.getChildren().size());
		configNum[5] = (double) n.pathNum;

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
			default:
				System.err.println("Line 416: Unkown type in method compareNodes(), type: " + type.toString());
				System.exit(-1);
				break;
			}

			int indexN = all.indexOf(n);

			// configNum[i] = Double.parseDouble(df.format(1- ((double) indexN /
			// (double) all.size())));

			if (effect > 0) {
				info.changingNodeByPercent[i] = Double.parseDouble(df.format(value / max));
				info.changingNodeByFlag[i] = Double.parseDouble(df.format((double) indexN / (double) all.size()));
				info.changingNodeAbsolute = Double
						.parseDouble(df.format((double) (n.getWCET() - n.getWCET() * (1 + effect))));

			} else {
				info.changingNodeByPercent[i] = -Double.parseDouble(df.format(value / max));
				info.changingNodeByFlag[i] = -Double.parseDouble(df.format((double) indexN / (double) all.size()));
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

		SimualtorNWC no_fault = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, method1,
				RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, seed, true);
		no_fault.simulate(print);

		List<DirectedAcyclicGraph> dags = sys.getFirst();
		if (print)
			System.out.println(dags.get(dags.size() - 1).finishTime - dags.get(dags.size() - 1).startTime);

		return dags.get(dags.size() - 1).finishTime - dags.get(dags.size() - 1).startTime;
	}

	public static long runTwo(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int cores, int seed,
			boolean print) {

		OnlineFixedScheduleAllocation.execution_order_controller = 0;

		SimualtorNWC no_fault = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, method2,
				RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, seed, true);
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

	public static int compareNodes(List<DirectedAcyclicGraph> dags, Node c1, Node c2, boolean oppsite, faultType type) {
		switch (type) {

		case high_et:
			return compareNodebyET(c1, c2, oppsite);

		case high_pathNum:
			return compareNodebyPathNum(c1, c2, oppsite);

		case high_pathET:
			return compareNodebyPathET(c1, c2, oppsite);

		case high_out_degree:
			return compareNodebyOutDegree(c1, c2, oppsite);

		case high_in_degree:
			return compareNodebyInDegree(c1, c2, oppsite);

		case high_in_out_degree:
			return compareNodebyInAndOutDegree(c1, c2, oppsite);

		default:
			System.err.println("Line 416: Unkown type in method compareNodes(), type: " + type.toString());
			System.exit(-1);
			break;
		}

		return 0;
	}

	public static void write(ChangingNodeInfo r, int cores, double judgement, int id, boolean append, double effect,
			double percent) {
		String out = "";

		double[] changingNodePerc = r.changingNodeByPercent;
		double[] changingNodeFlag = r.changingNodeByFlag;
		double changingNodeAbs = r.changingNodeAbsolute;
		double makespan = r.makespanChange;

		for (int i = 0; i < changingNodePerc.length; i++) {
			out += df.format(changingNodePerc[i]) + " " + df.format(changingNodeFlag[i]) + " ";
		}

		out += df.format(changingNodeAbs) + " " + df.format(makespan);

		String method1Name = method1.toString().equals("CARVB") ? "carvb" : "ajlr";
		String method2Name = method2.toString().equals("CARVB") ? "carvb" : "ajlr";

		String folderName = "result/" + "faults_new/";
		String fileName = "/before_after_percent_" + SystemParameters.utilPerTask + "_" + method1Name + "_"
				+ method2Name + "_" + cores + "_" + judgement + "_" + effect + "_" + percent + "_" + id + ".txt";
		Utils.writeResult(folderName, fileName, out, append);
	}
}