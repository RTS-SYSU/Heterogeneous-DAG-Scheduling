close all;

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

legend_labels= ["MSF","MCF"];
% legend_labels= ["AJLR","CARVB"];
legend_position1= 'northwest';
legend_position2= 'vertical';
% legend_labels= ["WCET","Sensitivity"];
% legend_labels= ["Sensitivity","AJLR v2.0"];

xticklables = ["10","20","30","40","50", "60" ,"70","80","90","100" ];


p1_ary = [0 1 5 ];
p2_ary = [100 99 95 ];

% percentile_range = [[0 100]; [1 99]; [5 95]; [10 90]; [20 80]];
% percentile_value = percentile_range(1,:);

% scatter_colors=['b','r','y'];
colors=[[0 0.4470 0.7410]; [0.8500 0.3250 0.0980];  [0.9290 0.6940 0.1250]; [0.4940 0.1840 0.5560];[0.4660 0.6740 0.1880]; [0.3010 0.7450 0.9330];[0.6350 0.0780 0.1840]];


%% boxplot_makespan_medain
f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
file_name = '../predict_rule2/';

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
    for m=1:methodNum
        datam = dataByMethod{m};
        d = reshape(median(datam'),[],1);
        data_per_util =[data_per_util d];
    end
    
    pos = zeros(1);
    for col = 1:methodNum
        pos(col) = (util - 1) * methodNum + col;
    end
    
    for c = 1:methodNum
        boxplot(data_per_util(:,c), 'position',pos(c), 'widths', 0.65, 'symbol','', 'color', colors(c,:));
        hold on
    end
end

xlim([0 uEnd*methodNum+1]);
% ylim([13000 17000]);
xticks = sum(1:methodNum)/methodNum : methodNum : uEnd*methodNum+1;


set(gca,'xtick',xticks );
set(gca,'xticklabel',xticklables(uStart:uEnd));


ax = gca();
ax.TickLabelInterpreter = 'tex';
ax.FontSize = 12;

xlabel('System utilisation (%)','FontSize', 14)
ylabel('$\mathcal{M}$','Interpreter','latex','FontSize', 14)

c = findobj(gca,'Tag','Box');



h = legend([c(2),c(1)],legend_labels,'FontAngle','italic','location',legend_position1,'Orientation',legend_position2);
set(h,'FontSize',12);


set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('../figs/ep_predict_rule2_compare_medain.png'));
saveas(gcf,strcat('../figs/ep_predict_rule2_compare_medain.eps'), 'epsc');



%% boxplot_makespan_upper
% for p = 1:length(p1_ary)
    p=2;
    p1 = p1_ary(p);
    p2 = p2_ary(p);
    
    
    f=figure('Position', [100, 100, wid, len]);
    set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
    file_name = '../predict_rule2/';
    
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
        for m=1:methodNum
            datam = dataByMethod{m};
            d = reshape(prctile(datam', p2,1),[],1);
            data_per_util =[data_per_util d];
        end
        
        pos = zeros(1);
        for col = 1:methodNum
            pos(col) = (util - 1) * methodNum + col;
        end
        
        for c = 1:methodNum
            boxplot(data_per_util(:,c), 'position',pos(c), 'widths', 0.65, 'symbol','', 'color', colors(c,:));
            hold on
        end
    end
    
    xlim([0 uEnd*methodNum+1]);
    % ylim([13000 17000]);
    xticks = sum(1:methodNum)/methodNum : methodNum : uEnd*methodNum+1;
    
    
    set(gca,'xtick',xticks );
    set(gca,'xticklabel',xticklables(uStart:uEnd));
    
    
    ax = gca();
    ax.TickLabelInterpreter = 'tex';
    ax.FontSize = 12;
    
    xlabel('System utilisation (%)','FontSize', 14)
    if p==2
        ylabel('$\mathcal{U}$','Interpreter','latex','FontSize', 14)
    else
        ylabel({'Normalised makespan';strcat("(the ",num2str(p2),"% percentile)")},'FontSize', 14)
    end
    
    
    c = findobj(gca,'Tag','Box');
    
    
    
    h = legend([c(2),c(1)],legend_labels,'FontAngle','italic','location',legend_position1,'Orientation',legend_position2);
    set(h,'FontSize',12);
    
    
    set(gcf, 'PaperSize', [25 25])
    saveas(gcf,strcat('../figs/ep_predict_rule2_compare_',num2str(p2),'.png'));
    saveas(gcf,strcat('../figs/ep_predict_rule2_compare_',num2str(p2),'.eps'), 'epsc');
% end


%% boxplot_variation
% for p = 1:length(p1_ary)
    p=2;
    p1 = p1_ary(p);
    p2 = p2_ary(p);
  
    f=figure('Position', [100, 100, wid, len]);
    set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
    file_name = '../predict_rule2/';

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
        for m=1:methodNum
            datam = dataByMethod{m};
            datam = datam(:,startIns:endIns);
            %         datam = prctile(datam, percentile_range(1,:));

            %         dataDiff = max(datam') - min(datam');
            %         data_per_util =[data_per_util dataDiff'];

            datam_std = std(datam');
            data_per_util =[data_per_util prctile(datam_std', p1:p2,'all')];
        end

        pos = zeros(1);
        for col = 1:methodNum
            pos(col) = (util - 1) * methodNum + col;
        end

        for c = 1:methodNum
            boxplot(data_per_util(:,c)/max(max(data_per_util)), 'position',pos(c), 'widths', 0.65, 'symbol','', 'color', colors(c,:));
            hold on
        end
    end

    xlim([0 uEnd*methodNum+1]);

%     if(p==1)
%         ylim([0 3000]);
%     end

    xticks = sum(1:methodNum)/methodNum : methodNum : uEnd*methodNum+1;
    set(gca,'xticklabel',xticklables(uStart:uEnd));

    set(gca,'xtick',xticks );
    set(gca,'xticklabel',xticklables);


    ax = gca();
    ax.TickLabelInterpreter = 'tex';
    ax.FontSize = 12;

    xlabel('System utilisation (%)','FontSize', 14)

    if p==2
        ylabel('$\mathcal{V}$','Interpreter','latex','FontSize', 14)
    else
        ylabel('$\mathcal{V}$','Interpreter','latex','FontSize', 14)
    end

    c = findobj(gca,'Tag','Box');



    h = legend([c(2),c(1)],legend_labels,'FontAngle','italic','location','northeast','Orientation',legend_position2);
    set(h,'FontSize',12);


    set(gcf, 'PaperSize', [25 25])
    saveas(gcf,strcat('../figs/ep_predict_rule2_compare_diff_',num2str(p1),'_',num2str(p2),'.png'));
    saveas(gcf,strcat('../figs/ep_predict_rule2_compare_diff_',num2str(p1),'_',num2str(p2),'.eps'), 'epsc');
% end

% for p = 1:length(p1_ary)
%     p1 = p1_ary(p);
%     p2 = p2_ary(p);
%
%     %% boxplot_diff_normalized
%     f=figure('Position', [100, 100, wid, len]);
%     set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
%     file_name = '../predict/';
%
%     for util = uStart:uEnd
%         read = util + 0;
%         if(read == 5)
%             data = readmatrix(strcat(file_name,metric ,'1', uSpec1, '.txt'));
%         elseif(read == 10)
%             data = readmatrix(strcat(file_name,metric ,'1', uSpec2, '.txt'));
%         else
%             data = readmatrix(strcat(file_name,metric ,'1', '_',num2str(read * uGap), '.txt'));
%         end
%
%         rowsNum = size(data,1);
%
%         methodNum = rowsNum/systemNo;
%         dataByMethod = cell(1,methodNum);
%
%         % get data by each method
%         for m = 1: methodNum
%             startIndex = 1 + (m-1) * systemNo;
%             endIndex = m * systemNo;
%             dataByMethod{m} = data(startIndex :endIndex,:);
%         end
%
%         data_per_util = [];
%         for m=1:methodNum
%             datam = dataByMethod{m};
%             datam = datam(:,startIns:endIns);
%
%             % dataDiff = max(datam') - min(datam');
%             % data_per_util =[data_per_util dataDiff'];
%
%             datam_std = std(datam');
%             data_per_util =[data_per_util prctile(datam_std', p1:p2,'all')];
%         end
%
%         pos = zeros(1);
%         for col = 1:methodNum
%             pos(col) = (util - 1) * methodNum + col;
%         end
%
%         for c = 1:methodNum
%             boxplot(data_per_util(:,c)/max(max(data_per_util)), 'position',pos(c), 'widths', 0.65, 'symbol','.', 'color', colors(c,:));
%             hold on
%         end
%     end
%
%     xlim([0 uEnd*methodNum+1]);
%     % ylim([13000 17000]);
%     xticks = sum(1:methodNum)/methodNum : methodNum : uEnd*methodNum+1;
%     set(gca,'xticklabel',xticklables(uStart:uEnd));
%
%     set(gca,'xtick',xticks );
%     set(gca,'xticklabel',xticklables);
%
%
%     ax = gca();
%     ax.TickLabelInterpreter = 'tex';
%     ax.FontSize = 12;
%
%     xlabel('System utilisation (%)','FontSize', 14)
%     ylabel('Diff of max and min makespan','FontSize', 14)
%
%     c = findobj(gca,'Tag','Box');
%
%
%
%     h = legend([c(2),c(1)],"AJLR v1.0","AJLR v2.0 CC",'FontAngle','italic','location','northoutside','Orientation','horizontal');
%     set(h,'FontSize',12);
%
%
%     set(gcf, 'PaperSize', [25 25])
%     saveas(gcf,strcat('../figs/ep_predict_compare_diff_norm_',num2str(p1),'_',num2str(p2),'.png'));
%     saveas(gcf,strcat('../figs/ep_predict_compare_diff_norm_',num2str(p1),'_',num2str(p2),'.eps'), 'epsc');


% %% scatter
% f=figure('Position', [100, 100, wid*3, len*3]);
% for util = uStart:uEnd
%     read = util + 0;
%     if(read == 5)
%         data = readmatrix(strcat(file_name,metric ,'1', uSpec1, '.txt'));
%     elseif(read == 10)
%         data = readmatrix(strcat(file_name,metric ,'1', uSpec2, '.txt'));
%     else
%         data = readmatrix(strcat(file_name,metric ,'1', '_',num2str(read * uGap), '.txt'));
%     end
%
%
%     colsNum = 10;
%     rowsNum = size(data,1);
%
%     methodNum = rowsNum/systemNo;
%     dataByMethod = cell(1,methodNum);
%
%     % get data by each method
%     for m = 1: methodNum
%         startIndex = 1 + (m-1) * systemNo;
%         endIndex = m * systemNo;
%         dataByMethod{m} = data(startIndex :endIndex,:);
%     end
%
%     subplot(4,3,util);
%
%     data_per_util = [];
%     for m=1:methodNum
%         datam = dataByMethod{m};
%         data_per_util =[data_per_util reshape(datam(:,startIns_scatter:endIns_scatter),[],1)];
%         [r,c] = size(data_per_util);
%
%         xticks = 1:1:r;
%
%         scatter(xticks,data_per_util(:,m), 5, 'MarkerFaceColor', colors(m,:));
%         hold on
%     end
%
%     xticks = 0:10:(endIns_scatter-startIns_scatter);
%     xtickslabel = startIns_scatter:10:endIns_scatter;
%     xlim([0 (endIns_scatter-startIns_scatter)]);
%     set(gca,'xtick',xticks);
%     set(gca,'xticklabel',xtickslabel);
%     xlabel('No. instance','FontSize', 12)
%     ylabel('normalized makespan','FontSize', 12)
%     h = legend("AJLR v1.0", "AJLR v2.0 CC",'location','northoutside','Orientation','horizontal');
%     legend boxoff
%
%     set(h,'FontSize',12,'color','none');
% end
% set(gcf, 'PaperSize', [25 25])
% saveas(gcf,strcat('../figs/ep_predict_scatter.png'));
% saveas(gcf,strcat('../figs/ep_predict_scatter.eps'), 'epsc');
%
% percentile = [75 80 90 95 99 99.9 99.99 100];
% xtick = 1: length(percentile);
%
%
% %% perdict
% metric = 'makespan_abs_';
% f=figure('Position', [100, 100, wid*2, len*3]);
% for util = uStart:uEnd
%     read = util + 0;
%     if(read == 5)
%         data = readmatrix(strcat(file_name,metric ,'1', uSpec1, '.txt'));
%     elseif(read == 10)
%         data = readmatrix(strcat(file_name,metric ,'1', uSpec2, '.txt'));
%     else
%         data = readmatrix(strcat(file_name,metric ,'1', '_',num2str(read * uGap), '.txt'));
%     end
%
%
%     colsNum = 10;
%     rowsNum = size(data,1);
%
%     methodNum = rowsNum/systemNo;
%     dataByMethod = cell(1,methodNum);
%
%     % get data by each method
%     for m = 1: methodNum
%         startIndex = 1 + (m-1) * systemNo;
%         endIndex = m * systemNo;
%         dataByMethod{m} = data(startIndex :endIndex,:);
%     end
%
%     scatter_colors=['r','b','g'];
%     subplot(4,3,util);
%
%     data_per_util = [];
%     for m=1:methodNum
%         datam = dataByMethod{m};
%         data_per_util =[data_per_util reshape(datam(1,startIns_predict:endIns_predict),[],1)];
%     end
%
%     plot(xtick, prctile(data_per_util(:,1),percentile,"all") ,'-x', xtick, prctile(data_per_util(:,2),percentile,"all"),'-o', xtick, prctile(data_per_util(:,3),percentile,"all"),'-*', 'LineWidth', 2, 'MarkerSize', 10);
%
%     set(gca,'xtick',xtick);
%     set(gca,'xticklabel',percentile);
%     xlabel('Level of confidence','FontSize', 12)
%     ylabel('normalized makespan','FontSize', 12)
%     h = legend("AJLR v1.0", "AJLR v2.0 CC",'location','northoutside','Orientation','horizontal');
%     legend boxoff
%
%     set(h,'FontSize',12,'color','none');
% end
% set(gcf, 'PaperSize', [25 25])
% saveas(gcf,strcat('../figs/ep_predict.png'));
% saveas(gcf,strcat('../figs/ep_predict.eps'), 'epsc');

% end


%  %% boxplot_RMSD
% for p = 1:length(p1_ary)
%     p1 = p1_ary(p);
%     p2 = p2_ary(p);
%     
%    
%     f=figure('Position', [100, 100, wid, len]);
%     set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
%     file_name = '../predict/';
%     
%     for util = uStart:uEnd
%         read = util + 0;
%         if(read == 5)
%             data = readmatrix(strcat(file_name,metric ,'1', uSpec1, '.txt'));
%         elseif(read == 10)
%             data = readmatrix(strcat(file_name,metric ,'1', uSpec2, '.txt'));
%         else
%             data = readmatrix(strcat(file_name,metric ,'1', '_',num2str(read * uGap), '.txt'));
%         end
%         
%         rowsNum = size(data,1);
%         
%         methodNum = rowsNum/systemNo;
%         dataByMethod = cell(1,methodNum);
%         
%         % get data by each method
%         for m = 1: methodNum
%             startIndex = 1 + (m-1) * systemNo;
%             endIndex = m * systemNo;
%             dataByMethod{m} = data(startIndex :endIndex,:);
%         end
%         
%         data_per_util = [];
%         for m=1:methodNum
%             datam = dataByMethod{m};
%             datam = datam(:,startIns:endIns);
%             %         datam = prctile(datam, percentile_range(1,:));
%             
%             %         dataDiff = max(datam') - min(datam');
%             %         data_per_util =[data_per_util dataDiff'];
%             
%             datam_std = rms(datam');
%             
%           
%             med_v = median(datam');
%             factor = med_v
%             
%             rmsd_per_method = [];
%             for k = 1: size(datam,1)
%                 drow = datam(k,:);
%                 rmsd = 0;
%                 for r = 1:length(drow)
%                     d = drow(r);
%                     rmsd_one = (d-factor(k)) * (d-factor(k));
%                     rmsd = rmsd + rmsd_one;
%                 end
%                 
%                 rmsd = sqrt(rmsd / (length(drow)-2));
%                 rmsd_per_method = [rmsd_per_method rmsd];
%             end
% 
% %             data_per_util =[data_per_util prctile(datam_std', p1:p2,'all')];
%             data_per_util =[data_per_util rmsd_per_method'];
%         end
%         
%         pos = zeros(1);
%         for col = 1:methodNum
%             pos(col) = (util - 1) * methodNum + col;
%         end
%         
% %         / max(max(data_per_util))
%         for c = 1:methodNum
%             boxplot(data_per_util(:,c), 'position',pos(c), 'widths', 0.65, 'symbol','', 'color', colors(c,:));
%             hold on
%         end
%     end
%     xlim([0 uEnd*methodNum+1]);
%     ylim([0 3000]);
% %     if(p==1)
% %         ylim([0 80000]);
% %     end
%     
%     xticks = sum(1:methodNum)/methodNum : methodNum : uEnd*methodNum+1;
%     set(gca,'xticklabel',xticklables(uStart:uEnd));
%     
%     set(gca,'xtick',xticks );
%     set(gca,'xticklabel',xticklables);
%     
%     
%     ax = gca();
%     ax.TickLabelInterpreter = 'tex';
%     ax.FontSize = 12;
%     
%     xlabel('System utilisation (%)','FontSize', 14)
%     
%     if p==1
%         ylabel('RMSD','Interpreter','latex','FontSize', 14)
%     else
%         ylabel({'RMSD' ; strcat("(",num2str(p1),"%", " to ", num2str(p2),"%"  , " percentile)")},'FontSize', 14)
%     end
%     
%     c = findobj(gca,'Tag','Box');
%     
%     
%     
%     h = legend([c(2),c(1)],legend_labels,'FontAngle','italic','location','northoutside','Orientation','horizontal');
%     set(h,'FontSize',12);
%     
%     
%     set(gcf, 'PaperSize', [25 25])
%     saveas(gcf,strcat('../figs/ep_predict_compare_rmsd_',num2str(p1),'_',num2str(p2),'.png'));
%     saveas(gcf,strcat('../figs/ep_predict_compare_rmsd_',num2str(p1),'_',num2str(p2),'.eps'), 'epsc');
%     
% end