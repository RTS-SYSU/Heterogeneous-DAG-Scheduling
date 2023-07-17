package uk.ac.york.mocha.simulator.experiments_CARVB;

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
import uk.ac.york.mocha.simulator.parameters.SystemParameters.ExpName;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.Hardware;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.RecencyType;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.SimuType;
import uk.ac.york.mocha.simulator.resultAnalyzer.AllSystemsResults;
import uk.ac.york.mocha.simulator.resultAnalyzer.OneSystemResults;
import uk.ac.york.mocha.simulator.simulator.SimualtorNWC;
import uk.ac.york.mocha.simulator.simulator.Utils;

public class OneTask1000InstanceUtilCompare {

	static DecimalFormat df = new DecimalFormat("#.###");

	static int cores = 4;
	static int nos = 1;
	static int biggerTimes = 1;
	static int intanceNum = 100;

	static int startUtil = 8;
	static int incrementUtil = 4;
	static int endUtil = 8;
	static boolean hasFaults = false;

	static boolean print = true;

	static long baseDagWCET = 0;
	static long[] baseNodeWCET;

	public static void main(String args[]) {
		oneTaskWithFaults();
	}

	public static void oneTaskWithFaults() {

		int hyperPeriodNum = -1;
		int seed = 1000;

		SystemParameters.utilPerTask = Double.parseDouble(df.format((double) 20 / (double) 10));

		List<Pair<List<DirectedAcyclicGraph>, CacheHierarchy>> allSys = new ArrayList<>();

		for (int i = 0; i < nos; i++) {
			SystemGenerator gen = new SystemGenerator(cores, 1, true, true, null, seed + i, true, print);
			Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(intanceNum,
					hyperPeriodNum, null, false);

			List<DirectedAcyclicGraph> dags = sys.getFirst();

			for (DirectedAcyclicGraph d : dags) {
				SystemParameters.utilPerTask = Double.parseDouble(df.format((double) 8 / (double) 10));

				d.getSchedParameters().setWCET((long) Math
						.ceil((double) d.getSchedParameters().getPeriod() * (double) SystemParameters.utilPerTask));
				SystemGenerator.generateWCET(d, new Random(seed), true, print);

				baseDagWCET = d.getSchedParameters().getWCET();

				d.getSchedParameters().setPeriod(d.getSchedParameters().getPeriod() * biggerTimes);
				d.releaseTime = d.releaseTime * biggerTimes;

				baseNodeWCET = new long[d.getFlatNodes().size()];
				for (int k = 0; k < d.getFlatNodes().size(); k++) {
					baseNodeWCET[k] = d.getFlatNodes().get(k).getWCET();

					d.getFlatNodes().get(k).hasFaults = hasFaults;
					d.getFlatNodes().get(k).cvp.median = 0;
					d.getFlatNodes().get(k).cvp.range = 1.0;
				}
			}

			allSys.add(sys);
		}

		for (int i = startUtil; i <= endUtil; i = i + incrementUtil) {
			SystemParameters.utilPerTask = Double.parseDouble(df.format((double) i / (double) 10));

			for (Pair<List<DirectedAcyclicGraph>, CacheHierarchy> p : allSys) {
				for (DirectedAcyclicGraph d : p.getFirst()) {
					d.getSchedParameters().setWCET((long) Math
							.ceil((double) d.getSchedParameters().getPeriod() * (double) SystemParameters.utilPerTask));
					SystemGenerator.generateWCET(d, new Random(seed), true, print);

				}

			}

			RunOneGroup(1, intanceNum, hyperPeriodNum, true, null, seed, seed, null, nos, true, ExpName.predict,
					allSys);
		}
	}

	static boolean bigger = false;

	public static void RunOneGroup(int taskNum, int intanceNum, int hyperperiodNum, boolean takeAllUtil,
			List<List<Double>> util, int taskSeed, int tableSeed, List<List<Long>> periods, int NoS, boolean randomC,
			ExpName name, List<Pair<List<DirectedAcyclicGraph>, CacheHierarchy>> allsystems) {

		List<OneSystemResults> allSys = new ArrayList<>();

		int[] instanceNo = new int[taskNum];

		if (periods != null && hyperperiodNum > 0) {
			long totalHP = Utils.getHyperPeriod(periods.get(0)) * hyperperiodNum;

			for (int i = 0; i < periods.size(); i++) {
				int insNo = (int) (totalHP / periods.get(0).get(i));
				instanceNo[i] = insNo > intanceNum ? insNo : intanceNum;
			}
		} else if (intanceNum > 0) {
			for (int i = 0; i < instanceNo.length; i++)
				instanceNo[i] = intanceNum;
		} else {
			System.out.println("Cannot get same instances number for randomly generated periods.");
		}

		taskSeed = 1000;
		for (int i = 0; i < NoS; i++) {
			System.out.println(
					"\n\n****************************************************************************************************");
			System.out.println(
					"Util per task: " + SystemParameters.utilPerTask + " --- Current system number: " + (i + 1));

			OneSystemResults res = null;
			res = testOneCaseThreeMethod(allsystems.get(i), taskNum, instanceNo, cores, taskSeed, tableSeed, i);
			allSys.add(res);

			taskSeed++;
		}

		new AllSystemsResults(allSys, instanceNo, cores, taskNum, name);
	}

	/**
	 * This test case will generate two fixed DAG structure.
	 */
	public static OneSystemResults testOneCaseThreeMethod(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys,
			int tasks, int[] NoInstances, int cores, int taskSeed, int tableSeed, int not) {

		boolean lcif = false;

		SimualtorNWC cacheWFSim = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CACHE_AWARE_NEW, RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed,
				lcif);
		Pair<List<DirectedAcyclicGraph>, double[]> pair1 = cacheWFSim.simulate(print);

		for (DirectedAcyclicGraph d : sys.getFirst()) {
			for (Node n : d.getFlatNodes()) {
				n.sensitivity = 0;
				for (int k = 0; k < n.weights.length; k++) {
					n.sensitivity += n.weights[k];
				}
			}
		}

		SimualtorNWC cacheCASim = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CARVB, RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores,
				tableSeed, lcif);
		Pair<List<DirectedAcyclicGraph>, double[]> pair2 = cacheCASim.simulate(print);

		double cc_sens = 0;

		for (int k = 0; k < SystemParameters.cc_weights.length; k++) {
			cc_sens += SystemParameters.cc_weights[k];
		}

		for (DirectedAcyclicGraph d : sys.getFirst()) {
			for (Node n : d.getFlatNodes()) {
				n.sensitivity = 0;
				for (int k = 0; k < n.weights.length; k++) {
					n.sensitivity += n.weights[k] * SystemParameters.cc_weights[k] / cc_sens;
				}
			}
		}

		SimualtorNWC cacheCASim3 = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CARVB, RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores,
				tableSeed, lcif);
		Pair<List<DirectedAcyclicGraph>, double[]> pair3 = cacheCASim3.simulate(print);

		List<DirectedAcyclicGraph> m1 = pair1.getFirst();
		List<DirectedAcyclicGraph> m2 = pair2.getFirst();
		List<DirectedAcyclicGraph> m3 = pair3.getFirst();

		List<List<DirectedAcyclicGraph>> allMethods = new ArrayList<>();

		List<DirectedAcyclicGraph> method1 = new ArrayList<>();
		List<DirectedAcyclicGraph> method2 = new ArrayList<>();
		List<DirectedAcyclicGraph> method3 = new ArrayList<>();

		List<DirectedAcyclicGraph> dags = sys.getFirst();

		/*
		 * get a number of instances from each DAG based on long[] NoInstances.
		 */
		int count = 0;
		int currentID = -1;
		for (int i = 0; i < dags.size(); i++) {
			if (currentID != dags.get(i).id) {

				currentID = dags.get(i).id;
				count = 0;
			}

			if (count < NoInstances[dags.get(i).id]) {
				method1.add(m1.get(i));
				method2.add(m2.get(i));
				method3.add(m3.get(i));
				count++;
			}
		}

		allMethods.add(method1);
		allMethods.add(method2);
		allMethods.add(method3);

		List<double[]> cachePerformance = new ArrayList<>();
		cachePerformance.add(pair1.getSecond());
		cachePerformance.add(pair2.getSecond());
		cachePerformance.add(pair3.getSecond());

		OneSystemResults result = new OneSystemResults(allMethods, cachePerformance);

		return result;
	}
}
