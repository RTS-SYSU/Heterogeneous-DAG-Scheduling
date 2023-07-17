package uk.ac.york.mocha.simulator.experiments_AJLR_v2_0;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.util.Pair;

import uk.ac.york.mocha.simulator.entity.DirectedAcyclicGraph;
import uk.ac.york.mocha.simulator.generator.CacheHierarchy;
import uk.ac.york.mocha.simulator.generator.SystemGenerator;
import uk.ac.york.mocha.simulator.generator.UUnifastDiscard;
import uk.ac.york.mocha.simulator.parameters.SystemParameters;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.Allocation;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.ExpName;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.Hardware;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.RecencyType;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.SimuType;
import uk.ac.york.mocha.simulator.resultAnalyzer.AllSystemsResults;
import uk.ac.york.mocha.simulator.resultAnalyzer.OneSystemResults;
import uk.ac.york.mocha.simulator.simulator.Simualtor;
import uk.ac.york.mocha.simulator.simulator.SimualtorNWC;
import uk.ac.york.mocha.simulator.simulator.Utils;

public class Test_EP_1_2 {

	static DecimalFormat df = new DecimalFormat("#.###");

	public static void main(String args[]) {
		
		changeTaskNumRunner(1);
		
//		runOneDAG();

//		Thread t1 = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//			}
//		});
//
//		Thread t2 = new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				
//
//			}
//		});
//
//		t1.start();
//		t2.start();
//
//		try {
//			t1.join();
//			t2.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

	}

	public static void runOneDAG() {
		int intanceNum = 10;
		int hyperPeriodNum = -1;
		int seed = 1000;
		int NoS = SystemParameters.NoS * 5;

		List<List<Long>> periods = new ArrayList<>();
		for (int j = 0; j < NoS; j++) {
			ArrayList<Long> period = new ArrayList<>();
			long p = 144000;
			period.add(p);
			periods.add(period);
		}

		UUnifastDiscard uu = new UUnifastDiscard(4, 1, 1000, SystemParameters.coreNum, false, new Random(seed));

		List<List<Double>> utils = new ArrayList<>();
		for (int i = 0; i < NoS; i++) {
			double u = uu.getUtils().get(0);
			List<Double> us = new ArrayList<>();
			us.add(u);
			utils.add(us);
			System.out.println(u);
		}

		RunOneGroup(1, intanceNum, hyperPeriodNum, false, utils, seed, seed, periods, NoS, true, ExpName.oneDAG);
	}

	public static void changeTaskNumRunner(int numMax) {

		int intanceNum = 10;
		int hyperPeriodNum = -1;
		int seed = 1000;

		List<Thread> threads = new ArrayList<>();

		for (int i = 1; i <= numMax; i++) {

			final int num = i;

			threads.add(new Thread(new Runnable() {
				@Override
				public void run() {
					RunOneGroup(num, intanceNum, hyperPeriodNum, true, null, seed, seed, null, 1,
							true, ExpName.taskNum);
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

	public static List<Double> RunOneGroup(int taskNum, int intanceNum, int hyperperiodNum, boolean takeAllUtil,
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

//		List<List<DirectedAcyclicGraph>> systems = new ArrayList<>();
//		for (int i = 0; i < NoS; i++) {
//
//			taskSeed++;
//		}
//
//		systems.sort((c1, c2) -> -Long.compare(c1.get(0).getSchedParameters().getWCET(),
//				c2.get(0).getSchedParameters().getWCET()));

		taskSeed = 1000;
		for (int i = 0; i < NoS; i++) {
			System.out.println(
					"\n\n****************************************************************************************************");
			System.out.println("Change Task Number: " + taskNum + " --- Current system number: " + (i + 1));

			SystemGenerator gen = new SystemGenerator(SystemParameters.coreNum, taskNum, true, takeAllUtil,
					util == null ? null : util.get(i), taskSeed, randomC, SystemParameters.printGen);
			Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(intanceNum, hyperperiodNum,
					periods == null ? null : periods.get(i),false);

			OneSystemResults res = null;

			res = testOneCase(sys, taskNum, instanceNo, SystemParameters.coreNum, taskSeed, tableSeed);

			allSys.add(res);

			taskSeed++;

		}

//		for (int i = 0; i < allSys.size(); i++) {
//			System.out.println(
//					allSys.get(i).resultsPerMethod.get(0).dags.get(0).getSchedParameters().getWCET());
//		}

		allSys.sort((c1, c2) -> -Long.compare(c1.resultsPerMethod.get(0).dags.get(0).getSchedParameters().getWCET(),
				c2.resultsPerMethod.get(0).dags.get(0).getSchedParameters().getWCET()));

		new AllSystemsResults(allSys, instanceNo, SystemParameters.NoS, taskNum, name);

		List<Double> makespan_compare_medain = new ArrayList<>();

		for (OneSystemResults one : allSys) {
			List<Double> makespan_compare = one.resultsPerMetric.get(0).compare.get(0);

			double[] makesapn_compare_array = new double[makespan_compare.size()];
			for (int i = 0; i < makespan_compare.size(); i++) {
				double d = makespan_compare.get(i);
				makesapn_compare_array[i] = d;
			}

			Median med = new Median();
			double medain = med.evaluate(makesapn_compare_array);
//
//			double max = Collections.max(makespan_compare);
//			double min = Collections.min(makespan_compare);
//			double avg = makespan_compare.stream().mapToDouble(c -> c).average().getAsDouble();

			makespan_compare_medain.add(medain);

		}

		return makespan_compare_medain;
	}

	/**
	 * This test case will generate two fixed DAG strcuture.
	 */
	public static OneSystemResults testOneCase(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int tasks, int[] NoInstances, int cores,
			int taskSeed, int tableSeed) {
		
		SimualtorNWC cacheCASim = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.CACHE_AWARE_ROBUST_v2_1,
				RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed, false);
		Pair<List<DirectedAcyclicGraph>, double[]> pair2 = cacheCASim.simulate(true);
		

		Simualtor cacheBFSim = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.CACHE_AWARE,
				RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed, false);
		Pair<List<DirectedAcyclicGraph>, double[]> pair0 = cacheBFSim.simulate(true);

//		Simualtor cacheWFSim = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.FIRST_FIT,
//				RecencyType.TIME, dags, cores, tableSeed, false, false);
//		Pair<List<DirectedAcyclicGraph>, double[]> pair1 = cacheWFSim.simulate(SystemParameters.printSim);

		

		List<DirectedAcyclicGraph> m0 = pair0.getFirst();
//		List<DirectedAcyclicGraph> m1 = pair1.getFirst();
		List<DirectedAcyclicGraph> m2 = pair2.getFirst();

		List<List<DirectedAcyclicGraph>> allMethods = new ArrayList<>();

		List<DirectedAcyclicGraph> method0 = new ArrayList<>();
//		List<DirectedAcyclicGraph> method1 = new ArrayList<>();
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
//				method1.add(m1.get(i));
				method2.add(m2.get(i));
				count++;
			}
		}

		allMethods.add(method0);
//		allMethods.add(method1);
		allMethods.add(method2);

		List<double[]> cachePerformance = new ArrayList<>();
		cachePerformance.add(pair0.getSecond());
//		cachePerformance.add(pair1.getSecond());
		cachePerformance.add(pair2.getSecond());

		OneSystemResults result = new OneSystemResults(allMethods, cachePerformance);

		return result;
	}
}
