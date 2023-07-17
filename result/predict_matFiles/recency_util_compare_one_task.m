% close all

startIns = 1;
endIns = 1000;
startIns_scatter = 10;
endIns_scatter = 100;
startIns_predict = 3;
endIns_predict = 1000;

uStart = 1;
uEnd = 10;
uGap = 0.4;
uSpec1 = '_2.0';
uSpec2 = '_4.0';

wid = 800;
len = 300;

systemNo = 1;
metric = 'makespan_';

% scatter_colors=['b','r','y'];
colors=[[0 0.4470 0.7410]; [0.8500 0.3250 0.0980];  [0.9290 0.6940 0.1250]; [0.4940 0.1840 0.5560];[0.4660 0.6740 0.1880]; [0.3010 0.7450 0.9330];[0.6350 0.0780 0.1840]];

%% boxplot
f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
file_name = '../predict/';

for util = uStart:uEnd
    read = util + 0;
    if(read == 5)
        data = readmatrix(strcat(file_name,metric ,'1', uSpec1, '.txt'));
    elseif(read == 10)
        data = readmatrix(strcat(file_name,metric ,'1', uSpec2, '.txt'));
    else
        data = readmatrix(strcat(file_name,metric ,'1', '_',num2str(read * uGap), '.txt'));
    end
    
    colsNum = 10;
    rowsNum = size(data,1);
    
    methodNum = rowsNum/systemNo;
    dataByMethod = cell(1,methodNum);
    
    % get data by each method
    for m = 1: methodNum
        startIndex = 1 + (m-1) * systemNo;
        endIndex = m * systemNo;
        dataByMethod{m} = data(startIndex :endIndex,:);
    end
    
    data_per_util = [];
    for m=1:methodNum
        datam = dataByMethod{m};
        data_per_util =[data_per_util reshape(datam(1,startIns:endIns),[],1)];
        %         datam_media = zeros(systemNo:methodNum);
        %         for row = 1:size(datam,1)
        %             datam_media(row) = median(datam(row,1:10));
        %         end
        %
        %         datam_media_col = datam_media';
        %         data_per_util(:,m) = datam_media_col;
        
        
    end
    
    pos = zeros(1);
    for col = 1:methodNum
        pos(col) = (util - 1) * methodNum + col;
    end
    
    for c = 1:methodNum
        boxplot(data_per_util(:,c), 'position',pos(c), 'widths', 0.65, 'symbol','.', 'color', colors(c,:));
        hold on
    end
end

xlim([0 uEnd*methodNum+1]);
% ylim([13000 17000]);
xticks = sum(1:methodNum)/methodNum : methodNum : 5*methodNum+1;
xticklables = ["20","40","60","80","100" ];

set(gca,'xtick',xticks );
set(gca,'xticklabel',xticklables);


ax = gca();
ax.TickLabelInterpreter = 'tex';
ax.FontSize = 12;

xlabel('System utilisation (%)','FontSize', 14)
ylabel('Normalised makespan','FontSize', 14)

c = findobj(gca,'Tag','Box');



h = legend([c(3), c(2),c(1)],"AJLR v1.0","AJLR v2.0 Basic","AJLR v2.0 CC",'FontAngle','italic','location','northoutside','Orientation','horizontal');
set(h,'FontSize',12);


set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('../figs/ep_predict_compare.png'));
saveas(gcf,strcat('../figs/ep_predict_compare.eps'), 'epsc');


%% scatter
f=figure('Position', [100, 100, wid*3, len*3]);
for util = uStart:uEnd
    read = util + 0;
    if(read == 5)
        data = readmatrix(strcat(file_name,metric ,'1', uSpec1, '.txt'));
    elseif(read == 10)
        data = readmatrix(strcat(file_name,metric ,'1', uSpec2, '.txt'));
    else
        data = readmatrix(strcat(file_name,metric ,'1', '_',num2str(read * uGap), '.txt'));
    end
    
    
    colsNum = 10;
    rowsNum = size(data,1);
    
    methodNum = rowsNum/systemNo;
    dataByMethod = cell(1,methodNum);
    
    % get data by each method
    for m = 1: methodNum
        startIndex = 1 + (m-1) * systemNo;
        endIndex = m * systemNo;
        dataByMethod{m} = data(startIndex :endIndex,:);
    end
    
    subplot(4,3,util);
    
    data_per_util = [];
    for m=1:methodNum
        datam = dataByMethod{m};
        data_per_util =[data_per_util reshape(datam(1,startIns_scatter:endIns_scatter),[],1)];
        [r,c] = size(data_per_util);
        
        xticks = 1:1:r;
        
        scatter(xticks,data_per_util(:,m), 5, 'MarkerFaceColor', colors(m,:));
        hold on
    end
    
    xticks = 0:10:(endIns_scatter-startIns_scatter);
    xtickslabel = startIns_scatter:10:endIns_scatter;
    xlim([0 (endIns_scatter-startIns_scatter)]);
    set(gca,'xtick',xticks);
    set(gca,'xticklabel',xtickslabel);
    xlabel('No. instance','FontSize', 12)
    ylabel('normalized makespan','FontSize', 12)
    h = legend("AJLR v1.0", "AJLR v2.0 Basic", "AJLR v2.0 CC",'location','northoutside','Orientation','horizontal');
    legend boxoff
    
    set(h,'FontSize',12,'color','none');
end
set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('../figs/ep_predict_scatter.png'));
saveas(gcf,strcat('../figs/ep_predict_scatter.eps'), 'epsc');

percentile = [75 80 90 95 99 99.9 99.99 100];
xtick = 1: length(percentile);


%% perdict
metric = 'makespan_abs_';
f=figure('Position', [100, 100, wid*2, len*3]);
for util = uStart:uEnd
    read = util + 0;
    if(read == 5)
        data = readmatrix(strcat(file_name,metric ,'1', uSpec1, '.txt'));
    elseif(read == 10)
        data = readmatrix(strcat(file_name,metric ,'1', uSpec2, '.txt'));
    else
        data = readmatrix(strcat(file_name,metric ,'1', '_',num2str(read * uGap), '.txt'));
    end
    
    
    colsNum = 10;
    rowsNum = size(data,1);
    
    methodNum = rowsNum/systemNo;
    dataByMethod = cell(1,methodNum);
    
    % get data by each method
    for m = 1: methodNum
        startIndex = 1 + (m-1) * systemNo;
        endIndex = m * systemNo;
        dataByMethod{m} = data(startIndex :endIndex,:);
    end
    
    scatter_colors=['r','b','g'];
    subplot(4,3,util);
    
    data_per_util = [];
    for m=1:methodNum
        datam = dataByMethod{m};
        data_per_util =[data_per_util reshape(datam(1,startIns_predict:endIns_predict),[],1)];
    end
    
    plot(xtick, prctile(data_per_util(:,1),percentile,"all") ,'-x', xtick, prctile(data_per_util(:,2),percentile,"all"),'-o', xtick, prctile(data_per_util(:,3),percentile,"all"),'-*', 'LineWidth', 2, 'MarkerSize', 10);
    
    set(gca,'xtick',xtick);
    set(gca,'xticklabel',percentile);
    xlabel('Level of confidence','FontSize', 12)
    ylabel('normalized makespan','FontSize', 12)
    h = legend("AJLR v1.0", "AJLR v2.0 Basic", "AJLR v2.0 CC",'location','northoutside','Orientation','horizontal');
    legend boxoff
    
    set(h,'FontSize',12,'color','none');
end
set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('../figs/ep_predict.png'));
saveas(gcf,strcat('../figs/ep_predict.eps'), 'epsc');