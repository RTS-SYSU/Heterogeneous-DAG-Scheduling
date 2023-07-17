package uk.ac.york.mocha.simulator.experiments_AJLR_v2_0;

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
import uk.ac.york.mocha.simulator.parameters.SystemParameters.ExpName;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.Hardware;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.RecencyType;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.SimuType;
import uk.ac.york.mocha.simulator.resultAnalyzer.AllSystemsResults;
import uk.ac.york.mocha.simulator.resultAnalyzer.OneSystemResults;
import uk.ac.york.mocha.simulator.simulator.SimualtorNWC;
import uk.ac.york.mocha.simulator.simulator.Utils;

public class OneTaskCriticalVSNonCritical {

	static DecimalFormat df = new DecimalFormat("#.###");
	static int cores = 8;

	public static void main(String args[]) {
		oneTaskWithFaults();
	}

	public static void oneTaskWithFaults() {

		int intanceNum = 10;
		int hyperPeriodNum = -1;
		int seed = 1000;

		SystemParameters.fault_rate = 100;
		SystemParameters.fault_median = 50;
		SystemParameters.fault_range = SystemParameters.fault_median * 2;

		for (int i = 8; i <= 80; i = i + 8) {
			SystemParameters.utilPerTask = Double.parseDouble(df.format((double) i / (double) 10));

			RunOneGroupThreeMethod(1, intanceNum, hyperPeriodNum, true, null, seed, seed, null, 1000, true,
					ExpName.recency_fault_util);

		}
	}

	public static void RunOneGroupThreeMethod(int taskNum, int intanceNum, int hyperperiodNum, boolean takeAllUtil,
			List<List<Double>> util, int taskSeed, int tableSeed, List<List<Long>> periods, int NoS, boolean randomC,
			ExpName name) {

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

			SystemGenerator gen = new SystemGenerator(cores, taskNum, true, takeAllUtil,
					util == null ? null : util.get(i), taskSeed, randomC, SystemParameters.printGen);
			Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(intanceNum, hyperperiodNum,
					periods == null ? null : periods.get(i), false);

			OneSystemResults res = null;

			res = testOneCaseThreeMethod(sys, taskNum, instanceNo, cores, taskSeed, tableSeed);

			allSys.add(res);

			taskSeed++;

		}

		new AllSystemsResults(allSys, instanceNo, cores, taskNum, name);

	}

	/**
	 * This test case will generate two fixed DAG strcuture.
	 */
	public static OneSystemResults testOneCaseThreeMethod(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int tasks, int[] NoInstances,
			int cores, int taskSeed, int tableSeed) {

		boolean lcif = true;
		
		for (DirectedAcyclicGraph d : sys.getFirst())
			for (Node n : d.getFlatNodes())
				n.hasFaults = false;

		SimualtorNWC cacheBFSim = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CACHE_AWARE_NEW, RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed, lcif);
		Pair<List<DirectedAcyclicGraph>, double[]> pair0 = cacheBFSim.simulate(SystemParameters.printSim);

		for (DirectedAcyclicGraph d : sys.getFirst())
			for (Node n : d.getFlatNodes())
				n.hasFaults = true;

		SimualtorNWC cacheWFSim = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CACHE_AWARE_NEW, RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed, lcif);
		Pair<List<DirectedAcyclicGraph>, double[]> pair1 = cacheWFSim.simulate(SystemParameters.printSim);

		for (DirectedAcyclicGraph d : sys.getFirst())
			for (Node n : d.getFlatNodes())
				n.hasFaults = true;

		SimualtorNWC cacheCASim = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CACHE_AWARE_ROBUST_v2_1, RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed, lcif);
		Pair<List<DirectedAcyclicGraph>, double[]> pair2 = cacheCASim.simulate(SystemParameters.printSim);

		List<DirectedAcyclicGraph> m0 = pair0.getFirst();
		List<DirectedAcyclicGraph> m1 = pair1.getFirst();
		List<DirectedAcyclicGraph> m2 = pair2.getFirst();

		List<List<DirectedAcyclicGraph>> allMethods = new ArrayList<>();

		List<DirectedAcyclicGraph> method0 = new ArrayList<>();
		List<DirectedAcyclicGraph> method1 = new ArrayList<>();
		List<DirectedAcyclicGraph> method2 = new ArrayList<>();

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
				method0.add(m0.get(i));
				method1.add(m1.get(i));
				method2.add(m2.get(i));
				count++;
			}
		}

		allMethods.add(method0);
		allMethods.add(method1);
		allMethods.add(method2);

		List<double[]> cachePerformance = new ArrayList<>();
		cachePerformance.add(pair0.getSecond());
		cachePerformance.add(pair1.getSecond());
		cachePerformance.add(pair2.getSecond());

		OneSystemResults result = new OneSystemResults(allMethods, cachePerformance);

		return result;
	}
}
