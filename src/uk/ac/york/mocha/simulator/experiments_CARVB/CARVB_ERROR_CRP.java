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
import uk.ac.york.mocha.simulator.simulator.Simualtor;
import uk.ac.york.mocha.simulator.simulator.SimualtorNWC;
import uk.ac.york.mocha.simulator.simulator.Utils;

public class CARVB_ERROR_CRP {

	static DecimalFormat df = new DecimalFormat("#.###");

	static int nos = 500;
	static int intanceNum = 50;

	static int cores = 4;
	static int util = 20;
	static boolean print = false;

	static double[] faultRates = { 0.1, 0.3, 0.5, 0.7, 0.9 };
	static double[] faultEffects = { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 }; // 0.1, 0.2, 0.3, 0.4, 0.5,

	public static void main(String args[]) {
		SystemParameters.utilPerTask = Double.parseDouble(df.format((double) util / (double) 10));

		List<Thread> runners = new ArrayList<>();

		for (int i = 0; i < faultRates.length; i++) {
			final double rate = faultRates[i];
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					for (int j = 0; j < faultEffects.length; j++) {
						oneTaskWithFaults(rate, faultEffects[j]);
					}
				}
			});

			runners.add(t);
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

	public static void oneTaskWithFaults(double rate, double effect) {
		int seed = 1000;

		RunOneGroup(1, intanceNum, -1, true, null, seed, seed, null, nos, true, ExpName.error_crp, rate, effect);
	}

	public static void RunOneGroup(int taskNum, int intanceNum, int hyperperiodNum, boolean takeAllUtil,
			List<List<Double>> util, int taskSeed, int tableSeed, List<List<Long>> periods, int NoS, boolean randomC,
			ExpName name, double rate, double effect) {

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

		List<OneSystemResults> allRes = new ArrayList<>();

		for (int i = 0; i < nos; i++) {

			System.out.println("Rate: " + rate + ", Effect: " + effect + ", Util per task: "
					+ SystemParameters.utilPerTask + " --- Current system number: " + (i + 1));

			SystemGenerator gen = new SystemGenerator(cores, 1, true, true, null, taskSeed + i, true, print);
			Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(intanceNum, -1,
					null, false);

			OneSystemResults res = null;
			res = testOneCaseThreeMethod(sys, taskNum, instanceNo, cores, taskSeed, tableSeed, i, rate, effect);

			allRes.add(res);

			taskSeed++;
		}

		new AllSystemsResults(allRes, instanceNo, nos, taskNum, name, false, rate, effect);
	}

	/**
	 * This test case will generate two fixed DAG structure.
	 */
	public static OneSystemResults testOneCaseThreeMethod(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys,
			int tasks, int[] NoInstances, int cores, int taskSeed, int tableSeed, int not, double rate, double effect) {

		boolean lcif = true;

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

		assignErrors(sys, rate, effect);

		Simualtor sim1 = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.WORST_FIT,
				RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed, lcif);
		Pair<List<DirectedAcyclicGraph>, double[]> pair1 = sim1.simulate(print);

		SimualtorNWC sim2 = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.CACHE_AWARE_NEW,
				RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed, lcif);
		Pair<List<DirectedAcyclicGraph>, double[]> pair2 = sim2.simulate(print);

		SimualtorNWC cacheCASim3 = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.CARVB,
				RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed, false);
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

	private static void assignErrors(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, double rate, double effect) {
		Random rng = new Random(1000);

		List<DirectedAcyclicGraph> dags = sys.getFirst();
		int faultNodeSize = (int) Math.ceil((double) dags.get(0).getFlatNodes().size() * rate);

		List<Integer> faultNodeIndex = new ArrayList<Integer>();

		for (int i = 0; i < faultNodeSize; i++) {
			int index = rng.nextInt(faultNodeSize);
			while (faultNodeIndex.contains(index)) {
				index = rng.nextInt(faultNodeSize);
			}
			faultNodeIndex.add(index);
		}

		for (DirectedAcyclicGraph d : dags) {
			for (int i = 0; i < d.getFlatNodes().size(); i++) {
				Node n = d.getFlatNodes().get(i);
				if (faultNodeIndex.contains(i)) {
					n.hasFaults = true;
					n.cvp.median = 0;
					n.cvp.range = -effect + (rng.nextDouble() * (effect * 2));
				} else {
					n.hasFaults = false;
				}
			}
		}

	}
}
