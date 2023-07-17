
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
        
        pos = zeros(1);
        for col = 1:colsNum
            pos(col) = (col-1) * methodNum + m;
        end
        
        boxplot(datam(:,1), 'position', pos(1), 'widths' ,0.65, 'symbol', '', 'color', colors(m,:));
          hold on;
        boxplot(datam(:,2:colsNum), 'position', pos(:,2:colsNum), 'widths' ,0.65, 'symbol', '.', 'color', colors(m,:));
      
        hold on;
    end
 
   
    xticks = sum(1:methodNum)/methodNum : methodNum : instanceNo * methodNum;
    xticklables = 1:10
%     xticks = [1.5, 3.5, 5.5, 7.5, 9.5, 11.5, 13.5, 15.5,17.5, 19.5]
%     xticks = [2, 5, 8, 11, 14, 17, 20, 23,26, 29];
    set(gca,'xtick',xticks );
    set(gca,'xticklabel',xticklables);
    
    ax = gca;
    ax.FontSize = 12; 
    
    
    xlim([0.5, methodNum * colsNum + 1 ]);
    xlabel({'Instance index of the DAG'},'FontSize', 14)
    ylim([0, 1.02]);
    ylabel('Normalised makespan','FontSize', 14)
    
    c = findobj(gca,'Tag','Box');
    
    legendEle = zeros(1);
    for i = 0 : methodNum-1
        legendEle(i+1) = 1 + 10 * i;
    end
    legendEle =  sort(legendEle,'descend');
    

    
    h=legend(c(legendEle),methods,'FontAngle','italic','location','southeast','Orientation','horizontal');
    set(h,'FontSize',14);
    

    
    saveas(gcf,strcat('figs/ep_recency_box.eps'), 'epsc');
%     saveas(gcf,strcat('oneDAG/ep_recency_box.pdf'));
end
