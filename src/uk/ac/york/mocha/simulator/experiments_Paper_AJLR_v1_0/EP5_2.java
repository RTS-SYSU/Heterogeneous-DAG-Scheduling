package uk.ac.york.mocha.simulator.experiments_Paper_AJLR_v1_0;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

import uk.ac.york.mocha.simulator.entity.DirectedAcyclicGraph;
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
import uk.ac.york.mocha.simulator.simulator.Simualtor;
import uk.ac.york.mocha.simulator.simulator.Utils;

public class EP5_2 {

	static DecimalFormat df = new DecimalFormat("#.#");

	public static void main(String args[]) {

//		changeTaskUtil();

		changeTaskNumRunner(5);

	}

	public static void changeTaskNumRunner(int numMax) {

		int intanceNum = 10;
		int hyperPeriodNum = -1;
		int seed = 1000;

		SystemParameters.utilPerTask = 0.8;

		List<Thread> threads = new ArrayList<>();

		for (int i = 1; i <= 5; i++) {

			final int num = i;

			threads.add(new Thread(new Runnable() {
				@Override
				public void run() {
					RunOneGroupThreeMethods(num, intanceNum, hyperPeriodNum, true, null, seed, seed, null,
							SystemParameters.NoS, true, RecencyType.TIME_DEFAULT, ExpName.offline_multi);
				}
			}));
		}

		for (Thread t : threads)
			t.start();

		try {
			for (Thread t : threads)
				t.join();
		} catch (InterruptedException e) {
		}

	}

	public static void changeTaskUtil() {

		int intanceNum = 10;
		int hyperPeriodNum = -1;
		int seed = 1000;

		SystemParameters.fault_rate = 0;
		SystemParameters.fault_median = 0;
		SystemParameters.fault_range = SystemParameters.fault_median * 2;

		for (int i = 8; i <= 40; i = i + 8) {
			SystemParameters.utilPerTask = Double.parseDouble(df.format((double) i / (double) 10));

			RunOneGroupThreeMethods(1, intanceNum, hyperPeriodNum, true, null, seed, seed, null, SystemParameters.NoS,
					true, RecencyType.TIME_DEFAULT, ExpName.offline);
		}

	}

	public static void RunOneGroupThreeMethods(int taskNum, int intanceNum, int hyperperiodNum, boolean takeAllUtil,
			List<List<Double>> util, int taskSeed, int tableSeed, List<List<Long>> periods, int NoS, boolean randomC,
			RecencyType type, ExpName name) {

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
			System.out.println("Change Task Number: " + taskNum + "  " + SystemParameters.utilPerTask
					+ " --- Current system number: " + (i + 1));

			SystemGenerator gen = new SystemGenerator(SystemParameters.coreNum, taskNum, true, takeAllUtil,
					util == null ? null : util.get(i), taskSeed, randomC, SystemParameters.printGen);
			Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(intanceNum, hyperperiodNum,
					periods == null ? null : periods.get(i), true);

			OneSystemResults res = null;

			res = testOneCaseThreePattern(sys, taskNum, instanceNo, SystemParameters.coreNum, taskSeed, tableSeed,
					type);

			allSys.add(res);

			taskSeed++;

		}

		new AllSystemsResults(allSys, instanceNo, SystemParameters.NoS, taskNum, name, "_" + type.toString());

	}

	public static OneSystemResults testOneCaseThreePattern(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int tasks,
			int[] NoInstances, int cores, int taskSeed, int tableSeed, RecencyType type) {

		Simualtor cacheBFSim = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.WORST_FIT, type,
				sys.getFirst(),sys.getSecond(), cores, tableSeed, false);
		Pair<List<DirectedAcyclicGraph>, double[]> pair0 = cacheBFSim.simulate(SystemParameters.printSim);
//
		Simualtor cacheWFSim = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.CACHE_AWARE, type,
				sys.getFirst(),sys.getSecond(), cores, tableSeed, true);
		Pair<List<DirectedAcyclicGraph>, double[]> pair1 = cacheWFSim.simulate(SystemParameters.printSim);

		Simualtor cacheSim = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.OFFLINE_CACHE_AWARE,
				type, sys.getFirst(),sys.getSecond(), cores, tableSeed, true);
		Pair<List<DirectedAcyclicGraph>, double[]> pair2 = cacheSim.simulate(SystemParameters.printSim);

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
