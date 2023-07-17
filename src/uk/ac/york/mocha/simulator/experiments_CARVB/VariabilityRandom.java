package uk.ac.york.mocha.simulator.experiments_CARVB;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.util.Pair;
import org.python.modules.math;

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

public class VariabilityRandom {

	static DecimalFormat df = new DecimalFormat("#.######");

	public static enum faultType {
		high_et, high_pathET, high_in_degree, high_out_degree, high_in_out_degree, high_pathNum,
	}

	static int nop = 4;
	static int nos = 100000;
	static int[] allCores = { 4 };
	static boolean print = false;

	static double maxPercent = 0.0001;
	static int maxEffect = 10000;
	static double effectFactor = 0.2;
	static double[] judgementLine = { 0.1 };

	static int[] allInstanceNum = { 1, 3, 5, 10 };

	static Random rng = new Random(1000);

	public static void main(String args[]) {

		start(nop);

	}

	public static void start(int nop) {

		for (int i = 0; i < judgementLine.length; i++) {
			faults(allCores[0], judgementLine[i], allInstanceNum[0], nop);
		}

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

					String folderName = "result/" + "faults_new/";
					String fileName = "/random" + "_" + cores + "_" + judgement + "_" + id + ".txt";
					Utils.writeResult(folderName, fileName, "", false);

					int startingSeed = initialSeed + id * workload;
					runOneThread(cores, judgement, instanceNum, startingSeed, workload, id);

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
			int id) {

		int seed = startingSeed;

//		List<Pair<Pair<long[], long[]>, Long>> res = new ArrayList<>();

		for (int i = 0; i < workload; i++) {
			System.out.println("No. of system: " + (i + id * workload) + " --- " + "cores: " + cores
					+ ", No. instance: " + instanceNum);

			SystemGenerator gen = new SystemGenerator(SystemParameters.coreNum, 1, true, true, null, seed, true, print);
			Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(instanceNum, -1,
					null, false);

			Pair<Pair<double[], double[]>, Long> r = run(sys, cores, judgement, seed, print);

			write(r, cores, judgement, id, true);

//			res.add(r);
			seed++;
		}

	}

	public static void write(Pair<Pair<double[], double[]>, Long> r, int cores, double judgement, int id,
			boolean append) {
		String out = "";

		double[] configNum = r.getFirst().getFirst();
		double[] configEffect = r.getFirst().getSecond();
		double makespan = r.getSecond();

		for (int i = 0; i < configNum.length; i++) {
			out += df.format(configNum[i]) + " " + df.format(configEffect[i]) + " ";
		}

		out += df.format(makespan) + "\n";

		String folderName = "result/" + "faults_new/";
		String fileName = "/random" + "_" + cores + "_" + judgement + "_" + id + ".txt";
		Utils.writeResult(folderName, fileName, out, append);
	}

	public static Pair<Pair<double[], double[]>, Long> run(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys,
			int cores, double judgement, int seed, boolean print) {

		List<Pair<double[], double[]>> config = setUpSpecificFaults(sys.getFirst(), judgement, true);
		long makespan = oneRun(sys, cores, seed, print);

		setUpSpecificFaults(sys.getFirst(), judgement, false);
		long makespan1 = oneRun(sys, cores, seed, print);

		Pair<Pair<double[], double[]>, Long> res = new Pair<Pair<double[], double[]>, Long>(config.get(0),
				makespan1 - makespan);

		return res;
	}

	public static List<Pair<double[], double[]>> setUpSpecificFaults(List<DirectedAcyclicGraph> dags, double judgement,
			boolean fault) {

		List<Pair<double[], double[]>> allConfigs = new ArrayList<>();

		for (DirectedAcyclicGraph d : dags)
			for (Node n : d.getFlatNodes())
				n.hasFaults = false;

		if (!fault)
			return null;

		for (DirectedAcyclicGraph d : dags) {

			List<Node> allNodes = new ArrayList<Node>(d.getFlatNodes());

			List<Node> faultNodes = new ArrayList<>();
			int faultNodeNum = 0;

			double percent = rng.nextDouble() * maxPercent;
			double effect = rng.nextInt(maxEffect);

			Collections.shuffle(allNodes);

			faultNodeNum = 1; // (int) math.ceil((double) percent * (double) allNodes.size());
			for (int i = 0; i < faultNodeNum; i++) {
				faultNodes.add(allNodes.get(i));
			}

			double[] configNum = new double[faultType.values().length];
			double[] configEffect = new double[faultType.values().length];

			for (Node n : faultNodes) {
				n.hasFaults = true;
				n.cvp.median = 0;
				n.cvp.range = effectFactor;

				Pair<double[], double[]> config = getConfigurationValue(allNodes, n, effect, judgement);

				for (int i = 0; i < faultType.values().length; i++) {
					configNum[i] = configNum[i] + config.getFirst()[i];
					configEffect[i] = configEffect[i] + config.getSecond()[i];
				}
			}

			allConfigs.add(new Pair<double[], double[]>(configNum, configEffect));

		}

		return allConfigs;
	}

	public static Pair<double[], double[]> getConfigurationValue(List<Node> all, Node n, double effect,
			double judgement) {

		double[] configNum = new double[6];
		double[] configEffect = new double[6];

		configNum[0] = (double) n.getWCET();
		configNum[1] = (double) n.pathET;
		configNum[2] = (double) n.getParent().size();
		configNum[3] = (double) n.getChildren().size();
		configNum[4] = (double) (n.getParent().size() + n.getChildren().size());
		configNum[5] = (double) n.pathNum;

//		configNum[0] = (double) n.getWCET() / (double) all.stream().mapToLong(c -> c.getWCET()).max().getAsLong();
//		configNum[1] = (double) n.pathET / (double) all.stream().mapToLong(c -> c.pathET).max().getAsLong();
//		configNum[2] = (double) n.getParent().size()
//				/ (double) all.stream().mapToLong(c -> c.getParent().size()).max().getAsLong();
//		configNum[3] = (double) n.getChildren().size()
//				/ (double) all.stream().mapToLong(c -> c.getChildren().size()).max().getAsLong();
//		configNum[4] = (double) (n.getParent().size() + n.getChildren().size())
//				/ (double) all.stream().mapToLong(c -> c.getParent().size() + c.getChildren().size()).max().getAsLong();
//		configNum[5] = (double) n.pathNum / (double) all.stream().mapToLong(c -> c.pathNum).max().getAsLong();

		return new Pair<double[], double[]>(configNum, configEffect);
	}

	public static Pair<long[], long[]> getConfiguration(List<Node> all, Node n, long effect, double judgement) {

		long[] configNum = new long[6];
		long[] configEffect = new long[6];

		for (int i = 0; i < faultType.values().length; i++) {
			faultType type = faultType.values()[i];

			switch (type) {
			case high_et:
				all.sort((c1, c2) -> compareNodebyET(c1, c2, false));
				break;
			case high_pathET:
				all.sort((c1, c2) -> compareNodebyPathET(c1, c2, false));
				break;
			case high_in_degree:
				all.sort((c1, c2) -> compareNodebyInDegree(c1, c2, false));
				break;
			case high_out_degree:
				all.sort((c1, c2) -> compareNodebyOutDegree(c1, c2, false));
				break;
			case high_in_out_degree:
				all.sort((c1, c2) -> compareNodebyInAndOutDegree(c1, c2, false));
				break;
			case high_pathNum:
				all.sort((c1, c2) -> compareNodebyPathNum(c1, c2, false));
				break;
			default:
				System.err.println("Line 416: Unkown type in method compareNodes(), type: " + type.toString());
				System.exit(-1);
				break;
			}

			int indexN = all.indexOf(n);
			if (indexN <= (int) math.ceil((double) judgement * (double) all.size())) {
				configNum[i] = configNum[i] + 1;
				configEffect[i] = configEffect[i] + effect;
			}
		}

		return new Pair<long[], long[]>(configNum, configEffect);
	}

	public static long oneRun(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int cores, int seed,
			boolean print) {
		SimualtorNWC no_fault = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC, Allocation.SIMPLE,
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

}
