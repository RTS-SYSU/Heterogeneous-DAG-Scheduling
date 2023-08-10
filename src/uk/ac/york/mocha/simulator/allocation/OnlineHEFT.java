package uk.ac.york.mocha.simulator.allocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.york.mocha.simulator.entity.DirectedAcyclicGraph;
import uk.ac.york.mocha.simulator.entity.Node;
import uk.ac.york.mocha.simulator.simulator.Utils;

public class OnlineHEFT extends AllocationMethods {

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

		/*
		 * sort ready nodes list by FPS+WF, take first procNum nodes to execute.
		 */
		readyNodes.sort((c1, c2) -> Long.compare(c2.rank_down, c1.rank_down));

		// Math.max(FiveNodeAllocation.cores, FiveNodeAllocation.taskNum)
		// List<Node> preEligible = new ArrayList<>();
		// for (int i = 0; i < FiveNodeAllocation.taskNum ; i++) {
		// if (readyNodes.size() == i)
		// break;
		// preEligible.add(readyNodes.get(i));
		// }

		// preEligible.sort((c1, c2) -> Utils.compareNode(dags, c1, c2));

		List<Integer> freeProc = new ArrayList<>(availableProcs);

		// int allocate = 0;
		while (readyNodes.size() > 0) {
			int core = getCore_old(readyNodes.get(0), freeProc, speeds, procs,
					localRunqueue, currentTime);
			/*
			 * if (core == -1) {
			 * allocate++;
			 * continue;
			 * }
			 */
			readyNodes.get(0).partition = core;
			localRunqueue.get(core).add(readyNodes.get(0));
			readyNodes.remove(0);
		}

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

	public int getCore(List<Integer> freeProc, List<List<Node>> history_level1) {

		List<Long> accumaltedWorkload = new ArrayList<>();

		for (int i = 0; i < freeProc.size(); i++) {
			List<Node> nodeHis = history_level1.get(freeProc.get(i));

			long accumated = nodeHis.stream().mapToLong(c -> c.finishAt - c.start).sum();

			accumaltedWorkload.add(accumated);
		}

		long minWorkload = Collections.min(accumaltedWorkload);
		int coreIndex = accumaltedWorkload.indexOf(minWorkload);

		return freeProc.get(coreIndex);
	}

}
