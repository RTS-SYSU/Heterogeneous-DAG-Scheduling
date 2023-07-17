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
import uk.ac.york.mocha.simulator.parameters.SystemParameters.ExpName;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.Hardware;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.RecencyType;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.SimuType;
import uk.ac.york.mocha.simulator.resultAnalyzer.AllSystemsResults;
import uk.ac.york.mocha.simulator.resultAnalyzer.OneSystemResults;
import uk.ac.york.mocha.simulator.simulator.SimualtorNWC;
import uk.ac.york.mocha.simulator.simulator.Utils;

public class OneTaskUtilCompare {

	static DecimalFormat df = new DecimalFormat("#.###");
	static int cores = 8;

	static List<double[]> avg_data1 = new ArrayList<>();
	static List<double[]> avg_data2 = new ArrayList<>();

	static List<Long> noCall1 = new ArrayList<>();
	static List<Long> noCall2 = new ArrayList<>();

	static List<Long> d1_delays = new ArrayList<>();
	static List<Long> d1_ets = new ArrayList<>();
	static List<Long> d2_delays = new ArrayList<>();
	static List<Long> d2_ets = new ArrayList<>();

	public static void main(String args[]) {
		oneTaskWithFaults();
	}

	public static void oneTaskWithFaults() {

		int intanceNum = 10;
		int hyperPeriodNum = -1;
		int seed = 1000;

		String out = "";
		String out1 = "";

		SystemParameters.utilPerTask = Double.parseDouble(df.format((double) 40 / (double) 10));

		List<Pair<List<DirectedAcyclicGraph>, CacheHierarchy>> allSys = new ArrayList<>();

		for (int i = 0; i < 1000; i++) {
			SystemGenerator gen = new SystemGenerator(cores, 1, true, true, null, 1000 + i, true,
					SystemParameters.printGen);
			Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(intanceNum, hyperPeriodNum, null, false);
			allSys.add(sys);
		}

//		int count = 1;
		for (int i = 8; i <= 40; i = i + 8) {

			SystemParameters.utilPerTask = Double.parseDouble(df.format((double) i / (double) 10));

//			for (List<DirectedAcyclicGraph> dl : allDags) {
//				for (DirectedAcyclicGraph d : dl) {
////					d.getSchedParameters().setPeriod(d.getSchedParameters().getPeriod() * count);
//					for (Node n : d.getFlatNodes()) {
//						n.WCET = count * (long) Math.ceil((double) n.WCET / (double) 5);
//					}
//				}
//			}
//			count++;

			avg_data1 = new ArrayList<>();
			avg_data2 = new ArrayList<>();

			d1_delays = new ArrayList<>();
			d1_ets = new ArrayList<>();

			d2_delays = new ArrayList<>();
			d2_ets = new ArrayList<>();

			noCall1 = new ArrayList<>();
			noCall2 = new ArrayList<>();

			RunOneGroup(1, intanceNum, hyperPeriodNum, true, null, seed, seed, null, 1000, true, ExpName.util_compare,
					allSys);

			double d1_delay = avg_data1.stream().mapToDouble(d -> d[0]).average().getAsDouble();
			double d1_et = avg_data1.stream().mapToDouble(d -> d[1]).average().getAsDouble();

			double d2_delay = avg_data2.stream().mapToDouble(d -> d[0]).average().getAsDouble();
			double d2_et = avg_data2.stream().mapToDouble(d -> d[1]).average().getAsDouble();

			double call1 = noCall1.stream().mapToDouble(d -> d).average().getAsDouble();
			double call2 = noCall2.stream().mapToDouble(d -> d).average().getAsDouble();

			out += d1_delay + "," + d2_delay + "," + d1_et + "," + d2_et + ";" + call1 + "," + call2 + "\n";

			for (int k = 0; k < d1_delays.size(); k++) {
				out1 += d1_delays.get(k) + "," + d2_delays.get(k) + "," + d1_ets.get(k) + "," + d2_ets.get(k) + "\n";
				if (k % 1000 == 0)
					System.out.println(k + " / " + d1_delays.size());
			}

			Utils.writeResult("result/" + ExpName.util_compare.toString() + "/sum_detail_"
					+ SystemParameters.utilPerTask + ".txt", out1);
		}

		Utils.writeResult("result/" + ExpName.util_compare.toString() + "/sum.txt", out);
	}

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

//			SystemGenerator gen = new SystemGenerator(cores, taskNum, true, takeAllUtil,
//					util == null ? null : util.get(i), taskSeed, randomC, SystemParameters.printGen);
//			List<DirectedAcyclicGraph> dags = gen.generatedDAGInstancesInOneHP(intanceNum, hyperperiodNum,
//					periods == null ? null : periods.get(i), false);

			List<DirectedAcyclicGraph> dags = allsystems.get(i).getFirst();

			for (DirectedAcyclicGraph d : dags) {
				d.getSchedParameters().setWCET((long) Math
						.ceil((double) d.getSchedParameters().getPeriod() * (double) SystemParameters.utilPerTask));

				SystemGenerator.generateWCETs(dags, new Random(1000), true, false);
			}

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
	public static OneSystemResults testOneCaseThreeMethod(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys, int tasks, int[] NoInstances,
			int cores, int taskSeed, int tableSeed, int not) {

		boolean lcif = true;

		for (DirectedAcyclicGraph d : sys.getFirst())
			for (Node n : d.getFlatNodes())
				n.hasFaults = true;

		SimualtorNWC cacheWFSim = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CACHE_AWARE_NEW, RecencyType.TIME_DEFAULT, sys.getFirst(),sys.getSecond(), cores, tableSeed, lcif);
		Pair<List<DirectedAcyclicGraph>, double[]> pair1 = cacheWFSim.simulate(SystemParameters.printSim);

		for (DirectedAcyclicGraph d : sys.getFirst())
			for (Node n : d.getFlatNodes())
				n.hasFaults = true;

		SimualtorNWC cacheCASim = new SimualtorNWC(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE,
				Allocation.CACHE_AWARE_ROBUST_v2_1, RecencyType.TIME_DEFAULT, sys.getFirst(),sys.getSecond(), cores, tableSeed, lcif);
		Pair<List<DirectedAcyclicGraph>, double[]> pair2 = cacheCASim.simulate(SystemParameters.printSim);

		// List<DirectedAcyclicGraph> m0 = pair0.getFirst();
		List<DirectedAcyclicGraph> m1 = pair1.getFirst();
		List<DirectedAcyclicGraph> m2 = pair2.getFirst();

		List<List<DirectedAcyclicGraph>> allMethods = new ArrayList<>();

		// List<DirectedAcyclicGraph> method0 = new ArrayList<>();
		List<DirectedAcyclicGraph> method1 = new ArrayList<>();
		List<DirectedAcyclicGraph> method2 = new ArrayList<>();

		double[] d1 = new double[2];
		d1[0] = m1.stream().mapToDouble(d -> d.getDelayAndETinAvg()[0]).average().getAsDouble();
		d1[1] = m1.stream().mapToDouble(d -> d.getDelayAndETinAvg()[1]).average().getAsDouble();
		double[] d2 = new double[2];
		d2[0] = m2.stream().mapToDouble(d -> d.getDelayAndETinAvg()[0]).average().getAsDouble();
		d2[1] = m2.stream().mapToDouble(d -> d.getDelayAndETinAvg()[1]).average().getAsDouble();

		avg_data1.add(d1);
		avg_data2.add(d2);

		noCall1.add(cacheWFSim.noCalls);
		noCall2.add(cacheCASim.noCalls);

		if (not < 100) {
			List<Long> d1_delay = new ArrayList<>();
			List<Long> d1_et = new ArrayList<>();
			for (DirectedAcyclicGraph d : m1) {
				d1_delay.addAll(d.getAllDelayAndETs().get(0));
				d1_et.addAll(d.getAllDelayAndETs().get(1));
			}
			d1_delays.addAll(d1_delay);
			d1_ets.addAll(d1_et);

			List<Long> d2_delay = new ArrayList<>();
			List<Long> d2_et = new ArrayList<>();
			for (DirectedAcyclicGraph d : m2) {
				d2_delay.addAll(d.getAllDelayAndETs().get(0));
				d2_et.addAll(d.getAllDelayAndETs().get(1));
			}
			d2_delays.addAll(d2_delay);
			d2_ets.addAll(d2_et);
		}

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
				// method0.add(m0.get(i));
				method1.add(m1.get(i));
				method2.add(m2.get(i));
				count++;
			}
		}

		// allMethods.add(method0);
		allMethods.add(method1);
		allMethods.add(method2);

		List<double[]> cachePerformance = new ArrayList<>();
		// cachePerformance.add(pair0.getSecond());
		cachePerformance.add(pair1.getSecond());
		cachePerformance.add(pair2.getSecond());

		OneSystemResults result = new OneSystemResults(allMethods, cachePerformance);

		return result;
	}
}
