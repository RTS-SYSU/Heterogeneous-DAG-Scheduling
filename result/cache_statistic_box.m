% close all;

folder = 'makespan_';
taskNum = 1;
systemPerMethod = 2000;
colors=['r','b','k'];

comparingMethod=['Worst-fit','Cache-aware online'];

for task = 1 : taskNum
    f=figure('Position', [100, 100, 1200, 400]);
    set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
    
    instanceNo = readmatrix(strcat('oneDAG/','instanceNum_',num2str(task), '.txt'));
    taskParam = readmatrix(strcat('oneDAG/','taskparam_',num2str(task), '.txt'));
    cachePerf = readmatrix(strcat('oneDAG/','cache_',num2str(task), '.txt'));
    
    taskWCET = taskParam(:,1);
    
    colsNum = 2;
    rowsNum = size(cachePerf,1);
    
    methodNum = rowsNum/systemPerMethod;
    dataByMethod = cell(1,methodNum);
    
    % get data by each method
    for m = 1: methodNum
        startIndex = 1 + (m-1) * systemPerMethod;
        endIndex = m * systemPerMethod;
        dataByMethod{m} = cachePerf(startIndex :endIndex,:);
    end
    
    statics = zeros(1);
    
    % plot all data by boxplot
%     for m=1:methodNum
        datam1 = dataByMethod{1}(:,1:colsNum);
        datam2 = dataByMethod{2}(:,1:colsNum);
        
        
        data_mean = [mean(datam1(:,1)),  mean(datam2(:,1)), mean(datam1(:,1)+datam1(:,2)), mean(datam2(:,1)+datam2(:,2))];
        data_median = [median(datam1(:,1)), median(datam2(:,1)), median(datam1(:,1)+datam1(:,2)), median(datam2(:,1)+datam2(:,2))];
        data_min = [min(datam1(:,1)), min(datam2(:,1)),  min(datam1(:,1)+datam1(:,2)), min(datam2(:,1)+datam2(:,2))];
        data_max = [max(datam1(:,1)), max(datam2(:,1)), max(datam1(:,1)+datam1(:,2)),  max(datam2(:,1)+datam2(:,2))];
       
        disp(data_mean)
        disp(data_median)
        disp(data_min)
        disp(data_max)
        
        data = [datam1(:,1), datam2(:,1), datam1(:,1)+datam1(:,2), datam2(:,1)+datam2(:,2)];
        boxplot(data);
        
%         boxplot([datam1(:,1), datam2(:,1),datam1(:,1)+datam1(:,2), datam2(:,1)+datam2(:,2)] , 'color', colors(m));
%         hold on;
%         boxplot([datam2(:,1)+datam1(:,2), datam2(:,1)+datam1(:,2)], 'position', [3,4], 'symbol','', 'color', colors(m));
%         hold on;
%         pos = zeros(1);
%         for col = 1:colsNum
%             pos(col) = (col-1) * methodNum + m+1;
%         end
        
       
     
        
%     end
    
    
%     xlim([0, 400]);
%     xlabel({'total WCET of the DAG task'},'FontSize', 14)
%     ylim([0, 1.0]);
%     ylabel('Level 1 cache hit rate','FontSize', 14)
%     
%     ax = gca;
%     ax.FontSize = 14; 
%   
%     h=legend('Worst-Fit','Cache-Aware','location','northeast');
%     set(h,'FontSize',12);
%     
%     saveas(gcf,strcat('oneDAG/ep_cache.eps'), 'epsc');
end

% close all
