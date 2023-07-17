close all

% colors=[[0.8500 0.3250 0.0980]; [0 0.4470 0.7410];  [0.9290 0.6940 0.1250]; [0.4660, 0.6740, 0.1880]; [0.3010, 0.7450, 0.9330]; [0.6350, 0.0780, 0.1840]];
colors=["b"; "r"];
types = ["all","nodeET","pathET","in_degree","out_degree","in_out_degree","pathNum","sensivitiy","sensivitiy*"];
% types = ["nodeET","pathET","in\_degree","out\_degree","in\_out\_degree","pathNum"];

% types = ["all","nodeET","pathET","in_degree","out_degree","in_out_degree","pathNum","basic sensivitity","K-S based sensivitiy","C-C based sensivitiy*"];

ignoredType = [];

cores = [4];
percents = [0.1,0.2,0.3,0.4,0.5];
% effects = ["0.2","0.4","0.6","0.8","1.0"];
effects = ["0.1","0.2","0.3","0.4","0.5"];
instanceNums = [1,3,5,10];

row = 2;
col = 3;

% effectsNum = 200:200:1000;
% effects = 200:200:1000;

effectsNum = 0:5:1000;
effects = 0:5:1000;

% cc1;

% sens_scatter_faults_variation_hist
scatter_faults_variation_hist;

% faults_variation_hist

% faults_allNodes;
% faults_variation_hist;
% faults_sensitivity;