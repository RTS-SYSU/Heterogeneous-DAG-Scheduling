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

public class OnlineGYY extends AllocationMethods {

    @Override
    public void allocate(List<DirectedAcyclicGraph> dags, List<Node> readyNodes, List<List<Node>> localRunqueue,
            List<Integer> cores, long[] procs, List<List<Node>> history_level1,
            List<List<Node>> history_level2, List<Node> history_level3, List<List<Node>> allocHistory,
            long currentTime, boolean affinity, List<Node> etHist, List<Double> speeds) {
        /*
         * localRunqueue: size = m
         * availableProcs: record the available cores' id
         * procs: the next available time of each processor, size = m
         */

        List<Integer> freeProc = new ArrayList<>();
        for (int i = 0; i < cores.size(); i++) {
            // find the available cores
            // no local queue or has but in the future, has margin for current task
            if ((localRunqueue.get(i).size() == 0
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
         * higher sensitivity first
         */
        readyNodes.sort((c1, c2) -> Double.compare(c2.sensitivity, c1.sensitivity));

        List<Integer> futureProc = new ArrayList<>();
        List<Node> futureNodes = new ArrayList<>();

        /*
         * current ready: readyNodes
         * current cores: freeProc
         * future cores: futureProc
         * future nodes: futureNodes
         */

        // find the median time
        int busyNum = localRunqueue.size() - freeProc.size();
        int heapsize = (busyNum + 1) / 2;
        PriorityQueue<Long> pq = new PriorityQueue<Long>((a, b) -> Long.compare(b, a));
        for (int i = 0; i < localRunqueue.size(); i++) {
            if (localRunqueue.get(i).size() > 0) {
                if (pq.size() < heapsize) {
                    pq.offer(procs[i]);
                } else {
                    if (pq.peek() > procs[i]) {
                        pq.poll();
                        pq.offer(procs[i]);
                    }
                }
            }
        }
        long medTime = pq.peek();

        HashMap<Integer, Long> id_to_waiting = new HashMap<>();
        // determine the core set based on medTime -- futureProc
        List<Node> nodesTobedone = new ArrayList<>();
        for (int i = 0; i < localRunqueue.size(); i++) {
            if (procs[i] <= medTime) {
                futureProc.add(i);
                // question here: does current running node in waiting list? now is 'yes'
                // version
                for (Node w : localRunqueue.get(i)) {
                    nodesTobedone.add(w);
                }
            }
        }
        // determine the node to be free -- futureNodes
        for (Node tmp : nodesTobedone) {
            for (Node child : tmp.getChildren()) {
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
        futureNodes.sort((c1, c2) -> Double.compare(c2.sensitivity, c1.sensitivity));

        // List<Node> current_future = new ArrayList<>();
        int not_allocate = 0;// index to current node, can't be allocated at this time number
        while (readyNodes.size() > not_allocate && freeProc.size() > 0) {
            // ndoe - processor
            if (is_future_node(readyNodes, futureNodes)) {
            } else {
                // current node
                int id_current = 0, id_future = 0;
                boolean flag = false;
                while (id_current < freeProc.size() && id_future < futureProc.size()) {
                    if (is_future_processor(freeProc, futureProc, speeds, id_current, id_future)) {
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
                                && get_available_time_for_fake(freeProc.get(id_current), localRunqueue,
                                        currentTime) < readyNodes.get(not_allocate).WCET
                                                / speeds.get(freeProc.get(id_current))) {
                            id_current++;
                        }
                        if (id_current == freeProc.size()) {
                            break;
                        }
                        // compare
                        long wcet_tmp = readyNodes.get(not_allocate).WCET;
                        int core_cur = freeProc.get(id_current), core_fur = futureProc.get(id_future);
                        if (wcet_tmp / speeds.get(core_cur) > get_available_time_for_fake(id_future, localRunqueue,
                                currentTime) + wcet_tmp / speeds.get(core_fur)) {
                            // allocate to future
                            readyNodes.get(not_allocate).partition = core_fur;
                            // remove
                            readyNodes.remove(not_allocate);
                            futureProc.remove(core_fur);
                        } else {
                            // allocate to current
                            readyNodes.get(not_allocate).partition = core_cur;
                            // remove
                            readyNodes.remove(not_allocate);
                            futureProc.remove(core_cur);
                        }
                        flag = true;
                    } else {
                        int core = freeProc.get(id_current);
                        if (localRunqueue.get(core).size() > 0) {
                            // fake
                            if (get_available_time_for_fake(core, localRunqueue,
                                    currentTime) < readyNodes.get(not_allocate).WCET
                                            / speeds.get(core)) {
                                // not allocate
                                id_current++;
                                continue;
                            } else {
                                // allocate
                                readyNodes.get(not_allocate).partition = core;
                                // remove
                                readyNodes.remove(not_allocate);
                                freeProc.remove(id_current);
                                flag = true;
                            }
                        } else {
                            // true
                            // to do: should I change the information(procs, next available time...)?
                            readyNodes.get(not_allocate).partition = core;
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

    private boolean is_future_node(List<Node> readyNodes, List<Node> futureNodes) {
        // exists && higher sensitivity
        return futureNodes.size() > 0 && futureNodes.size() > 0
                && futureNodes.get(0).sensitivity > readyNodes.get(0).sensitivity;
    }

    private boolean is_future_processor(List<Integer> freeProc, List<Integer> futureProc, List<Double> speeds,
            int id_current, int id_future) {
        // exists && higher speed
        return futureProc.size() > 0 && speeds.get(futureProc.get(0)) > speeds.get(freeProc.get(0));
    }

    private long get_available_time_for_fake(int index, List<List<Node>> localRunqueue, long currentTime) {
        return localRunqueue.get(index).get(0).start - currentTime;
    }

}
/*
 * 关于异构处理器的构建
 * 一个数组记录处理器的执行速率
 * 如何估计下一个future节点in the waiting queue的预计开始时间？ 加一个属性记一下，max（正在执行中的父节点的预计最大值）
 * 如何把暂时有空的处理器加入列表？ // simulator line 383
 */
/*
 * 处理节点的地址问题
 * 局部and全局
 * 状态变化如何返回到主函数中去
 */