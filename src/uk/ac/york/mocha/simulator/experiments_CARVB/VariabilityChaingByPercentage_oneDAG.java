package uk.ac.york.mocha.simulator.experiments_CARVB;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

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

/* Number, Type, Effect */

/*
 * Show the climb effects, can we model that as pressure, i.e. if the pressure
 * is higher than the threshold then it will impact the makespan?
 */

public class VariabilityChaingByPercentage_oneDAG {

	static DecimalFormat df = new DecimalFormat("#.###");

	public static enum faultType {
		all_nodes, all_critical, all_non_critical, high_et, high_pathNum, high_pathET, sensivitiy, high_in_degree,
		high_out_degree, high_in_out_degree, statSensitivity
	}

	static int nop = 1;
	static int nos = 2;
	static int[] allCores = { 4 };
	static boolean print = false;
	static double[] allPercent = { 0.1, 0.2, 0.3, 0.4, 0.5 };

	static List<Long> allEffect;
	static int[] allInstanceNum = { 1, 3, 5, 10 };

	public static void main(String args[]) {

		allEffect = new ArrayList<Long>();
		for (int i = 0; i <= 1000; i += 5) {
			allEffect.add((long) i);
		}

		start(nop);

	}

	public static void start(int nop) {
		
		SystemGenerator gen = new SystemGenerator(SystemParameters.coreNum, 1, true, true, null, 1000, true, print);
		Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(1, -1, null,
				false);

		for (int j = 0; j < allPercent.length; j++) {
			for (int i = 0; i < allEffect.size(); i++) {
				faults(sys, allCores[0], allPercent[j], allEffect.get(i), allInstanceNum[0], nop);
			}
		}
	}

	public static synchronized void addAll(List<List<Long>> res, List<List<Long>> add) {
		res.addAll(add);
	}

	public static void faults(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int cores, double percent, long effect, int instanceNum, int nop) {
		final int initialSeed = 1000;

		List<List<Long>> allResult = new ArrayList<>();

		List<Thread> runners = new ArrayList<>();

		

		for (int i = 0; i < nop; i++) {

			final int id = i;
			final int workload = (int) Math.ceil((double) nos / (double) nop);

			runners.add(new Thread(new Runnable() {

				@Override
				public void run() {
					int startingSeed = initialSeed + id * workload;
					List<List<Long>> result = runOneThread(sys, cores, percent, (double) effect / (double) 1000,
							instanceNum, startingSeed, workload, id);
					addAll(allResult, result);
				}
			}));
		}

		for (Thread t : runners)
			t.run();

		for (Thread t : runners)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		String out = "";
		for (List<Long> ll : allResult) {
			for (int k = 0; k < ll.size(); k++) {
				out += ll.get(k);
				if (k != ll.size() - 1)
					out += " ";
			}
			out += "\n";
		}

		String folderName = "result/" + "faults_new/";
		String fileName = "/out" + "_" + cores + "_" + percent + "_" + effect + "_" + instanceNum + ".txt";
		Utils.writeResult(folderName, fileName, out);
	}

	public static List<List<Long>> runOneThread(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int cores,
			double percent, double effect, int instanceNum, int startingSeed, int workload, int id) {

		int seed = startingSeed;

		List<List<Long>> res = new ArrayList<>();

		for (int i = 0; i < workload; i++) {
			System.out.println("No. of system: " + (i + id * workload) + " --- " + "cores: " + cores + ", percent: "
					+ percent + ", effect: " + effect + ", No. instance: " + instanceNum);

			List<Long> results = new ArrayList<>();

			results.addAll(run(sys, cores, seed, faultType.all_nodes, percent, effect, print));

			results.addAll(run(sys, cores, seed, faultType.high_et, percent, effect, print));

			results.addAll(run(sys, cores, seed, faultType.high_pathET, percent, effect, print));

			results.addAll(run(sys, cores, seed, faultType.high_in_degree, percent, effect, print));

			results.addAll(run(sys, cores, seed, faultType.high_out_degree, percent, effect, print));

			results.addAll(run(sys, cores, seed, faultType.high_in_out_degree, percent, effect, print));

			results.addAll(run(sys, cores, seed, faultType.high_pathNum, percent, effect, print));

			for (DirectedAcyclicGraph d : sys.getFirst()) {
				for (Node n : d.getFlatNodes()) {
					n.sensitivity = 0;
					for (int k = 0; k < n.weights.length; k++) {
						n.sensitivity += n.weights[k];
					}
				}
			}

			results.addAll(run(sys, cores, seed, faultType.sensivitiy, percent, effect, print));

			double d_sens = 0;
			double cc_sens = 0;

			for (int k = 0; k < SystemParameters.cc_weights.length; k++) {
				d_sens += SystemParameters.d_weights[k];
				cc_sens += SystemParameters.cc_weights[k];
			}

			for (DirectedAcyclicGraph d : sys.getFirst()) {
				for (Node n : d.getFlatNodes()) {
					n.sensitivity = 0;
					for (int k = 0; k < n.weights.length; k++) {
						n.sensitivity += n.weights[k] * SystemParameters.d_weights[k] / d_sens;
					}
				}
			}

			results.addAll(run(sys, cores, seed, faultType.sensivitiy, percent, effect, print));

			for (DirectedAcyclicGraph d : sys.getFirst()) {
				for (Node n : d.getFlatNodes()) {
					n.sensitivity = 0;
					for (int k = 0; k < n.weights.length; k++) {
						n.sensitivity += n.weights[k] * SystemParameters.cc_weights[k] / cc_sens;
					}
				}
			}

			results.addAll(run(sys, cores, seed, faultType.sensivitiy, percent, effect, print));

			res.add(results);
			seed++;
		}

		return res;
	}

	public static List<Long> run(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int cores, int seed,
			faultType type, double percent, double effect, boolean print) {
		
	

		List<Long> makespans = new ArrayList<>();

		if (print)
			System.out.println("\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ " + type.toString()
					+ "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		setUpSpecificFaults(sys.getFirst(), type, percent, effect, true, print);
		long makespan_opposite = oneRun(sys, cores, seed, print);
		makespans.add(makespan_opposite);
		if (print)
			System.out.println(
					"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

		if (print)
			System.out.println("\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ " + type.toString()
					+ "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		setUpSpecificFaults(sys.getFirst(), type, percent, effect, false, print);
		long makespan = oneRun(sys, cores, seed, print);
		makespans.add(makespan);
		if (print)
			System.out.println(
					"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n");

		return makespans;
	}

	public static List<List<Node>> setUpSpecificFaults(List<DirectedAcyclicGraph> dags, faultType type, double percent,
			double faultEfect, boolean opposite, boolean print) {

		List<List<Node>> faultNodesinDAGs = new ArrayList<>();

		for (DirectedAcyclicGraph d : dags)
			for (Node n : d.getFlatNodes())
				n.hasFaults = false;

		for (DirectedAcyclicGraph d : dags) {

			// all nodes with no order
			List<Node> allNodes = new ArrayList<Node>(d.getFlatNodes());

			List<Node> faultNodes = new ArrayList<>();
			int faultNodeNum = 0;

			switch (type) {
			case all_nodes:
				if (!opposite)
					faultNodes = new ArrayList<>(allNodes);
				else
					faultNodes = new ArrayList<>();
				break;

			case high_et:
				allNodes.sort((c1, c2) -> compareNodes(dags, c1, c2, opposite, type));
				faultNodeNum = (int) Math.ceil(percent * (double) allNodes.size());

				for (int i = 0; i < faultNodeNum; i++) {
					faultNodes.add(allNodes.get(i));
				}
				break;

			case high_pathNum:
				allNodes.sort((c1, c2) -> compareNodes(dags, c1, c2, opposite, type));
				faultNodeNum = (int) Math.ceil(percent * (double) allNodes.size());

				for (int i = 0; i < faultNodeNum; i++) {
					faultNodes.add(allNodes.get(i));
				}
				break;

			case high_pathET:
				allNodes.sort((c1, c2) -> compareNodes(dags, c1, c2, opposite, type));
				faultNodeNum = (int) Math.ceil(percent * (double) allNodes.size());

				for (int i = 0; i < faultNodeNum; i++) {
					faultNodes.add(allNodes.get(i));
				}
				break;

			case high_out_degree:
				allNodes.sort((c1, c2) -> compareNodes(dags, c1, c2, opposite, type));

				faultNodeNum = (int) Math.ceil(percent * (double) allNodes.size());

				for (int i = 0; i < faultNodeNum; i++) {
					faultNodes.add(allNodes.get(i));
				}
				break;

			case high_in_degree:
				allNodes.sort((c1, c2) -> compareNodes(dags, c1, c2, opposite, type));

				faultNodeNum = (int) Math.ceil(percent * (double) allNodes.size());

				for (int i = 0; i < faultNodeNum; i++) {
					faultNodes.add(allNodes.get(i));
				}
				break;

			case high_in_out_degree:
				allNodes.sort((c1, c2) -> compareNodes(dags, c1, c2, opposite, type));

				faultNodeNum = (int) Math.ceil(percent * (double) allNodes.size());

				for (int i = 0; i < faultNodeNum; i++) {
					faultNodes.add(allNodes.get(i));
				}
				break;

			case statSensitivity:
				allNodes.sort((c1, c2) -> compareNodes(dags, c1, c2, opposite, type));
				faultNodeNum = (int) Math.ceil(percent * (double) allNodes.size());

				for (int i = 0; i < faultNodeNum; i++) {
					faultNodes.add(allNodes.get(i));
				}
				break;
			case sensivitiy:

				/*
				 * Here we normalise the three major factors into one.
				 */

				allNodes.sort((c1, c2) -> compareNodes(dags, c1, c2, opposite, type));
				faultNodeNum = (int) Math.ceil(percent * (double) allNodes.size());

				for (int i = 0; i < faultNodeNum; i++) {
					faultNodes.add(allNodes.get(i));
				}

//				allNodes.sort((c1, c2) -> compareNodebyPathET(dags, c1, c2));
//				faultNodeNum = (int) Math.ceil(percent / 3.0 * (double) allNodes.size());
//
//				for (int i = 0; i < faultNodeNum; i++) {
//					if (!faultNodes.contains(allNodes.get(i)))
//						faultNodes.add(allNodes.get(i));
//				}
//
//				allNodes.sort((c1, c2) -> compareNodebyPath(dags, c1, c2));
//				faultNodeNum = (int) Math.ceil(percent / 3.0 * (double) allNodes.size());
//
//				for (int i = 0; i < faultNodeNum; i++) {
//					if (!faultNodes.contains(allNodes.get(i)))
//						faultNodes.add(allNodes.get(i));
//				}
//
//				allNodes.sort((c1, c2) -> compareNodebyET(c1, c2));
//				faultNodeNum = (int) Math.ceil(percent / 3.0 * (double) allNodes.size());
//
//				for (int i = 0; i < faultNodeNum; i++) {
//					if (!faultNodes.contains(allNodes.get(i)))
//						faultNodes.add(allNodes.get(i));
//				}

				break;

			default:
				break;
			}

			if (print)
				System.out.println("Fault nodes: ");

//			if (!opposite) {
			for (Node n : faultNodes) {
				n.hasFaults = true;
				n.cvp.median = 0;
				n.cvp.range = faultEfect; // ((double) rng.nextInt(effect +
											// 1) / (double) 100) / 3.0;
				if (print)
					System.out.println(n.toString() + ": " + n.cvp.median + ", " + n.cvp.range);
			}

			faultNodesinDAGs.add(faultNodes);
//			} else {
//				allNodes.removeAll(faultNodes);
//
//				for (Node n : allNodes) {
//					n.hasFaults = true;
//					n.cvp.median = 0;
//					n.cvp.range = faultEfect; // ((double) rng.nextInt(effect +
//												// 1) / (double) 100) / 3.0;
//					if (print)
//						System.out.println(n.toString() + ": " + n.cvp.median + ", " + n.cvp.range);
//				}
//
//				faultNodesinDAGs.add(allNodes);
//			}
		}

		return faultNodesinDAGs;
	}

	public static long oneRun(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int cores, int seed,
			boolean print) {
		SimualtorNWC no_fault = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC,
				Allocation.SIMPLE, RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores,
				seed, true);
		no_fault.simulate(print);

		List<DirectedAcyclicGraph> dags = sys.getFirst();
		if (print)
			System.out.println(dags.get(dags.size() - 1).finishTime - dags.get(dags.size() - 1).startTime);

		return dags.get(dags.size() - 1).finishTime - dags.get(dags.size() - 1).startTime;
	}

	public static int compareNodes(List<DirectedAcyclicGraph> dags, Node c1, Node c2, boolean oppsite, faultType type) {
		switch (type) {

		case high_et:
			return compareNodebyET(c1, c2, oppsite);

		case high_pathNum:
			return compareNodebyPath(dags, c1, c2, oppsite);

		case high_pathET:
			return compareNodebyPathET(dags, c1, c2, oppsite);

		case high_out_degree:
			return compareNodebyOutDegree(c1, c2, oppsite);

		case high_in_degree:
			return compareNodebyInDegree(c1, c2, oppsite);

		case high_in_out_degree:
			return compareNodebyInAndOutDegree(c1, c2, oppsite);

		case sensivitiy:
			return compareNodebySensitivity(dags, c1, c2, oppsite);

		default:
			System.err.println("Line 416: Unkown type in method compareNodes(), type: " + type.toString());
			System.exit(-1);
			break;
		}

		return 0;
	}

	public static int compareNodebySensitivity(List<DirectedAcyclicGraph> dags, Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Double.compare(c1.sensitivity, c2.sensitivity);
		else
			return -Double.compare(c1.sensitivity, c2.sensitivity);
	}

	public static int compareNodebyPathET(List<DirectedAcyclicGraph> dags, Node c1, Node c2, boolean oppsite) {
		if (oppsite)
			return Long.compare(c1.pathET, c2.pathET);
		else
			return -Long.compare(c1.pathET, c2.pathET);
	}

	public static int compareNodebyPath(List<DirectedAcyclicGraph> dags, Node c1, Node c2, boolean oppsite) {
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

}
