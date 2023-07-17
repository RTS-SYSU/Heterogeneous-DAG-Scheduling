package uk.ac.york.mocha.simulator.experiments_real;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;

import uk.ac.york.mocha.simulator.allocation.OnlineCacheAware_ForCompare;
import uk.ac.york.mocha.simulator.entity.DirectedAcyclicGraph;
import uk.ac.york.mocha.simulator.entity.RecencyProfileReal;
import uk.ac.york.mocha.simulator.generator.CacheHierarchy;
import uk.ac.york.mocha.simulator.generator.SystemGenerator;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.Allocation;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.Hardware;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.RecencyType;
import uk.ac.york.mocha.simulator.parameters.SystemParameters.SimuType;
import uk.ac.york.mocha.simulator.simulator.Simualtor;
import uk.ac.york.mocha.simulator.simulator.Utils;

public class FiveNodeForCompare {

	public static String[] tasks = { "tacle/ndes", "tacle/h264_dec", "tacle/adpcm_dec", "tacle/adpcm_enc",
			"tacle/gsm_dec", "tacle/statemate", "tacle/g723_enc", "tacle/anagram", "tacle/gsm_enc", "tacle/mpeg2",
			"tacle/ammunition" };

	public static final String crpFile = "crp/profile.tacle.crp.json"; // "crp/test.profile_220311.crp.scaled.json";

	public static final int nos = 1000;
	public static int seed = 1000;
	public static int cores = 5;
	public static int taskNum = 5;
	public static final boolean licf = true;
	public static boolean print = false;
	public static Random rng = new Random(seed);

	public static final int instanceNum = 20;
	public static final int totalDuration = 20000;
	public static int util = 1;

	public static void main(String args[]) {
		compare();
	}

	

	public static void compare() {
		
		oneGroup("false_crp");

	}

	public static void oneGroup(String filename) {
		List<long[]> res0 = new ArrayList<>();

		cores = 3;
		taskNum = 5;

		for (int i = 0; i < nos; i++) {
			rng = new Random(seed);
			System.out.println("--------------------- " + " Core: " + cores + ", TaskNum: " + taskNum + ", No. System "
					+ (i + 1) + " ---------------------");
			long[] r = runOne(cores, taskNum, instanceNum, false, seed, rng);
			res0.add(r);
			seed++;
		}

		String out = "";
		System.out.println("\n\n\n------------ ALL RESULTS ------------");
		for (int i = 0; i < res0.size(); i++) {
			for (int k = 0; k < res0.get(i).length; k++) {
				if (k < res0.get(i).length - 1) {
					System.out.print(res0.get(i)[k] + ",");
					out += res0.get(i)[k] + ",";
				} else {
					System.out.print(res0.get(i)[k] + "\n");
					out += res0.get(i)[k] + "\n";
				}
			}
		}

		Utils.writeResult("result/real/" + filename + ".txt", out);
	}

	public static void changeUtilization(int startUtil, int endUtil) {

		cores = 3;
		taskNum = 5;

		List<List<long[]>> res = new ArrayList<>();

		for (int i = 0; i <= endUtil - startUtil; i++) {
			res.add(new ArrayList<>());
		}

		int index = 0;
		for (int i = startUtil; i <= endUtil; i++) {
			util = i;
			for (int k = 0; k < nos; k++) {
				rng = new Random(seed);
				System.out.println("--------------------- " + " Core: " + cores + ", TaskNum: " + taskNum + ", Util: "
						+ ((double) util / (double) 10 * taskNum) + ", No. System " + (k + 1)
						+ " ---------------------");
				long[] r = runOne(cores, taskNum, i, instanceNum, print, seed, rng);
				res.get(index).add(r);
				seed++;
			}

			index++;
		}

		getAllResults("utils", res);

	}

	public static void changeCoreNum(int startCore, int endCore) {
		taskNum = 7;

		List<List<long[]>> res = new ArrayList<>();

		for (int i = 0; i <= (endCore - startCore); i++) {
			res.add(new ArrayList<>());
		}

		int index = 0;
		for (int i = startCore; i <= endCore; i++) {
			cores = i;

			for (int k = 0; k < nos; k++) {
				rng = new Random(seed);
				System.out.println("--------------------- " + " Core: " + cores + ", TaskNum: " + taskNum
						+ ", No. System " + (k + 1) + " ---------------------");
				long[] r = runOne(cores, taskNum, instanceNum, print, seed, rng);
				res.get(index).add(r);
				seed++;
			}

			index++;
		}

		getAllResults("cores", res);
	}

	public static void changeTaskNum(int startNum, int endNum) {

		cores = 3;

		List<List<long[]>> res = new ArrayList<>();

		for (int i = 0; i <= (endNum - startNum); i++) {
			res.add(new ArrayList<>());
		}

		int index = 0;
		for (int i = startNum; i <= endNum; i++) {
			taskNum = i;

			for (int k = 0; k < nos; k++) {
				rng = new Random(seed);
				System.out.println("--------------------- " + " Core: " + cores + ", TaskNum: " + taskNum
						+ ", No. System " + (k + 1) + " ---------------------");
				long[] r = runOne(cores, taskNum, instanceNum, print, seed, rng);
				res.get(index).add(r);
				seed++;
			}

			index++;
		}

		getAllResults("tasks", res);

	}

	public static void getAllResults(String fileName, List<List<long[]>> res) {
		System.out.println("\n\n\n------------ ALL RESULTS ------------");

		String out = "";
		for (int i = 0; i < res.get(0).size(); i++) {

			for (int k = 0; k < res.size(); k++) {

				for (int j = 0; j < res.get(k).get(i).length; j++) {
					System.out.print(res.get(k).get(i)[j] + ",");
					out += res.get(k).get(i)[j] + ",";
				}

				// for (int j = 0; j < res.get(k).get(i).length; j++) {
				// System.out.print(res.get(k).get(i)[j] + "\n");
				// out += res.get(k).get(i)[j] + "\n";
				// }

			}

			System.out.println();
			out += "\n";
		}

		Utils.writeResult("result/real/" + fileName + ".txt", out);
	}

	public static void oneRun() {
		print = true;
		runOne(3, 5, 5, print, seed, rng);

		System.out.println("\n\n\n\n\n***************************************************");
		System.out.println("***************************************************");
		System.out.println("***************************************************\n\n\n\n\n");

		runOne(4, 5, 5, print, seed, rng);

		System.out.println("\n\n\n\n\n***************************************************");
		System.out.println("***************************************************");
		System.out.println("***************************************************\n\n\n\n\n");

		runOne(5, 5, 5, print, seed, rng);
	}

	public static long[] runOne(int cores, int taskNum, int instanceNum, boolean print, int seed, Random rng) {

		return runOne(cores, taskNum, util, instanceNum, print, seed, rng);
	}

	public static long[] runOne(int cores, int taskNum, int util, int instanceNum, boolean print, int seed,
			Random rng) {

		List<String> taskName = new ArrayList<>();
		for (int i = 0; i < taskNum; i++) {
			taskName.add(tasks[i]);
		}

		CacheHierarchy cache = generateCache(cores);
		List<RecencyProfileReal> crps = readCRP(crpFile, taskName, cache);

		List<DirectedAcyclicGraph> dags = dagGenerator(crps.subList(0, taskNum), cache, cores, instanceNum, rng);

		return simulate(taskNum, dags, cache, cores, print, rng);
	}

	public static long[] simulate(int taskNum, List<DirectedAcyclicGraph> dags, CacheHierarchy cache, int cores,
			boolean print, Random rng) {

		for (DirectedAcyclicGraph d : dags) {
			// for (Node n : d.getFlatNodes())
			// n.hasFaults = true;

			// d.releaseTime = 0;
		}

		List<DirectedAcyclicGraph> tasks = new ArrayList<>(dags);
		// for (int i = 0; i < instanceNum; i++) {
		// for (int j = 0; j < taskNum; j++) {
		// tasks.add(dags.get(j * instanceNum + i));
		// }
		// // tasks.add(dags.get(10 + i));
		// // tasks.add(dags.get(20 + i));
		// // tasks.add(dags.get(40 + i));
		// // tasks.add(dags.get(60 + i));
		// // tasks.add(dags.get(80 + i));
		// }

		OnlineCacheAware_ForCompare.useSynth = true;
		rng = new Random(seed);
		Simualtor sim1 = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.CACHE_AWARE_COMPARE,
				RecencyType.TIME_DEFAULT, tasks, cache, cores, seed, licf);
		Pair<List<DirectedAcyclicGraph>, double[]> pair1 = sim1.simulate(print);

		OnlineCacheAware_ForCompare.useSynth = false;
		rng = new Random(seed);
		Simualtor sim2 = new Simualtor(SimuType.CLOCK_LEVEL, Hardware.PROC_CACHE, Allocation.CACHE_AWARE_COMPARE,
				RecencyType.TIME_DEFAULT, tasks, cache, cores, seed, licf);
		Pair<List<DirectedAcyclicGraph>, double[]> pair2 = sim2.simulate(print);


		// rng = new Random(seed);
		// Simualtor sim4 = new Simualtor(SimuType.CLOCK_LEVEL,
		// Hardware.PROC_CACHE, Allocation.RANDOM,
		// RecencyType.TIME_DEFAULT, tasks, cache, cores, seed, licf);
		// Pair<List<DirectedAcyclicGraph>, double[]> pair4 =
		// sim4.simulate(print);

		// pair1.getFirst().stream().forEach(c -> System.out.print(c.finishTime
		// - c.startTime + " "));
		// System.out.println();
		// pair2.getFirst().stream().forEach(c -> System.out.print(c.finishTime
		// - c.startTime + " "));

		sim1.totalMakespan = pair1.getFirst().stream().mapToLong(c -> c.finishTime - c.startTime).sum();
		sim2.totalMakespan = pair2.getFirst().stream().mapToLong(c -> c.finishTime - c.startTime).sum();

		System.out.println(Arrays.toString(pair1.getSecond()));
		System.out.println(Arrays.toString(pair2.getSecond()));

		System.out.println(sim1.totalMakespan + " " + sim2.totalMakespan);

		long[] makespan = { sim1.totalMakespan, sim2.totalMakespan};
		return makespan;
	}

	public static List<RecencyProfileReal> readCRP(String filename, List<String> taskNames, CacheHierarchy cache) {
		List<RecencyProfileReal> crps = Utils.readJson(filename, taskNames, cache);
		return crps;
	}

	public static CacheHierarchy generateCache(int cores) {

		List<int[]> level2 = new ArrayList<>();

		for (int i = 0; i < cores; i++) {
			int[] cluster = { i };
			level2.add(cluster);
		}

		CacheHierarchy cache = new CacheHierarchy(cores, 3, level2);

		return cache;
	}

	public static List<DirectedAcyclicGraph> dagGenerator(List<RecencyProfileReal> crps, CacheHierarchy cache,
			int cores, int instanceNum, Random rng) {

		crps.stream().forEach(c -> c.WCET = c.WCET * (1 + rng.nextDouble() * 0.2 - 0.1) * 2);
		// crps.stream().forEach(c -> c.medainET = c.WCET * (1 +
		// rng.nextDouble() * 0.2 - 0.1 ));
		crps.stream().forEach(c -> c.medainET = c.medainET * (1 + rng.nextDouble() * 0.2 - 0.1) * 2);

		List<Long> wcets = crps.stream().map(c -> (long) Math.round(c.WCET)).collect(Collectors.toList());
		List<Integer> priorities = crps.stream().map(c -> 1000).collect(Collectors.toList());
		List<Long> periods = crps.stream().map(c -> (long) Math.round((double) c.WCET / ((double) util / (double) 10)))
				.collect(Collectors.toList());

		List<Integer> instances = null;

		if (util > 0) {
			instances = new ArrayList<>();

			if (taskNum == 1) {
				instances.add(3);
				instances.add(3);
				instances.add(1);
				instances.add(23);
				instances.add(5);
			} else {
				for (Long t : periods) {
					int ins = (int) Math.ceil((double) totalDuration / (double) t);
					instances.add(ins);
				}

				System.out.println(Arrays.toString(instances.toArray()));
			}
		}

		SystemGenerator gen = new SystemGenerator(cores, crps.size(), false, false, null, rng, false, false,
				cache.level2);
		List<DirectedAcyclicGraph> sys = gen.generatedForSteven(wcets, periods, priorities, crps, cache, instanceNum,
				instances);

		return sys;
	}

}
