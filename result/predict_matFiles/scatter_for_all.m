% % close all

startIns_scatter = 2;
endIns_scatter = 100;


wid = 800;
len = 300;

systemNo = 500;
taskNo = 10;
metric = 'makespan_abs_';

colors=[[0 0.4470 0.7410]; [0.8500 0.3250 0.0980];  [0.9290 0.6940 0.1250]; [0.4940 0.1840 0.5560];[0.4660 0.6740 0.1880]; [0.3010 0.7450 0.9330];[0.6350 0.0780 0.1840]];

%% scatter
data = readmatrix(strcat(file_name,metric ,'1', '_0.4','.txt'));


rowsNum = size(data,1);

methodNum = rowsNum/systemNo;
dataByMethod = cell(1,methodNum);

% get data by each method
for m = 1: methodNum
    startIndex = 1 + (m-1) * systemNo;
    endIndex = m * systemNo;
    dataByMethod{m} = data(startIndex :endIndex,:);
end

for numTask=1:taskNo
    f=figure('Position', [100, 100, wid, len]);
    
    data_per_util = [];
    median_v1 = 0;
    median_v2 = 0;
    for m=1:methodNum
        datam = dataByMethod{m};
        data_per_util =[data_per_util reshape(datam(numTask,startIns_scatter:endIns_scatter),[],1)];
        [r,c] = size(data_per_util);
        
        xticks = 1:1:r;
        
        scatter(xticks,data_per_util(:,m), 5, 'MarkerFaceColor', colors(m,:));
        hold on
        
        if m==1
            median_v1 = median(data_per_util(:,m));
        else
            median_v2 = median(data_per_util(:,m));
        end
     
        
    end
    
    xticks = 0:10:(endIns_scatter-startIns_scatter);
    xtickslabel = startIns_scatter:10:endIns_scatter;
    xlim([0 (endIns_scatter-startIns_scatter)]);
    set(gca,'xtick',xticks);
    set(gca,'xticklabel',xtickslabel);
    xlabel('No. instance','FontSize', 12)
    ylabel('normalized makespan','FontSize', 12)
    h = legend("AJLR v1.0", "AJLR v2.0 CC",'location','northoutside','Orientation','horizontal');
    legend boxoff
    
    line(xticks,zeros(length(xticks),1) + median_v1,'Color',colors(1,:),'LineStyle','--')
    line(xticks,zeros(length(xticks),1) + median_v2,'Color',colors(2,:),'LineStyle','--')
    
    set(h,'FontSize',12,'color','none');
%     
%     set(gcf, 'PaperSize', [25 25])
%     saveas(gcf,strcat('../figs_predict/ep_predict_scatter.png'));
%     saveas(gcf,strcat('../figs/ep_predict_scatter.eps'), 'epsc');
end