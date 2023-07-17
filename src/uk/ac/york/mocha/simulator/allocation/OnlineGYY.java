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
            List<Integer> availableProcs, long[] procs, List<List<Node>> history_level1,
            List<List<Node>> history_level2, List<Node> history_level3, List<List<Node>> allocHistory,
            long currentTime, boolean affinity, List<Node> etHist, List<Double> speeds) {
        /*
         * localRunqueue: size = m
         * availableProcs: record the available cores' id
         * procs: the next available time of each processor, size = m
         */

        // no need to allocate at this time step
        if (readyNodes.size() == 0 || availableProcs.size() == 0)
            return;

        // init the partition to -1
        readyNodes.stream().forEach(c -> c.partition = -1);

        /*
         * sort ready nodes
         * higher sensitivity first
         */
        readyNodes.sort((c1, c2) -> Double.compare(c2.sensitivity, c1.sensitivity));

        List<Integer> freeProc = new ArrayList<>(availableProcs);
        List<Integer> futureProc = new ArrayList<>();
        List<Node> futureNodes = new ArrayList<>();

        /*
         * current ready: readyNodes
         * current cores: freeProc
         * future cores: futureProc
         * future nodes: futureNodes
         */

        // find the median time
        int busyNum = localRunqueue.size() - availableProcs.size();
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

        // match TO DO here
        int cnt = 0;
        while (readyNodes.size() > cnt && freeProc.size() > 0) {
            Node tmp;
            // boolean node_situ = false, pros_situ = false;
            if (futureNodes.size() > 0 && futureNodes.get(0).sensitivity > readyNodes.get(0).sensitivity) {
                // chose the future node
                tmp = futureNodes.get(0);
            } else {
                // chose the current node
                tmp = readyNodes.get(0);
            }

            // chose the processor
            if (futureProc.size() > 0 && speeds.get(futureProc.get(0)) > speeds.get(freeProc.get(0))) {
                // chose the future processor
                if (tmp.getId() == readyNodes.get(0).getId()) {
                    // current - future
                } else {
                    // future - future
                }
            } else {
                // chose the current processor
                if (tmp.getId() == readyNodes.get(0).getId()) {
                    // current - current
                    tmp.partition = freeProc.get(0);
                    freeProc.remove(0);
                } else {
                    // future - current
                }
            }
        }
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