package uk.ac.york.mocha.simulator.allocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.york.mocha.simulator.entity.DirectedAcyclicGraph;
import uk.ac.york.mocha.simulator.entity.Node;
import uk.ac.york.mocha.simulator.simulator.Utils;

public class OnlineHSF extends AllocationMethods {

	@Override
	public void allocate(List<DirectedAcyclicGraph> dags, List<Node> readyNodes, List<List<Node>> localRunqueue,
			List<Integer> availableProcs, long[] procs, List<List<Node>> history_level1,
			List<List<Node>> history_level2, List<Node> history_level3, List<List<Node>> allocHistory,
			long currentTime, boolean affinity, List<Node> etHist, List<Double> speeds, Node[] currentExe) {

		if (readyNodes.size() == 0 || availableProcs.size() == 0)
			return;

		/*
		 * Entry for debugging a single node
		 */
		for (Node n : readyNodes) {
			if (n.getDagID() == 0 && n.getDagInstNo() == 6 && n.getId() == 7) {
				break;
			}
		}

		readyNodes.stream().forEach(c -> c.partition = -1);

		readyNodes.sort((c1, c2) -> Double.compare(c2.sensitivity, c1.sensitivity));

		// Math.max(FiveNodeAllocation.cores, FiveNodeAllocation.taskNum)
		// List<Node> preEligible = new ArrayList<>();
		// for (int i = 0; i < FiveNodeAllocation.taskNum ; i++) {
		// if (readyNodes.size() == i)
		// break;
		// preEligible.add(readyNodes.get(i));
		// }

		// preEligible.sort((c1, c2) -> Utils.compareNode(dags, c1, c2));

		List<Integer> freeProc = new ArrayList<>(availableProcs);
		// freeProc.sort((c1, c2) -> Double.compare(speeds.get(c2), speeds.get(c1)));

		/*
		 * for (int i = 0; i < availableProcs.size(); i++) {
		 * if (i >= readyNodes.size())
		 * break;
		 * 
		 * int core = freeProc.get(0);
		 * readyNodes.get(i).partition = core;
		 * freeProc.remove(freeProc.indexOf(core));
		 * }
		 */
		while (readyNodes.size() > 0) {
			/*
			 * if (readyNodes.get(0).getId() == 0) {
			 * int e = 0;
			 * }
			 * int coree = getCoreC(readyNodes, freeProc, speeds, procs, localRunqueue,
			 * currentTime, currentExe,
			 * 0);
			 */

			int core = getCoreC(readyNodes, freeProc, speeds, procs, localRunqueue,
					currentTime, currentExe,
					0);

		}
		/*
		 * int allocate = 0;
		 * while (allocate < readyNodes.size()) {
		 * if (readyNodes.get(allocate).getParent().size() == 0) {
		 * int QQ = 1;
		 * }
		 * int core = getCore_old(readyNodes.get(allocate), freeProc, speeds, procs,
		 * localRunqueue, currentTime);
		 * if (core == -1) {
		 * allocate++;
		 * continue;
		 * }
		 * readyNodes.get(allocate).partition = core;
		 * localRunqueue.get(core).add(readyNodes.get(allocate));
		 * readyNodes.remove(allocate);
		 * }
		 */

	}

	public int getCore_old(Node n, List<Integer> freeProc, List<Double> speeds, long[] procs,
			List<List<Node>> localRunqueue, long currentTime) {

		int partition = -1;
		double EFT = -1;

		for (int i = 0; i < freeProc.size(); i++) {
			double tmp = Math.max(0, procs[i] - currentTime) + n.WCET / speeds.get(i);
			if (localRunqueue.get(i).size() > 0) {
				for (Node m : localRunqueue.get(i)) {
					tmp += m.WCET / speeds.get(i);
				}
			}
			if (partition == -1 || tmp < EFT) {
				partition = i;
				EFT = tmp;
			}
		}

		return partition;
	}

	public int getCoreC(List<Node> readyNodes, List<Integer> freeProc, List<Double> speeds, long[] procs,
			List<List<Node>> localRunqueue, long currentTime, Node[] currentExe, long wait) {

		int partition = -1, insert_id = -1;
		double EFT = -1;
		Node n = readyNodes.get(0);
		if (n.getId() == 0) {
			int sysp = 0;
		}

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

	/*
	 * public int getCoreIndexWithMinialWorkload(List<Integer> freeProc,
	 * List<List<Node>> history_level1) {
	 * 
	 * List<Long> accumaltedWorkload = new ArrayList<>();
	 * 
	 * for (int i = 0; i < freeProc.size(); i++) {
	 * List<Node> nodeHis = history_level1.get(freeProc.get(i));
	 * 
	 * long accumated = nodeHis.stream().mapToLong(c -> c.finishAt - c.start).sum();
	 * 
	 * accumaltedWorkload.add(accumated);
	 * }
	 * 
	 * long minWorkload = Collections.min(accumaltedWorkload);
	 * int coreIndex = accumaltedWorkload.indexOf(minWorkload);
	 * 
	 * return freeProc.get(coreIndex);
	 * }
	 */

}
