
taskNum = 1;

for task = 1 : taskNum
    f=figure('Position', [100, 100, wid, len]);
    set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
    
    data = readmatrix(strcat('oneDAG/',metric ,num2str(task), '_2.0', '.txt'));
    instanceNo = readmatrix(strcat('oneDAG/','instanceNum_',num2str(task), '_2.0', '.txt'));
    taskParam = readmatrix(strcat('oneDAG/','taskparam_',num2str(task), '_2.0', '.txt'));
    
    taskWCET = taskParam(:,1);
    
    colsNum = size(data,2) - 1;
    rowsNum = size(data,1);
    
    methodNum = rowsNum/systemNoLarge;
    dataByMethod = cell(1,methodNum);
    
    % get data by each method
    for m = 1: methodNum
        startIndex = 1 + (m-1) * systemNoLarge;
        endIndex = m * systemNoLarge;
        dataByMethod{m} = data(startIndex :endIndex,:);
    end
    
    
    
    % plot all data by boxplot
    for m=1:methodNum
        datam = dataByMethod{m}(:,1:colsNum);
        
        datam_media = zeros(1);
        
        for row = 1:size(datam,1)
            datam_media(row) = median(datam(row,:));
        end
      
        datam_media_col = datam_media';
        h1 = plot(taskWCET/1000,datam_media_col,'o','MarkerSize',3,'color',colors(m,:));
        hold on;
    end
 

    if(xlim_value>0)
        xlim([0, xlim_value]);
    end
    
    ax = gca;
    ax.FontSize = 12; 
    
    xlabel({'Workload of the DAG task'},'FontSize', 14)
    ylim([0, 1.0]);
    ylabel('Normalised makespan','FontSize', 14)
    
    h=legend(methods,'FontAngle','italic','location','southeast','Orientation','horizontal');
    set(h,'FontSize',14);

    
    saveas(gcf,strcat('figs/ep_recency_scatter.eps'), 'epsc');
end
