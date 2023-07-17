package uk.ac.york.mocha.simulator.experiments_CARVB;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.util.Pair;
import org.python.modules.math;

import uk.ac.york.mocha.simulator.allocation.empricial.OnlineFixedScheduleAllocation;
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

public class CARVB_BEFORE_AFTER_CC_AJLR {

	static DecimalFormat df = new DecimalFormat("#.######");

	public static enum faultType {
		high_et, high_pathET, high_in_degree, high_out_degree, high_in_out_degree, high_pathNum,
	}

	static int nop = 1;
	static int nos = 50000;
	static int[] allCores = { 4 };
	static boolean print = false;

	static double effectFactor = 2.0;
	static double[] judgementLine = { 0.1 };

	static int instNo = 10;
	static int[] allInstanceNum = { 1 };

	static Random rng = new Random(1000);

	public static void main(String args[]) {
		start(nop);
	}

	public static void start(int nop) {
		rng = new Random(1000);
		effectFactor = 1.0;
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
					String fileName = "/before_after_ajlr" + "_" + cores + "_" + judgement + "_" + effectFactor + "_" + id
							+ ".txt";
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

		for (int i = 0; i < workload; i++) {
			System.out.println("No. of system: " + (i + id * workload) + " --- " + "cores: " + cores + ", effect: "
					+ effectFactor + ", No. instance: " + instanceNum);

			SystemGenerator gen = new SystemGenerator(SystemParameters.coreNum, 1, true, true, null, seed, true, print);
			Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(instanceNum, -1,
					null, false);

			Pair<Pair<double[], double[]>, Double> r = run(sys, cores, judgement, seed, print);

			write(r, cores, judgement, id, true);

			seed++;
		}

	}

	public static void write(Pair<Pair<double[], double[]>, Double> r, int cores, double judgement, int id,
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
		String fileName = "/before_after_ajlr" + "_" + cores + "_" + judgement + "_" + effectFactor + "_" + id + ".txt";
		Utils.writeResult(folderName, fileName, out, append);
	}

	public static Pair<Pair<double[], double[]>, Double> run(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys,
			int cores, double judgement, int seed, boolean print) {

		setUpSpecificFaults(sys.getFirst(), judgement, false);

		DirectedAcyclicGraph d = sys.getFirst().get(0);

		List<DirectedAcyclicGraph> dags = new ArrayList<>();
		for (int i = 0; i < instNo; i++) {
			DirectedAcyclicGraph ins = d.deepCopy();
			ins.instanceNo = i;
			ins.releaseTime = i * ins.sched_param.getPeriod();

			ins.instantiated = true;

			for (Node n : ins.getFlatNodes())
				n.setDagInstNo(i);

			dags.add(ins);
		}

		Pair<List<DirectedAcyclicGraph>, CacheHierarchy> full_sys = new Pair<List<DirectedAcyclicGraph>, CacheHierarchy>(
				dags, sys.getSecond());

		long makespan1 = runOne(full_sys, cores, seed, print);
		
		
		List<Pair<double[], double[]>> config = setUpSpecificFaults(sys.getFirst(), judgement, true);
		
		DirectedAcyclicGraph d1 = sys.getFirst().get(0);

		List<DirectedAcyclicGraph> dags1 = new ArrayList<>();
		for (int i = 0; i < instNo; i++) {
			DirectedAcyclicGraph ins = d1.deepCopy();
			ins.instanceNo = i;
			ins.releaseTime = i * ins.sched_param.getPeriod();

			ins.instantiated = true;

			for (Node n : ins.getFlatNodes())
				n.setDagInstNo(i);

			dags1.add(ins);
		}

		Pair<List<DirectedAcyclicGraph>, CacheHierarchy> full_sys1 = new Pair<List<DirectedAcyclicGraph>, CacheHierarchy>(
				dags1, sys.getSecond());
		
		long makespan = runTwo(full_sys1, cores, seed, print);

		Pair<Pair<double[], double[]>, Double> res = new Pair<Pair<double[], double[]>, Double>(config.get(0),
				(double) (makespan - makespan1) / (double) makespan);

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

				Pair<double[], double[]> config = getConfiguration(allNodes, n, judgement);

				for (int i = 0; i < faultType.values().length; i++) {
					configNum[i] = configNum[i] + config.getFirst()[i];
					configEffect[i] = configEffect[i] + config.getSecond()[i];
				}
			}

			allConfigs.add(new Pair<double[], double[]>(configNum, configEffect));

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

	public static Pair<double[], double[]> getConfiguration(List<Node> all, Node n, double judgement) {

		double[] configNum = new double[6];
		double[] configEffect = new double[6];

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

			configNum[i] = Double.parseDouble(df.format(1 - ((double) indexN / (double) all.size())));
//			configNum[i] = Double.parseDouble(df.format(value / max));
//			configEffect[i] = (double) n.getWCET() * (1 + effectFactor);

			if (indexN <= (int) math.ceil((double) judgement * (double) all.size())) {
//				configEffect[i] =   1;
				configEffect[i] = 1; // (double) n.getWCET() * (1 + effectFactor);
			}
		}

		return new Pair<double[], double[]>(configNum, configEffect);
	}

	public static long runOne(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int cores, int seed,
			boolean print) {
		SimualtorNWC no_fault = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.CACHE_AWARE_NEW,
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

		SimualtorNWC no_fault = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.CACHE_AWARE_NEW,
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
