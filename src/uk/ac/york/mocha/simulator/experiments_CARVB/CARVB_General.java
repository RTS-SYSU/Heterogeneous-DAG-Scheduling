package uk.ac.york.mocha.simulator.experiments_CARVB;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
import uk.ac.york.mocha.simulator.simulator.SimualtorGYY;
import uk.ac.york.mocha.simulator.simulator.Utils;

import java.io.*;

public class CARVB_General {

	static DecimalFormat df = new DecimalFormat("#.###");

	static int cores = 4;
	static int nos = 500;
	static int intanceNum = 100;

	static int startUtil = 4;
	static int incrementUtil = 4;
	static int endUtil = 36;

	static boolean print = false;

	static List<Double> speeds;

	public static void main(String args[]) {
		oneTaskWithFaults();
	}

	public static void oneTaskWithFaults() {
		int hyperPeriodNum = -1;
		int seed = 1000;

		for (int i = startUtil; i <= endUtil; i = i + incrementUtil) {
			SystemParameters.utilPerTask = Double.parseDouble(df.format((double) i / (double) 10));
			RunOneGroup(1, intanceNum, hyperPeriodNum, true, null, seed, seed, null, nos, true, ExpName.predict);
		}
	}

	static boolean bigger = false;

	public static void RunOneGroup(int taskNum, int intanceNum, int hyperperiodNum, boolean takeAllUtil,
			List<List<Double>> util, int taskSeed, int tableSeed, List<List<Long>> periods, int NoS, boolean randomC,
			ExpName name) {

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

		for (int i = 0; i < 1; i++) {// nos
			System.out.println(
					"Util per task: " + SystemParameters.utilPerTask + " --- Current system number: " + (i + 1));

			SystemGenerator gen = new SystemGenerator(cores, 1, true, true, null, taskSeed + i, true, print);
			Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys = gen.generatedDAGInstancesInOneHP(intanceNum, -1,
					null, false);
			speeds = gen.generateCoresSpeed(cores, true);

			OneSystemResults res = null;
			res = testOneCaseThreeMethod(sys, taskNum, instanceNo, cores, taskSeed, tableSeed, i);

			allRes.add(res);
			taskSeed++;
		}
		new AllSystemsResults(allRes, instanceNo, cores, taskNum, name);
	}

	/**
	 * This test case will generate two fixed DAG structure.
	 */
	public static OneSystemResults testOneCaseThreeMethod(Pair<List<DirectedAcyclicGraph>, CacheHierarchy> sys,
			int tasks, int[] NoInstances, int cores, int taskSeed, int tableSeed, int not) {

		boolean lcif = true;

		// get the features for each node for each DAG
		String python_file = "data_process/mlp.py";
		ArrayList<ArrayList<Double>> features = new ArrayList<>();
		for (DirectedAcyclicGraph d : sys.getFirst()) {
			for (Node n : d.getFlatNodes()) {
				ArrayList<Double> list = new ArrayList<Double>();
				for (int k = 0; k < n.weights.length; k++) {
					list.add(n.weights[k]);
				}
				features.add(list);
			}

		}

		// write the feature matrixs into a mid-file
		File file = new File("data_process/midway/array.txt");
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < features.size(); i++) {
				for (int j = 0; j < 10; j++) {
					writer.write(features.get(i).get(j) + " ");
				}
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// process python to deal with the data
		ProcessBuilder pb = new ProcessBuilder("python", python_file);
		Process p;
		try {
			p = pb.start();
			InputStream is = p.getInputStream();
			Scanner scanner = new Scanner(is);
			String output = scanner.nextLine();
			ArrayList<Double> myOutputList = new ArrayList<>();
			String[] outputArray = output.replaceAll("[\\[\\]]", "").split(", ");
			for (String s : outputArray) {
				myOutputList.add(Double.parseDouble(s));
			}
			scanner.close();

			// System.out.println(myOutputList[0].size());
			// add the sensitivity to each node
			int feature_id = 0;
			for (DirectedAcyclicGraph d : sys.getFirst()) {
				for (Node n : d.getFlatNodes()) {
					n.sensitivity = myOutputList.get(feature_id++);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Simualtor sim1 = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.WORST_FIT, // Hardware.PROC
				RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed, lcif, speeds);
		Pair<List<DirectedAcyclicGraph>, double[]> pair1 = sim1.simulate(print);

		// SimualtorNWC sim2 = new SimualtorNWC(SimuType.CLOCK_LEVEL,
		// Hardware.PROC_CACHE, Allocation.CACHE_AWARE_NEW, // PROC_CACHE
		// RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed,
		// lcif);
		// Pair<List<DirectedAcyclicGraph>, double[]> pair2 = sim2.simulate(print);

		// for (DirectedAcyclicGraph d : sys.getFirst()) {
		// for (Node n : d.getFlatNodes()) {
		// n.sensitivity = 0;
		// for (int k = 0; k < n.weights.length; k++) {
		// n.sensitivity += n.weights[k];
		// }
		// }
		// }
		//
		// SimualtorNWC cacheCASim = new SimualtorNWC(SimuType.CLOCK_LEVEL,
		// Hardware.PROC_CACHE,
		// Allocation.CACHE_AWARE_PREDICT_R, RecencyType.TIME_DEFAULT,
		// sys.getFirst(), sys.getSecond(), cores,
		// tableSeed, lcif);
		// Pair<List<DirectedAcyclicGraph>, double[]> pair2 =
		// cacheCASim.simulate(print);

		// SimualtorNWC cacheCASim3 = new SimualtorNWC(SimuType.CLOCK_LEVEL,
		// Hardware.PROC_CACHE, Allocation.CARVB, // PROC_CACHE
		// RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed,
		// false);
		// Pair<List<DirectedAcyclicGraph>, double[]> pair3 =
		// cacheCASim3.simulate(print);

		SimualtorGYY cacheCASim2 = new SimualtorGYY(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.CARVB, // PROC_CACHE
				RecencyType.TIME_DEFAULT, sys.getFirst(), sys.getSecond(), cores, tableSeed, false, speeds);
		Pair<List<DirectedAcyclicGraph>, double[]> pair2 = cacheCASim2.simulate(print);

		List<DirectedAcyclicGraph> m1 = pair1.getFirst();
		List<DirectedAcyclicGraph> m2 = pair2.getFirst();

		List<List<DirectedAcyclicGraph>> allMethods = new ArrayList<>();

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
				method1.add(m1.get(i));
				method2.add(m2.get(i));
				count++;
			}
		}

		allMethods.add(method1);
		allMethods.add(method2);

		List<double[]> cachePerformance = new ArrayList<>();
		cachePerformance.add(pair1.getSecond());
		cachePerformance.add(pair2.getSecond());

		OneSystemResults result = new OneSystemResults(allMethods, cachePerformance);

		return result;
	}
}
