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

public class OnlineGYY_WCET extends AllocationMethods {

    @Override
    public void allocate(List<DirectedAcyclicGraph> dags, List<Node> readyNodes, List<List<Node>> localRunqueue,
            List<Integer> cores, long[] procs, List<List<Node>> history_level1,
            List<List<Node>> history_level2, List<Node> history_level3, List<List<Node>> allocHistory,
            long currentTime, boolean affinity, List<Node> etHist, List<Double> speeds, Node[] currentExe) {
        /*
         * localRunqueue: size = m
         * procs: the next available time of each processor, size = m
         */

        List<Integer> freeProc = new ArrayList<>();
        for (int i = 0; i < cores.size(); i++) {
            // find the available cores
            // no local queue or has but in the future, has margin for current task
            if (currentExe[i] == null && (localRunqueue.get(i).size() == 0
                    || (localRunqueue.get(i).size() > 0 && localRunqueue.get(i).get(0).start > currentTime))
                    && procs[i] <= currentTime) {
                freeProc.add(i);
            }
        }
        // higher speed core first
        freeProc.sort((c1, c2) -> Double.compare(speeds.get(c2), speeds.get(c1)));

        // no need to allocate at this time step
        if (readyNodes.size() == 0 || freeProc.size() == 0)
            return;

        // init the partition to -1
        readyNodes.stream().forEach(c -> c.partition = -1);

        /*
         * sort ready nodes
         * higher WCET first
         */
        readyNodes.sort((c1, c2) -> Double.compare(c2.WCET, c1.WCET));

        List<Integer> futureProc = new ArrayList<>();
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
            if (procs[i] > currentTime) {
                futureProc.add(i);
                for (Node w : localRunqueue.get(i)) {
                    if (w.start > procs[i])
                        break;
                    else
                        nodesTobedone.add(w);
                }
                if (currentExe[i] != null && currentExe[i].finishAt <= procs[i]) {
                    nodesTobedone.add(currentExe[i]);
                }
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
         * higher WCET first
         */
        futureNodes.sort((c1, c2) -> Double.compare(c2.WCET, c1.WCET));

        // List<Node> current_future = new ArrayList<>();
        int not_allocate = 0;// index to current node, can't be allocated at this time number
        while (readyNodes.size() > not_allocate && freeProc.size() > 0) {
            // node - processor
            if (is_future_node(readyNodes, futureNodes, not_allocate)) {
                // future node

                // only considered current processor, whether it should be booked or not
                int id_current = 0;
                // find the current processor can run the task
                while (id_current < freeProc.size()) {
                    int core = freeProc.get(id_current);
                    if (currentExe[core] == null && (localRunqueue.get(core).size() == 0
                            || (localRunqueue.get(core).size() > 0 && get_available_time_for_fake(core,
                                    localRunqueue, currentTime) >= id_to_waiting.get(futureNodes.get(0).getId())
                                            + futureNodes.get(0).WCET / speeds.get(core)))) {
                        break;
                    } else
                        id_current++;
                }
                if (id_current < freeProc.size()) {
                    // can be allocated
                    int core = freeProc.get(id_current);
                    futureNodes.get(0).partition = core;
                    futureNodes.get(0).start = id_to_waiting.get(futureNodes.get(0).getId());
                    double DrealET = Math.max(futureNodes.get(0).WCET / speeds.get(core), 1);
                    long realET = (long) DrealET;
                    futureNodes.get(0).finishAt = futureNodes.get(0).start + realET;
                    localRunqueue.get(core).add(futureNodes.get(0));
                }
                futureNodes.remove(0);

            } else {
                // current node
                int id_current = 0, id_future = 0;
                boolean flag = false;
                while (id_current < freeProc.size() || id_future < futureProc.size()) {
                    if (is_future_processor(freeProc, futureProc, speeds, id_current, id_future)) {
                        // current - future
                        // only consider current node
                        /*
                         * // record first, may refine later
                         * current_future.add(readyNodes.get(not_allocate));
                         * readyNodes.get(not_allocate).partition = futureProc.get(0);
                         * // remove
                         * futureProc.remove(id_future);
                         * readyNodes.remove(not_allocate);
                         * flag = true;
                         */
                        // find the target current to current node if exists
                        while (id_current < freeProc.size()
                                && (localRunqueue.get(freeProc.get(id_current)).size() == 0 ||
                                        get_available_time_for_fake(freeProc.get(id_current), localRunqueue,
                                                currentTime) < readyNodes.get(not_allocate).WCET
                                                        / speeds.get(freeProc.get(id_current)))) {
                            id_current++;
                        }
                        if (id_current == freeProc.size()) {
                            break;
                        }
                        // compare
                        long wcet_tmp = readyNodes.get(not_allocate).WCET;
                        int core_cur = freeProc.get(id_current), core_fur = futureProc.get(id_future);

                        if (currentTime + wcet_tmp / speeds.get(core_cur) > procs[core_fur]
                                + wcet_tmp / speeds.get(core_fur)) {
                            // allocate to future
                            readyNodes.get(not_allocate).partition = core_fur;
                            readyNodes.get(not_allocate).start = procs[core_cur];
                            double DrealET = Math.max(readyNodes.get(not_allocate).WCET / speeds.get(core_fur), 1);
                            long realET = (long) DrealET;
                            readyNodes.get(not_allocate).finishAt = readyNodes.get(not_allocate).start + realET;
                            localRunqueue.get(core_fur).add(readyNodes.get(not_allocate));
                            // remove
                            readyNodes.remove(not_allocate);
                            futureProc.remove(id_future);
                        } else {
                            // allocate to current
                            readyNodes.get(not_allocate).partition = core_cur;
                            localRunqueue.get(core_cur).add(0, readyNodes.get(not_allocate));
                            // remove
                            readyNodes.remove(not_allocate);
                            freeProc.remove(id_current);
                        }
                        flag = true;
                    } else {
                        // current - current
                        int core = freeProc.get(id_current);
                        if (localRunqueue.get(core).size() > 0) {
                            // fake
                            if (get_available_time_for_fake(core, localRunqueue,
                                    currentTime) < readyNodes.get(not_allocate).WCET
                                            / speeds.get(core)) {
                                // not allocate
                                id_current++;
                                continue;
                                // next iter
                            } else {
                                // allocate
                                readyNodes.get(not_allocate).partition = core;
                                localRunqueue.get(core).add(0, readyNodes.get(not_allocate));
                                // remove
                                readyNodes.remove(not_allocate);
                                freeProc.remove(id_current);
                                flag = true;
                            }
                        } else {
                            // true
                            readyNodes.get(not_allocate).partition = core;
                            localRunqueue.get(core).add(0, readyNodes.get(not_allocate));
                            // remove
                            readyNodes.remove(not_allocate);
                            freeProc.remove(id_current);
                            flag = true;
                        }
                    }
                    if (flag)
                        break;
                }
                if (!flag)
                    not_allocate++;
            }
        }
    }

    private boolean is_future_node(List<Node> readyNodes, List<Node> futureNodes, int not_allocate) {
        // exists && higher WCET
        return readyNodes.size() > not_allocate && futureNodes.size() > 0
                && futureNodes.get(0).WCET > readyNodes.get(0).WCET;
    }

    private boolean is_future_processor(List<Integer> freeProc, List<Integer> futureProc, List<Double> speeds,
            int id_current, int id_future) {
        // exists && higher speed
        return id_current >= freeProc.size() || (id_current < freeProc.size() && futureProc.size() > id_future
                && speeds.get(futureProc.get(id_future)) > speeds.get(freeProc.get(id_current)));
    }

    private long get_available_time_for_fake(int index, List<List<Node>> localRunqueue, long currentTime) {
        return localRunqueue.get(index).get(0).start - currentTime;
    }

}