package uk.ac.york.mocha.simulator.allocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Iterator;

import uk.ac.york.mocha.simulator.entity.DirectedAcyclicGraph;
import uk.ac.york.mocha.simulator.entity.Node;
import uk.ac.york.mocha.simulator.simulator.Utils;

public class Onlinegyy_test extends AllocationMethods {

	@Override
	public void allocate(List<DirectedAcyclicGraph> dags, List<Node> readyNodes, List<List<Node>> localRunqueue,
			List<Integer> cores, long[] procs, List<List<Node>> history_level1,
			List<List<Node>> history_level2, List<Node> history_level3, List<List<Node>> allocHistory,
			long currentTime, boolean affinity, List<Node> etHist, List<Double> speeds, Node[] currentExe) {
		/*
		 * localRunqueue: size = m
		 * procs: the next available time of each processor, size = m
		 */

		List<Integer> freeProc = new ArrayList<>(cores);

		// no need to allocate at this time step
		if (readyNodes.size() == 0 || freeProc.size() == 0)
			return;

		// init the partition to -1
		readyNodes.stream().forEach(c -> c.partition = -1);

		/*
		 * sort ready nodes
		 * higher sensitivity first
		 */
		// readyNodes.sort((c1, c2) -> Double.compare(c2.sensitivity, c1.sensitivity));
		readyNodes.sort((c1, c2) -> Double.compare(c2.gyy_priority, c1.gyy_priority));

		// List<Integer> futureProc = new ArrayList<>();
		List<Node> futureNodes = new ArrayList<>();

		/*
		 * current ready: readyNodes
		 * current cores: freeProc
		 * future cores: futureProc
		 * future nodes: futureNodes
		 */

		// find the median time
		/*
		 * int busyNum = localRunqueue.size() - freeProc.size();
		 * int heapsize = (busyNum + 1) / 2;
		 * PriorityQueue<Long> pq = new PriorityQueue<Long>((a, b) -> Long.compare(b,
		 * a));
		 * for (int i = 0; i < localRunqueue.size(); i++) {
		 * if (localRunqueue.get(i).size() > 0) {
		 * if (pq.size() < heapsize) {
		 * pq.offer(procs[i]);
		 * } else {
		 * if (pq.peek() > procs[i]) {
		 * pq.poll();
		 * pq.offer(procs[i]);
		 * }
		 * }
		 * }
		 * }
		 * long medTime = pq.peek();
		 */

		HashMap<Integer, Long> id_to_waiting = new HashMap<>();
		// determine the core set based on medTime -- futureProc
		List<Node> nodesTobedone = new ArrayList<>();
		for (int i = 0; i < localRunqueue.size(); i++) {
			if (currentExe[i] != null && currentExe[i].finishAt <= procs[i]) {
				nodesTobedone.add(currentExe[i]);
			}

		}
		// determine the node to be free -- futureNodes
		for (Node tmp : nodesTobedone) {
			for (Node child : tmp.getChildren()) {
				if (futureNodes.contains(child) || child.start != -1) {
					// already added
					continue;
				}
				long worst_time = tmp.finishAt;
				boolean isReady = true;
				for (Node parent : child.getParent()) {
					// haven't been finished before and would not be finished this turn
					if (!parent.finish && !nodesTobedone.contains(parent)) {
						isReady = false;
						break;
					}
					if (nodesTobedone.contains(parent)) {
						worst_time = Math.max(worst_time, parent.finishAt);
					}
				}
				if (isReady) {
					futureNodes.add(child);
					id_to_waiting.put(child.getId(), worst_time);
				}
			}
		}

		// init the partition to -1
		futureNodes.stream().forEach(c -> c.partition = -1);

		/*
		 * sort ready nodes
		 * higher sensitivity first
		 */
		// futureNodes.sort((c1, c2) -> Double.compare(c2.sensitivity, c1.sensitivity));
		futureNodes.sort((c1, c2) -> Double.compare(c2.gyy_priority, c1.gyy_priority));
		int limit = 3;
		if (futureNodes.size() > limit)
			futureNodes = futureNodes.subList(0, limit);

		while (readyNodes.size() > 0) {
			/*
			 * if (readyNodes.get(0).getId() == 0) {
			 * int e = 0;
			 * }
			 * int coree = getCoreC(readyNodes, freeProc, speeds, procs, localRunqueue,
			 * currentTime, currentExe,
			 * 0);
			 */

			if (is_future_node(readyNodes, futureNodes)) {
				int core = getCoreF(futureNodes, freeProc, speeds, procs, localRunqueue,
						currentTime, currentExe,
						id_to_waiting.get(futureNodes.get(0).getId()));
			} else {
				int core = getCoreC(readyNodes, freeProc, speeds, procs, localRunqueue,
						currentTime, currentExe,
						0);
			}

		}

	}

	private boolean is_future_node(List<Node> readyNodes, List<Node> futureNodes) {
		// exists && higher sensitivity
		/*
		 * return readyNodes.size() > not_allocate && futureNodes.size() > 0
		 * && futureNodes.get(0).sensitivity > readyNodes.get(not_allocate).sensitivity;
		 */
		if (futureNodes.size() == 0)
			return false;

		return futureNodes.get(0).gyy_priority >= readyNodes.get(0).gyy_priority;
	}

	public int getCoreC(List<Node> readyNodes, List<Integer> freeProc, List<Double> speeds, long[] procs,
			List<List<Node>> localRunqueue, long currentTime, Node[] currentExe, long wait) {

		int partition = -1, insert_id = -1;
		double EFT = -1;
		Node n = readyNodes.get(0);

		for (int i = 0; i < freeProc.size(); i++) {
			int num = localRunqueue.get(i).size();
			// double tmp = Math.max(0, procs[i] - currentTime);
			double exp = n.WCET / speeds.get(i);

			if (num == 0) {
				long l = Math.max(currentTime, Math.max((int) wait, (int) procs[i]));
				if (partition == -1 || EFT > l + exp) {
					partition = i;
					insert_id = 0;
					EFT = l + exp;
				}
				continue;
			}

			for (int j = 0; j < num; j++) {
				if (localRunqueue.get(i).get(j).start <= wait && j < num - 1)
					continue;
				if (j == 0) {
					long l = Math.max(currentTime, Math.max((int) wait, (int) procs[i]));
					if (localRunqueue.get(i).get(j).start - l >= exp) {
						// can
						if (partition == -1 || EFT > l + exp) {
							partition = i;
							insert_id = j;
							EFT = l + exp;
						}
						break;
					}
				} else {
					long l = Math.max(currentTime,
							Math.max((int) wait, (int) localRunqueue.get(i).get(j - 1).finishAt));
					if (localRunqueue.get(i).get(j).start - l >= exp) {
						// can
						if (partition == -1 || EFT > l + exp) {
							partition = i;
							insert_id = j;
							EFT = l + exp;
						}
						break;
					}

				}
				if (j == num - 1) {
					if (partition == -1
							|| EFT > Math.max(currentTime,
									Math.max((int) wait, (int) localRunqueue.get(i).get(num - 1).finishAt)) + exp) {
						partition = i;
						insert_id = num;
						EFT = Math.max(currentTime,
								Math.max((int) wait, (int) localRunqueue.get(i).get(num - 1).finishAt)) + exp;
					}
				}

			}
		}

		readyNodes.get(0).partition = partition;
		readyNodes.get(0).finishAt = (long) EFT;
		readyNodes.get(0).start = (long) EFT - Math.round(readyNodes.get(0).WCET / speeds.get(partition));
		localRunqueue.get(partition).add(insert_id, readyNodes.get(0));
		readyNodes.remove(0);

		return partition;
	}

	public int getCoreF(List<Node> futureNodes, List<Integer> freeProc, List<Double> speeds, long[] procs,
			List<List<Node>> localRunqueue, long currentTime, Node[] currentExe, long wait) {

		int partition = -1, insert_id = -1;
		double EFT = -1;
		Node n = futureNodes.get(0);

		for (int i = 0; i < freeProc.size(); i++) {
			int num = localRunqueue.get(i).size();
			// double tmp = Math.max(0, procs[i] - currentTime);
			double exp = n.WCET / speeds.get(i);

			if (num == 0) {
				long l = Math.max(currentTime, Math.max((int) wait, (int) procs[i]));
				if (partition == -1 || EFT > l + exp) {
					partition = i;
					insert_id = 0;
					EFT = l + exp;
				}
				continue;
			}

			for (int j = 0; j < num; j++) {
				if (localRunqueue.get(i).get(j).start <= wait && j < num - 1)
					continue;
				if (j == 0) {
					long l = Math.max(currentTime, Math.max((int) wait, (int) procs[i]));
					if (localRunqueue.get(i).get(j).start - l >= exp) {
						// can
						if (partition == -1 || EFT > l + exp) {
							partition = i;
							insert_id = j;
							EFT = l + exp;
						}
						break;
					}
				} else {
					long l = Math.max(currentTime,
							Math.max((int) wait, (int) localRunqueue.get(i).get(j - 1).finishAt));
					if (localRunqueue.get(i).get(j).start - l >= exp) {
						// can
						if (partition == -1 || EFT > l + exp) {
							partition = i;
							insert_id = j;
							EFT = l + exp;
						}
						break;
					}

				}
				if (j == num - 1) {
					if (partition == -1
							|| EFT > Math.max(currentTime,
									Math.max((int) wait, (int) localRunqueue.get(i).get(num - 1).finishAt)) + exp) {
						partition = i;
						insert_id = num;
						EFT = Math.max(currentTime,
								Math.max((int) wait, (int) localRunqueue.get(i).get(num - 1).finishAt)) + exp;
					}
				}

			}
		}

		futureNodes.get(0).partition = partition;
		futureNodes.get(0).finishAt = (long) EFT;
		futureNodes.get(0).start = (long) EFT - Math.round(futureNodes.get(0).WCET / speeds.get(partition));
		localRunqueue.get(partition).add(insert_id, futureNodes.get(0));
		futureNodes.remove(0);

		return partition;
	}

}