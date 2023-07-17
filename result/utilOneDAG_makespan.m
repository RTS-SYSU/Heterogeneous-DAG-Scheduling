close all
f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

InstanceIndex = 10;
ccc = 1

for task = 1:2:9
    data = readmatrix(strcat('sysUtilOneDAG/',metric ,num2str(task), '_2.0', '.txt'));
    instanceNo = readmatrix(strcat('sysUtilOneDAG/','instanceNum_',num2str(task), '_2.0', '.txt'));

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
    
    pos_DAG1 = 1;
    % plot all data by boxplot
    for m=1:methodNum
        datam = dataByMethod{m};
        
        pos = zeros(1);
        for col = 1:colsNum
            pos(col) = (col-1) * methodNum + m;
        end

        boxplot(datam(:,InstanceIndex), 'position', (ccc-1)*2+m, 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;

        pos_DAG1 = pos_DAG1 +1;
    end
    ccc = ccc +1;
end

ccc = 1;
for task = 1:2:9
    data = readmatrix(strcat('sysUtilOneDAG/',metric ,num2str(task), '_2.0', '.txt'));
    instanceNo = readmatrix(strcat('sysUtilOneDAG/','instanceNum_',num2str(task), '_2.0', '.txt'));

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
    
    pos_DAG1 = 1;
    % plot all data by boxplot

    
    for m=1:methodNum
        datam = dataByMethod{m};
        
        pos = zeros(1);
        for col = 1:colsNum
            pos(col) = (col-1) * methodNum + m;
        end

        boxplot(datam(:,instanceNo(2)+InstanceIndex), 'position', 10 + (ccc-1)*2+m, 'widths', 0.65, 'symbol','.', 'color', colors(m,:));
        hold on;
    end
    
    ccc = ccc +1;
end



xlim([0 20+1]);

ylim([0, 1.05]);
xlabel({'\fontsize{12}DAG-1                                                         DAG-2';strcat('\fontsize{14} System Utilisation U (%)')}) 
           
ylabel('Normalised makespan','FontSize', 14)

c = findobj(gca,'Tag','Box');

legendEle = zeros(1);
for i = 0 : methodNum-1
    legendEle(i+1) = 1 + 10 * i * 2;
end
legendEle =  sort(legendEle,'descend');

line([10.5 10.5], [0 1.1],'LineStyle',':','color','k','LineWidth',1);
    
ho = findobj(gcf,'tag','Outliers');
set(ho,'MarkerSize',1)

xticks= 1.5 : 2 : 21;
set(gca,'xtick',xticks );
xticklables = strings(1);
index = 1
count = 1
for i=1.5:2:10
    xticklables(index) =  num2str((count*2) * 10);
    
    count= count +1
    index= index +1
end

count = 1
for i=1.5:2:10
    xticklables(index) =  num2str((count*2) * 10);
    
    count= count +1
    index= index +1
end
set(gca,'xticklabel',xticklables );


saveas(gcf,strcat('figs/','epUtilOneDAG_',metric,num2str(task),'.eps'), 'epsc');