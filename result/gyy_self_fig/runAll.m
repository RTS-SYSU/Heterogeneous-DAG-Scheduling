close all;

wid = 1000;% 800
len = 300;% 300

metric = 'makespan_';
% colors=["b";"r";"g";"k"];
colors=[[0.8500 0.3250 0.0980]; [0 0.4470 0.7410];  [0.9290 0.6940 0.1250]; [0.4660, 0.6740, 0.1880]; [0.3010, 0.7450, 0.9330]; [0.6350, 0.0780, 0.1840]];
methods = ["WF+OLD";"HSF+NEW";"HSF+OLD";"WF+NEW";"SOTA";"HEFT"];
% "Worst-fit";

systemNo = 100; % 500
systemNoLarge = systemNo * 5;
xlim_value = 300;

% recency

% recency_boxplot
% recency_scatter
% level1_cache_scatter
% 
makespan_1
% makespan_2
% makespan_3to4

% util_makespan_extend

% utilOneDAG_makespan

% recency_fault
% recency_fault_util

% recency_fault_pattern
% 
% online_and_offline_taskNum

%recency_util_compare
% recency_util_compare_detail
% recency_util_compare_detail_with_outlayer

% recency_util_compare_three
% recency_util_compare_three_abs