package uk.ac.york.mocha.simulator.experiments_AJLR_v2_0;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import uk.ac.york.mocha.simulator.simulator.Simualtor;
import uk.ac.york.mocha.simulator.simulator.SimualtorNWC;

/* Number, Type, Effect */

/*
 * Show the climb effects, can we model that as pressure, i.e. if the pressure
 * is higher than the threshold then it will impact the makespan?
 */

public class Test {

	static DecimalFormat df = new DecimalFormat("#.###");

	public static void main(String args[]) {
		faults();
	}

	public static int NoF = 1;

	public static void faults() {

		int cores = 8;
		int seed = 1000;
		boolean print = true;

		SystemGenerator gen = new SystemGenerator(SystemParameters.coreNum, 1, true, true, null, seed, true,
				SystemParameters.printGen);
		Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(1, -1, null, false);

		List<DirectedAcyclicGraph> dags = sys.getFirst();
		if (print) {

			List<Node> longestPath = dags.get(0).longestPath;
			String out = "longest path: ";
			for (int i = 0; i < longestPath.size(); i++) {
				out += longestPath.get(i).getShortName();

				if (i != longestPath.size() - 1)
					out += "  ->  ";
			}
			System.out.println(out);
		}

		setUpGenearalFaults(-1, dags);
		oneRun(sys, cores, seed, print);

		for (int i = 0; i < dags.get(0).longestPath.size(); i++) {
			setUpSpecificFaults(i + 1, 1, 0, 0.5, dags, print);
			oneRun(sys, cores, seed, print);
		}

	}

	public static long oneRun(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int cores, int seed, boolean print) {
		SimualtorNWC no_fault = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CACHE_AWARE_ROBUST_v2_1, RecencyType.TIME_DEFAULT, sys.getFirst(),sys.getSecond(), cores, seed, true);
		no_fault.simulate(print);
		System.out.println(sys.getFirst().get(0).finishTime);

		return sys.getFirst().get(0).finishTime;
	}

	public static void setUpSpecificFaults(int nof, int faultFlag, double medain, double effect,
			List<DirectedAcyclicGraph> dags, boolean print) {

		for (DirectedAcyclicGraph d : dags) {

			List<Node> flatNodes = d.getFlatNodes();
			List<Node> critical = d.longestPath;

			List<Node> non_critical = new ArrayList<>();
			for (Node n : flatNodes) {
				if (critical.contains(n))
					continue;
				else
					non_critical.add(n);
			}

			List<Node> nodeForFaults = null;
			switch (faultFlag) {
			case 0:
				nodeForFaults = new ArrayList<>(flatNodes);
				break;
			case 1:
				nodeForFaults = new ArrayList<>(critical);
				break;
			case 2:
				nodeForFaults = new ArrayList<>(non_critical);
				break;
			default:
				break;
			}

			if (nof > nodeForFaults.size())
				nof = nodeForFaults.size();

			Random rng = new Random(1000);

			if (print)
				System.out.println("Fault nodes: ");

			int count = 0;
			while (count < nof) {
				int index = rng.nextInt(nodeForFaults.size());

				Node n = nodeForFaults.get(index);
				n.hasFaults = true;
				n.cvp.median = medain;
				n.cvp.range = effect; // ((double) rng.nextInt(effect + 1) / (double) 100) / 3.0;

				if (print)
					System.out.println(n.toString() + ": " + n.cvp.median + ", " + n.cvp.range);

				nodeForFaults.remove(index);

				count++;

				if (nodeForFaults.size() == 0)
					break;
			}

		}

	}

	/*************************************************************************************************************************/

	public static void faultsByNodeType() {

		int cores = 8;
		int seed = 1000;

		SystemGenerator gen = new SystemGenerator(SystemParameters.coreNum, 1, true, true, null, seed, true,
				SystemParameters.printGen);
		Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(1, -1, null, false);

		List<DirectedAcyclicGraph> dags = sys.getFirst();
		setUpGenearalFaults(-1, dags);

		SimualtorNWC no_fault = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CACHE_AWARE_ROBUST_v2_1, RecencyType.TIME_DEFAULT, sys.getFirst(),sys.getSecond(), cores, seed, true);
		no_fault.simulate(true);

		System.out.println("\n\n");

		setUpGenearalFaults(0, dags);
		SimualtorNWC all_fault = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CACHE_AWARE_ROBUST_v2_1, RecencyType.TIME_DEFAULT, sys.getFirst(),sys.getSecond(), cores, seed, true);
		all_fault.simulate(true);

		System.out.println("\n\n");

		setUpGenearalFaults(1, dags);
		SimualtorNWC critical_fault = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CACHE_AWARE_ROBUST_v2_1, RecencyType.TIME_DEFAULT, sys.getFirst(),sys.getSecond(), cores, seed, true);
		critical_fault.simulate(true);

		System.out.println("\n\n");

		setUpGenearalFaults(2, dags);
		SimualtorNWC non_critical_fault = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CACHE_AWARE_ROBUST_v2_1, RecencyType.TIME_DEFAULT, sys.getFirst(),sys.getSecond(), cores, seed, true);
		non_critical_fault.simulate(true);

//		long longestpath = dags.get(0).longestPath.stream().mapToLong(n -> n.getWCET()).sum();

	}

	/*
	 * Set faults by node "criticality" --- No nodes, All nodes, Critical nodes,
	 * Non-critical nodes.
	 */
	public static void setUpGenearalFaults(int faultFlag, List<DirectedAcyclicGraph> dags) {
		switch (faultFlag) {
		case -1:
			for (DirectedAcyclicGraph d : dags) {
				for (Node n : d.getFlatNodes()) {
					n.hasFaults = false;
				}
			}
			break;
		case 0:
			for (DirectedAcyclicGraph d : dags) {
				for (Node n : d.getFlatNodes()) {
					n.hasFaults = true;
				}
			}
			break;
		case 1:
			for (DirectedAcyclicGraph d : dags) {
				for (Node n : d.getFlatNodes()) {
					if (n.isCritical)
						n.hasFaults = true;
					else
						n.hasFaults = false;
				}
			}
			break;
		case 2:
			for (DirectedAcyclicGraph d : dags) {
				for (Node n : d.getFlatNodes()) {
					if (!n.isCritical)
						n.hasFaults = true;
					else
						n.hasFaults = false;
				}
			}
			break;
		default:
			break;
		}
	}

	/*************************************************************************************************************************/

	/**
	 * A test program checking the implementation correctness of the longest path.
	 */
	public static void checkCriticalPath() {

		for (int i = 0; i < 1000000; i++) {
			int seed = i;

			SystemGenerator gen = new SystemGenerator(SystemParameters.coreNum, 1, true, true, null, seed, true,
					SystemParameters.printGen);
			Pair<List<DirectedAcyclicGraph>,CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(1, -1, null, false);

//			System.out.println(dags.get(0).toString());

			Simualtor cacheCASim = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC, Allocation.FIRST_FIT,
					RecencyType.TIME_DEFAULT, sys.getFirst(),sys.getSecond(), 16, seed, true);
			cacheCASim.simulate(false, 0);

//			System.out.println(dags.get(0).printExeInfo());

			List<DirectedAcyclicGraph> dags = sys.getFirst();
			
			long longestpath = dags.get(0).longestPath.stream().mapToLong(n -> n.getWCET()).sum();
			if (longestpath != dags.get(0).finishTime) {
				System.err.println("****!!! The " + i + "th time !!!****");
				System.out.println("critical path length: " + longestpath);
				System.out.println("DAG finish time: " + dags.get(0).finishTime);
				System.err.println("****!!! critical path != dag finish !!!****");
				System.exit(-1);
			} else {
				System.out.println("****!!! the " + i + "th time" + " !!!****");
				System.out.println("****!!! critical path found correctly !!!****");
				System.out.println("\n");
			}

		}

	}

}
