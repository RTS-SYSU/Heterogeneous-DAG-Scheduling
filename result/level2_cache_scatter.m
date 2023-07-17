

taskNum = 1;

methodNames = strcat(methods, " level 2 miss");

for task = 1 : taskNum
    f=figure('Position', [100, 100, 1200, 400]);
    set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
    
    instanceNo = readmatrix(strcat('oneDAG/','instanceNum_',num2str(task), '.txt'));
    taskParam = readmatrix(strcat('oneDAG/','taskparam_',num2str(task), '.txt'));
    cachePerf = readmatrix(strcat('oneDAG/','cache_',num2str(task), '.txt'));
    
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
    
    
    
    % plot all data by boxplot
    for m=1:methodNum
        datam = dataByMethod{m}(:,1:colsNum);
       
        data = [taskWCET/1000, datam(1: length(taskWCET),1)];
        
        

        h2 = plot(taskWCET/1000, 1-datam(:,1)-datam(:,2),'x','MarkerSize',5,'color',colors(m,:));
        hold on;
    end
    
    
    if(xlim_value>0)
        xlim([0, xlim_value]);
    end
    ylim([-0.1, 1.0]);
    
    ax = gca;
    ax.FontSize = 12; 
    
    xlabel({'Workload of the DAG task'},'FontSize', 14)
    ylabel('Recency miss rate','FontSize', 14)
  
    h=legend(methodNames,'location','southeast','Orientation','horizontal');
    set(h,'FontSize',14);
    
    saveas(gcf,strcat('figs/ep_recnecy_miss_rate.eps'), 'epsc');
end
