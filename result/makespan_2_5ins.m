

for task = 2
    f=figure('Position', [100, 100, wid, len]);
    set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
    
    data = readmatrix(strcat('taskNum/',metric ,num2str(task), '_2.0', '.txt'));
    instanceNo = readmatrix(strcat('taskNum/','instanceNum_',num2str(task), '_2.0', '.txt'));
    
%     data = readmatrix(strcat('offline_multi/',metric ,num2str(task), '_0.8','_TIME_DEFAULT', '.txt'));
%     instanceNo = readmatrix(strcat('offline_multi/','instanceNum_',num2str(task), '_0.8','_TIME_DEFAULT', '.txt'));
    
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
        
        boxplot(datam(:,1:4), 'position', pos(:,1:4), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;
        boxplot(datam(:,10), 'position', pos(:,5), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;
        
        boxplot(datam(:,11:14), 'position', pos(:,6:9), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;
        boxplot(datam(:,20), 'position', pos(:,10), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;
        
        if(task > 2 )
            boxplot(datam(:,21:24), 'position', pos(:,11:14), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
            hold on;
            boxplot(datam(:,30), 'position', pos(:,15), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
            hold on;
        end
        if(task > 3)
            boxplot(datam(:,31:34), 'position', pos(:,16:19), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
            hold on;
            boxplot(datam(:,40), 'position', pos(:,20), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
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
        
        for col = 1:instanceNumber
            xticks(counter) = xTick_start + xTick_space * (counter-1);
            if(col == 3)
                 xticklables(counter) = strcat({'     '}, num2str(col),{ '     '});
                 xticksecondlables(counter) = strcat('DAG-',num2str(taskNo));
                 
                 if(col == 5)
                     xticklables(counter) = strcat({'     '}, num2str(10),{ '     '});
                 end
            else
                 xticklables(counter) = num2str(col);
                 xticksecondlables(counter) = strcat('');
                 if(col == 5)
                     xticklables(counter) = strcat({'     '}, num2str(10),{ '     '});
                 end
            end
            counter=counter+1;
        end
        
         
    end
    
    xtickLableArray = [xticklables;xticksecondlables ];
    fullXtickLabels = strtrim(sprintf('%s\\newline%s\n', xtickLableArray{:}));
   
    xlim([0 colsNum/2*methodNum+1]);
    set(gca,'xtick',xticks );
        
    vertical_line_x = zeros(1);
    for i = 1 : task
        if(i > 1)
            segment = (colsNum/2*methodNum) / task;
            
            for j = 1 : task -1
                vertical_line_x(j) = segment * j;
                x_axis = segment * j + 0.5;
                line([x_axis x_axis], [0 1.1],'LineStyle',':','color','k','LineWidth',1);
            end
            
            
        end
    end
    
    ylim([0, 1.05]);
    
    ax = gca(); 
    ax.TickLabelInterpreter = 'tex';
    ax.FontSize = 12; 
    
    if task == 1
        set(gca,'xticklabel',xticklables);
         xlabel({'Instances of DAG task'},'FontSize', 14) 
%          'DAG-1';
    else
        set(gca,'xticklabel',fullXtickLabels);
         xlabel({'Instances of DAG tasks'},'FontSize', 14)
    end
    ylabel('Normalised makespan','FontSize', 14)
    
    c = findobj(gca,'Tag','Box');
    
    legendEle = zeros(1);
    for i = 0 : methodNum-1
        legendEle(i+1) = 1 + 10 * i * task;
    end
    legendEle =  sort(legendEle,'descend');

    
    
    saveas(gcf,strcat('figs/','ep_',metric,num2str(task),'.eps'), 'epsc');
end
