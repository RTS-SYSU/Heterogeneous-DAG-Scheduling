

taskNum = 1;

% methodNames = strcat(methods, " level 1 miss");

methodName1 = strcat(methods, " - core");
methodName2 = strcat(methods, " - cluster");
methodNames = strings(length(methods));
for i = 0 : length(methods)-1
    methodNames(i*2+1) = methodName1(i+1);
    methodNames(i*2+2) = methodName2(i+1);
end

for task = 1 : taskNum
    f=figure('Position', [100, 100, wid, len]);
    set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
    
    
    instanceNo = readmatrix(strcat('oneDAG/','instanceNum_',num2str(task), '_2.0', '.txt'));
    taskParam = readmatrix(strcat('oneDAG/','taskparam_',num2str(task), '_2.0', '.txt'));
    cachePerf = readmatrix(strcat('oneDAG/','cache_',num2str(task), '_2.0', '.txt'));
    
    taskWCET = taskParam(:,1);
    
    colsNum = size(cachePerf,2) - 1;
    rowsNum = size(cachePerf,1);
    
    methodNum = rowsNum/systemNoLarge;
    dataByMethod = cell(1,methodNum);
    
    % get data by each method
    for m = 1: methodNum
        startIndex = 1 + (m-1) * systemNoLarge;
        endIndex = m * systemNoLarge;
        dataByMethod{m} = cachePerf(startIndex :endIndex,:);
    end
   
    colors=[[0.8500 0.3250 0.0980];  [0 0.4470 0.7410];  [0.9290 0.6940 0.1250];  [0.4660, 0.6740, 0.1880];  [0.3010, 0.7450, 0.9330];  [0.6350, 0.0780, 0.1840]];
    

    % plot all data by boxplot
%     for m=1:methodNum
        datam1 = dataByMethod{1}(:,1:colsNum);
       
        data = [taskWCET/1000, datam1(1: length(taskWCET),1)];
        
        plot(taskWCET/1000, 1-datam1(:,1),'x','MarkerSize',5,'color',colors((1-1)*2+1,:) );
        hold on;
        plot(taskWCET/1000, 1-datam1(:,1)-datam1(:,2),'.','MarkerSize',8,'color',colors((1-1)*2+2,:) );
        hold on;
        
        %%%%%%%%%%%%%%%%%%%%%%%%%%
        
        datam2 = dataByMethod{2}(:,1:colsNum);
       
        data = [taskWCET/1000, datam2(1: length(taskWCET),1)];
        
        plot(taskWCET/1000, 1-datam2(:,1),'x','MarkerSize',5,'color',colors((2-1)*2+1,:) );
        hold on;

        plot(taskWCET/1000, 1-datam2(:,1)-datam2(:,2),'.','MarkerSize',8,'color',colors((2-1)*2+2,:) );
        hold on;
%     end
    
    
    if(xlim_value>0)
        xlim([0, xlim_value]);
    end
    ylim([-0.15, 1.1]);
    
    ax = gca;
    ax.FontSize = 12; 
    
    xlabel({'Workload of the DAG task'},'FontSize', 14)
    ylabel('Recency miss rate','FontSize', 14)
    
    
%     hCopy = copyobj(h, ax); 
%     set(hCopy(1),'XData', NaN', 'YData', NaN)
%     set(hCopy(2),'XData', NaN', 'YData', NaN)
%     set(hCopy(3),'XData', NaN', 'YData', NaN)
%     set(hCopy(4),'XData', NaN', 'YData', NaN)
%     
%     hCopy(1).MarkerSize = 20; 
%     hCopy(2).MarkerSize = 20; 
%     hCopy(3).MarkerSize = 20; 
%     hCopy(4).MarkerSize = 20; 
  
    h=legend(methodNames,'location','southeast','Orientation','horizontal');
    set(h,'FontSize',12);
    
%     ch = findobj(get(h,'children'), 'type', 'line');
%     set(ch, 'Markersize', 20);
    
    
    saveas(gcf,strcat('figs/ep_recnecy_miss_rate.eps'), 'epsc');
end
