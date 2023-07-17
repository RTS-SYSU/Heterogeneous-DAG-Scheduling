instances = [1,2,3,4,5,6,7,8,9, 10, 20, 30, 40, 50];

for task = 1:4
    f=figure('Position', [100, 100, wid, len]);
    set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
    
    data = readmatrix(strcat('sysUtil/',metric ,num2str(task), '_2.0', '.txt'));
    instanceNo = readmatrix(strcat('sysUtil/','instanceNum_',num2str(task), '_2.0', '.txt'));
    
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
        
        boxplot(datam(:,instances), 'position', pos(:,1:length(instances)), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;
        
        boxplot(datam(:,instances), 'position', pos(:,length(instances)+1:length(instances)*2), 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;
    end
    
    ho = findobj(gcf,'tag','Outliers');
    set(ho,'MarkerSize',1)
    
    xTick_start = sum(1:methodNum) /methodNum ;
    xTick_space = methodNum;
    xticks = zeros(1);
    xticklables = strings(1);
    
    
    % add xticks, xticklables.
    counter = 1;
    for taskNo = 1 : length(instanceNo)
        instanceNumber = length(instances);
        
        for col = 1:instanceNumber
            xticks(counter) = xTick_start + xTick_space * (counter-1);
            xticklables(counter) = instances(col);
            counter=counter+1;
        end
    end
    
   
    xlim([0 length(instances)*2*2+1]);
    set(gca,'xtick',xticks );
    set(gca,'xticklabel',xticklables );
    
    line([length(instances)*2 + 0.5 length(instances)*2 + 0.5], [0 1.1],'LineStyle',':','color','k','LineWidth',1);
    
    ylim([0, 1.05]);
    
    ax = gca(); 
    ax.TickLabelInterpreter = 'tex';
    ax.FontSize = 12; 
    
    set(gca,'xticklabel',xticklables);
    xlabel({'\fontsize{12}DAG-1                                                                          DAG-2';strcat('\fontsize{14} System Utilisation U= ', num2str(task * 25), '%')}) 

    ylabel('Normalised makespan','FontSize', 14)
    
    c = findobj(gca,'Tag','Box');
    
    legendEle = zeros(1);
    for i = 0 : methodNum-1
        legendEle(i+1) = 1 + 10 * i * 2;
    end
    legendEle =  sort(legendEle,'descend');
    
    saveas(gcf,strcat('figs/','ep_',metric,num2str(task),'.eps'), 'epsc');
end
