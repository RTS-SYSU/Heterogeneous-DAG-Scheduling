close all

startIns = 1;
endIns = 100;

uStart = 1;
uEnd = 9;
uGap = 0.4;
uSpec1 = '_2.0';
uSpec2 = '_4.0';

wid = 800;
len = 300;

systemNo = 500;
metric_makespan = 'makespan_';
metric = 'makespan_abs_';

legend_labels= ["HWF","HSF"];
legend_position1= 'northwest';
legend_position2= 'vertical';
% legend_labels= ["WCET","Sensitivity"];
% legend_labels= ["Sensitivity","AJLR v2.0"];

xticklables = ["10","20","30","40","50", "60" ,"70","80","90","100" ];

start_method = 1;

p1_ary = [0 1 5 ];
p2_ary = [100 99 95 ];

% percentile_range = [[0 100]; [1 99]; [5 95]; [10 90]; [20 80]];
% percentile_value = percentile_range(1,:);

% scatter_colors=['b','r','y'];
colors=[[0 0.4470 0.7410]; [0.8500 0.3250 0.0980];  [0.9290 0.6940 0.1250]; [0.4940 0.1840 0.5560];[0.4660 0.6740 0.1880]; [0.3010 0.7450 0.9330];[0.6350 0.0780 0.1840]];


alldata = [];
%% boxplot_makespan_medain
f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
file_name = '../predict_rule1/';

line([0 uEnd*methodToDisplay+1],[1.0 1.0],'Color','k','LineStyle',':','LineWidth',0.3);
hold on;

for util = uStart:uEnd
    read = util + 0;
    if(read == 5)
        data = readmatrix(strcat(file_name,metric_makespan ,'1', uSpec1, '.txt'));
    elseif(read == 10)
        data = readmatrix(strcat(file_name,metric_makespan ,'1', uSpec2, '.txt'));
    else
        data = readmatrix(strcat(file_name,metric_makespan ,'1', '_',num2str(read * uGap), '.txt'));
    end
    
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
    for m=start_method:methodNum
        datam = dataByMethod{m};
        d = reshape(median(datam'),[],1);
        data_per_util =[data_per_util d];
    end
    
    
    methodToDisplay = 1;
    pos = zeros(1);
    for col = 1:methodToDisplay
        pos(col) = (util - 1) * methodToDisplay + col;
    end
    
    alldata = [alldata data_per_util(:,2)./data_per_util(:,1)];
    
    for c = 1:methodToDisplay
        boxplot(data_per_util(:,2)./data_per_util(:,1), 'position',pos(c), 'widths', 0.65, 'symbol','', 'color', colors(c,:));
        hold on
    end
end

xlim([0 uEnd*methodToDisplay+1]);
ylim([0.6 1.3]);
xticks = sum(1:methodToDisplay)/methodToDisplay : methodToDisplay : uEnd*methodToDisplay;


set(gca,'xtick',xticks );
set(gca,'xticklabel',xticklables(uStart:uEnd));

ax = gca();
ax.TickLabelInterpreter = 'tex';
ax.FontSize = 12;

xlabel('System utilisation (%)','FontSize', 14)
ylabel('$\mathcal{M}_{div}$','Interpreter','latex','FontSize', 14)

c = findobj(gca,'Tag','Box');

% h = legend([ c(2),c(1)],legend_labels,'FontAngle','italic','location',legend_position1,'Orientation',legend_position2);
% set(h,'FontSize',12);


set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('../CARVB_figs/ep_predict_rule1_compare_medain_divide.png'));
saveas(gcf,strcat('../CARVB_figs/ep_predict_rule1_compare_medain_divide.eps'), 'epsc');

1 - mean(mean(alldata))



alldata = [];
%% boxplot_makespan_upper
% for p = 1:length(p1_ary)
p = 2;
p1 = p1_ary(p);
p2 = p2_ary(p);


f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
file_name = '../predict_rule1/';

line([0 uEnd*methodToDisplay+1],[1.0 1.0],'Color','k','LineStyle',':','LineWidth',0.3);
hold on;

for util = uStart:uEnd
    read = util + 0;
    if(read == 5)
        data = readmatrix(strcat(file_name,metric ,'1', uSpec1, '.txt'));
    elseif(read == 10)
        data = readmatrix(strcat(file_name,metric ,'1', uSpec2, '.txt'));
    else
        data = readmatrix(strcat(file_name,metric ,'1', '_',num2str(read * uGap), '.txt'));
    end
    
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
    for m=start_method:methodNum
        datam = dataByMethod{m};
        d = reshape(prctile(datam', p2,1),[],1);
        data_per_util =[data_per_util d];
    end
    
    
    methodToDisplay = 1;
    
    pos = zeros(1);
    for col = 1:methodToDisplay
        pos(col) = (util - 1) * methodToDisplay + col;
    end
    
    alldata = [alldata data_per_util(:,2)./data_per_util(:,1)];
    
    for c = 1:methodToDisplay
        boxplot(data_per_util(:,2)./data_per_util(:,1), 'position',pos(c), 'widths', 0.65, 'symbol','', 'color', colors(c,:));
        hold on
    end
end

xlim([0 uEnd*methodToDisplay+1]);
ylim([0.7 1.2]);
xticks = sum(1:methodToDisplay)/methodToDisplay : methodToDisplay : uEnd*methodToDisplay;


set(gca,'xtick',xticks );
set(gca,'xticklabel',xticklables(uStart:uEnd));


ax = gca();
ax.TickLabelInterpreter = 'tex';
ax.FontSize = 12;

xlabel('System utilisation (%)','FontSize', 14)
if p==2
    ylabel('$\mathcal{U}_{div}$','Interpreter','latex','FontSize', 14)
else
    ylabel({'Normalised makespan';strcat("(the ",num2str(p2),"% percentile)")},'FontSize', 14)
end


c = findobj(gca,'Tag','Box');



%     h = legend([ c(2),c(1)],legend_labels,'FontAngle','italic','location',legend_position1,'Orientation',legend_position2);
%     set(h,'FontSize',12);


set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('../CARVB_figs/ep_predict_rule1_compare_divide_',num2str(p2),'.png'));
saveas(gcf,strcat('../CARVB_figs/ep_predict_rule1_compare_divide_',num2str(p2),'.eps'), 'epsc');
% end

1 - mean(mean(alldata))


alldata = [];
%% boxplot_variation
% for p = 1:length(p1_ary)
p = 2;
p1 = p1_ary(p);
p2 = p2_ary(p);

f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
file_name = '../predict_rule1/';

line([0 uEnd*methodToDisplay+1],[1.0 1.0],'Color','k','LineStyle',':','LineWidth',0.3);
hold on;

for util = uStart:uEnd
    read = util + 0;
    if(read == 5)
        data = readmatrix(strcat(file_name,metric ,'1', uSpec1, '.txt'));
    elseif(read == 10)
        data = readmatrix(strcat(file_name,metric ,'1', uSpec2, '.txt'));
    else
        data = readmatrix(strcat(file_name,metric ,'1', '_',num2str(read * uGap), '.txt'));
    end
    
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
    for m=start_method:methodNum
        datam = dataByMethod{m};
        datam = datam(:,startIns:endIns);
        %         datam = prctile(datam, percentile_range(1,:));
        
        %         dataDiff = max(datam') - min(datam');
        %         data_per_util =[data_per_util dataDiff'];
        
        datam_std = std(datam');
        data_per_util =[data_per_util prctile(datam_std', p1:p2,'all')];
    end
    
    methodToDisplay = 1;
    
    pos = zeros(1);
    for col = 1:methodToDisplay
        pos(col) = (util - 1) * methodToDisplay + col;
    end
    
    
    alldata = [alldata data_per_util(:,2)./data_per_util(:,1)];
    for c = 1:methodToDisplay
        boxplot(data_per_util(:,2)./data_per_util(:,1), 'position',pos(c), 'widths', 0.65, 'symbol','', 'color', colors(c,:));
        hold on
    end
end

xlim([0 uEnd*methodToDisplay+1]);
ylim([0.8 1.05]);
%     if(p==1)
%         ylim([0 3000]);
%     end

xticks = sum(1:methodToDisplay)/methodToDisplay : methodToDisplay : uEnd*methodToDisplay;
set(gca,'xticklabel',xticklables(uStart:uEnd));

set(gca,'xtick',xticks );
set(gca,'xticklabel',xticklables);


ax = gca();
ax.TickLabelInterpreter = 'tex';
ax.FontSize = 12;

xlabel('System utilisation (%)','FontSize', 14)

if p==2
    ylabel('$\mathcal{V}_{div}$','Interpreter','latex','FontSize', 14)
else
    ylabel('$\mathcal{V}_{div}$','Interpreter','latex','FontSize', 14)
end

c = findobj(gca,'Tag','Box');



%     h = legend([c(2),c(1)],legend_labels,'FontAngle','italic','location',legend_position1,'Orientation',legend_position2);
%     set(h,'FontSize',12);


set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('../CARVB_figs/ep_predict_rule1_compare_diff_divide_',num2str(p1),'_',num2str(p2),'.png'));
saveas(gcf,strcat('../CARVB_figs/ep_predict_rule1_compare_diff_divide_',num2str(p1),'_',num2str(p2),'.eps'), 'epsc');

1- mean(mean(alldata))
% end