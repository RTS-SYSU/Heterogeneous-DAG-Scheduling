

for task = 3 : 4
    f=figure('Position', [100, 100, wid, len]);
    set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
    
    data = readmatrix(strcat('taskNum/',metric ,num2str(task), '_2.0', '.txt'));
    instanceNo = readmatrix(strcat('taskNum/','instanceNum_',num2str(task), '_2.0', '.txt'));
    
%     data = readmatrix(strcat('offline_multi/',metric ,num2str(task), '_2.0','_TIME_DEFAULT', '.txt'));
%     instanceNo = readmatrix(strcat('offline_multi/','instanceNum_',num2str(task), '_2.0','_TIME_DEFAULT', '.txt'));
    
    colsNum = size(data,2) - 1;
    rowsNum = size(data,1);
    
    methodNum = rowsNum/systemNo;
    dataByMethod = cell(1,methodNum);
    
    % get data by each method
    for m = 1: methodNum
        startIndex = 1 + (m-1) * systemNo;
        endIndex = m * systemNo;
        dataByMethod{m} = data(startIndex :endIndex,:);
    end
    
    % plot all data by boxplot
    for m=1:methodNum
        datam = dataByMethod{m};
        
        pos = zeros(1);
        for col = 1:colsNum
            pos(col) = (col-1) * methodNum + m;
        end
        
        boxplot(datam(:,1:3), 'position', pos(:,1:3), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;
        boxplot(datam(:,10), 'position', pos(:,4), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;
        
        boxplot(datam(:,11:13), 'position', pos(:,5:7), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;
        boxplot(datam(:,20), 'position', pos(:,8), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;
        
        boxplot(datam(:,21:23), 'position', pos(:,9:11), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;
        boxplot(datam(:,30), 'position', pos(:,12), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;

        if(task > 3)
            boxplot(datam(:,31:33), 'position', pos(:,13:15), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
            hold on;
            boxplot(datam(:,40), 'position', pos(:,16), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
            hold on;
        end
    end
    
    ho = findobj(gcf,'tag','Outliers');
    set(ho,'MarkerSize',1)
    
    xTick_start = sum(1:methodNum) /methodNum ;
    xTick_space = methodNum;
    xticks = zeros(1);
    xticklables = strings(1);
    xticksecondlables = strings(1);
    
    % add xticks, xticklables.
    counter = 1;
    for taskNo = 1 : length(instanceNo)
        instanceNumber = instanceNo(taskNo)/2;
        
        for col = 1:4
            xticks(counter) = xTick_start + xTick_space * (counter-1);
            if(col == 2)
                 xticklables(counter) = strcat({'     '}, num2str(col),{ '     '});
                 xticksecondlables(counter) = strcat('DAG-',num2str(taskNo));
                 
                 if(col == 4)
                     xticklables(counter) = strcat({'     '}, num2str(10),{ '     '});
                 end
            else
                 xticklables(counter) = num2str(col);
                 xticksecondlables(counter) = strcat('');
                 if(col == 4)
                     xticklables(counter) = strcat({'     '}, num2str(10),{ '     '});
                 end
            end
            counter=counter+1;
        end
        
         
    end
    
    xtickLableArray = [xticklables;xticksecondlables ];
    fullXtickLabels = strtrim(sprintf('%s\\newline%s\n', xtickLableArray{:}));
   
    xlim([0 4*task*methodNum+1]);
    set(gca,'xtick',xticks );
    
    
    line([8.5 8.5], [0 1.1],'LineStyle',':','color','k','LineWidth',1);
    line([16.5 16.5], [0 1.1],'LineStyle',':','color','k','LineWidth',1);
    if(task == 4)
        line([24.5 24.5], [0 1.1],'LineStyle',':','color','k','LineWidth',1);
    end
%     vertical_line_x = zeros(1);
%     for i = 1 : task
%         if(i > 1)
%             segment = (colsNum/2*methodNum) / task;
%             
%             for j = 1 : task -1
%                 vertical_line_x(j) = segment * j;
%                 x_axis = segment * j + 0.5;
%                 line([x_axis x_axis], [0 1.1],'LineStyle',':','color','k','LineWidth',1);
%             end
%             
%             
%         end
%     end
    
    ylim([0, 1.05]);
    
    ax = gca(); 
    ax.TickLabelInterpreter = 'tex';
    ax.FontSize = 12; 
    
    if task == 3
        set(gca,'xticklabel',xticklables);
         xlabel({'\fontsize{12}DAG-1                                 DAG-2                                 DAG-3';'\fontsize{14}Instances of DAG task'}) 
%          'DAG-1';
    else
        set(gca,'xticklabel',xticklables);
          xlabel({'\fontsize{12}DAG-1                         DAG-2                    DAG-3                         DAG-4';'\fontsize{14}Instances of DAG task'}) 
    end
    ylabel('Normalised makespan','FontSize', 14)
    
    c = findobj(gca,'Tag','Box');
    
    legendEle = zeros(1);
    for i = 0 : methodNum-1
        legendEle(i+1) = 1 + 10 * i * task;
    end
    legendEle =  sort(legendEle,'descend');

    
    
    saveas(gcf,strcat('figs/','ep_',metric,num2str(task),'.eps'), 'epsc');
    saveas(gcf,strcat('figs/','ep_',metric,num2str(task),'.png'));
end
