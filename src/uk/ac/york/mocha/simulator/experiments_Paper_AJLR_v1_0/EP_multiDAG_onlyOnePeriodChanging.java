package uk.ac.york.mocha.simulator.experiments_Paper_AJLR_v1_0;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.rank.Median;
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

public class EP_multiDAG_onlyOnePeriodChanging {

	static DecimalFormat df = new DecimalFormat("#.###");

	public static void main(String args[]) {
		int nos = 5;
		try {
			nos = Integer.parseInt(args[0]);
			System.out.println("Input received, Number of Systems: " + nos);

		} catch (Exception e) {
			System.out.println("No input given, using the default Number of Sever Core: " + nos);
		}

		SystemParameters.NoS = nos;

		changeTaskPeriodRunner(9);

	}

	public static void changeTaskPeriodRunner(int numMax) {

		int intanceNum = 10;
		int hyperPeriodNum = -1;
		int seed = 1000;

		List<Thread> threads = new ArrayList<>();

		long[] periodOne = { 144000, 72000, 48000, 36000, 28800, 24000, 20500, 18000, 16000 };

		for (int i = 1; i <= numMax; i += 2) {

			final int index = i;

			threads.add(new Thread(new Runnable() {
				@Override
				public void run() {

					List<Double> utils = new ArrayList<>();

					double firstUtil = 0.8 * index;
					double secondUtil = 2.4;
					utils.add(firstUtil);
					utils.add(secondUtil);

					List<List<Double>> allUtils = new ArrayList<>();
					for (int i = 0; i < SystemParameters.NoS; i++)
						allUtils.add(utils);

					List<List<Long>> periods = new ArrayList<>();
					for (int i = 0; i < SystemParameters.NoS; i++) {
						List<Long> p = new ArrayList<>();
						p.add(periodOne[index - 1] / 10);
						p.add((long) (144 * 1000 / 10));
						periods.add(p);
					}

					RunOneGroup(2, intanceNum, hyperPeriodNum, true, allUtils, seed, seed, periods,
							SystemParameters.NoS, true, ExpName.sysUtilOneDAG, index);
				}
			}));
		}

		for (Thread t : threads)
			t.run();

		try {
			for (Thread t : threads)
				t.join();
		} catch (InterruptedException e) {
		}

	}

	public static List<Double> RunOneGroup(int taskNum, int intanceNum, int hyperperiodNum, boolean takeAllUtil,
			List<List<Double>> util, int taskSeed, int tableSeed, List<List<Long>> periods, int NoS, boolean randomC,
			ExpName name, int periodTime) {

		ResultCapsule rc = new ResultCapsule();

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

		final int baseSeed = 1000;

		int not = NoS > 100 ? 100 : NoS;
		int workload = NoS / not;

		List<Thread> runners = new ArrayList<>();
		for (int i = 0; i < not; i++) {

			int offset = i * workload;

			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {

					int seed = baseSeed + offset;

					for (int k = offset; k < offset + workload; k++) {

						System.out.println(
								"\n\n****************************************************************************************************");
						System.out.println("First DAG Util: " + periodTime + " --- Current system number: " + (k + 1));

//						Calendar calendar = Calendar.getInstance();
//						calendar.setTime(new Date());
//						int hours = calendar.get(Calendar.HOUR_OF_DAY);
//						int minutes = calendar.get(Calendar.MINUTE);
//						int seconds = calendar.get(Calendar.SECOND);
//						System.out.println("First DAG Util: " + periodTime + " --- Current system number: " + (k + 1) + ", time: "
//								+ hours + ":" + minutes + ":" + seconds + ".");

						SystemGenerator gen = new SystemGenerator(SystemParameters.coreNum, taskNum, true, takeAllUtil,
								util == null ? null : util.get(k), seed, randomC, SystemParameters.printGen);

						Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(intanceNum, hyperperiodNum,
								periods == null ? null : periods.get(k), false);

						OneSystemResults res = null;

						res = testOneCase(sys, taskNum, instanceNo, SystemParameters.coreNum, seed, tableSeed);

						rc.add(res);

						seed++;
					}
				}
			});

			runners.add(t);
		}

		for (Thread t : runners) {
			t.start();
		}

		for (Thread t : runners) {
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}

		List<OneSystemResults> allSys = rc.allSys;
		allSys.sort((c1, c2) -> -Long.compare(c1.resultsPerMethod.get(0).dags.get(0).getSchedParameters().getWCET(),
				c2.resultsPerMethod.get(0).dags.get(0).getSchedParameters().getWCET()));

		new AllSystemsResults(allSys, instanceNo, SystemParameters.NoS, periodTime, name);

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

			makespan_compare_medain.add(medain);

		}

		return makespan_compare_medain;
	}

	/**
	 * This test case will generate two fixed DAG strcuture.
	 */
	public static OneSystemResults testOneCase(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int tasks, int[] NoInstances, int cores,
			int taskSeed, int tableSeed) {

		Simualtor cacheBFSim = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.WORST_FIT_OUR,
				RecencyType.TIME_DEFAULT, sys.getFirst(),sys.getSecond(), cores, tableSeed, false);
		Pair<List<DirectedAcyclicGraph>, double[]> pair0 = cacheBFSim.simulate(SystemParameters.printSim);

		Simualtor cacheCASim = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.CACHE_AWARE,
				RecencyType.TIME_DEFAULT, sys.getFirst(),sys.getSecond(), cores, tableSeed, true);
		Pair<List<DirectedAcyclicGraph>, double[]> pair2 = cacheCASim.simulate(SystemParameters.printSim);

		List<DirectedAcyclicGraph> m0 = pair0.getFirst();
		List<DirectedAcyclicGraph> m2 = pair2.getFirst();

		List<List<DirectedAcyclicGraph>> allMethods = new ArrayList<>();

		List<DirectedAcyclicGraph> method0 = new ArrayList<>();
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
				method2.add(m2.get(i));
				count++;
			}
		}

		allMethods.add(method0);
		allMethods.add(method2);

		List<double[]> cachePerformance = new ArrayList<>();
		cachePerformance.add(pair0.getSecond());
		cachePerformance.add(pair2.getSecond());

		OneSystemResults result = new OneSystemResults(allMethods, cachePerformance);

		return result;
	}
}
