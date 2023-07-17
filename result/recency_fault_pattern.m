
maxUtil = 8;


f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

line([10.5 10.5], [0 1.1],'LineStyle',':','color','k','LineWidth',1);
hold on
line([20.5 20.5], [0 1.1],'LineStyle',':','color','k','LineWidth',1);

 for util = 1:5
   if(util == 5)
       data = readmatrix(strcat('recency_pattern/',metric ,'1', '_4.0' , '_TIME_DEFAULT' , '.txt'));
   else
       data = readmatrix(strcat('recency_pattern/',metric ,'1', '_',num2str(util*0.8) ,'_TIME_DEFAULT', '.txt'));
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

    data_per_util = zeros(systemNo,methodNum);
    for m=1:methodNum
        datam = dataByMethod{m};

        datam_media = zeros(systemNo:methodNum);
        for row = 1:size(datam,1)
            datam_media(row) = median(datam(row,1:colsNum));
        end

        datam_media_col = datam_media';
        data_per_util(:,m) = datam_media_col;
    end

     pos = zeros(1);
     for col = 1:methodNum
         pos(col) = (util - 1) * 2 + col;
     end

     boxplot(data_per_util(:,1), 'position',pos(1), 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
     hold on
     boxplot(data_per_util(:,2), 'position',pos(2), 'widths', 0.65, 'symbol','.', 'color', colors(2,:));
     hold on
 end

for util = 1:5
   if(util == 5)
       data = readmatrix(strcat('recency_pattern/',metric ,'1', '_4.0' , '_TIME_STEP' , '.txt'));
   else
       data = readmatrix(strcat('recency_pattern/',metric ,'1', '_',num2str(util*0.8) ,'_TIME_STEP', '.txt'));
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

    data_per_util = zeros(systemNo,methodNum);
    for m=1:methodNum
        datam = dataByMethod{m};

        datam_media = zeros(systemNo:methodNum);
        for row = 1:size(datam,1)
            datam_media(row) = median(datam(row,1:colsNum));
        end

        datam_media_col = datam_media';
        data_per_util(:,m) = datam_media_col;

    end

     pos = zeros(1);
     for col = 1:methodNum
         pos(col) = (util - 1) * 2 + col + 10;
     end

     boxplot(data_per_util(:,1), 'position',pos(1), 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
     hold on
     boxplot(data_per_util(:,2), 'position',pos(2), 'widths', 0.65, 'symbol','.', 'color', colors(2,:));
     hold on

end

for util = 1:5
   if(util == 5)
       data = readmatrix(strcat('recency_pattern/',metric ,'1', '_4.0' , '_TIME_CURVE' , '.txt'));
   else
       data = readmatrix(strcat('recency_pattern/',metric ,'1', '_',num2str(util*0.8) ,'_TIME_CURVE', '.txt'));
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

    data_per_util = zeros(systemNo,methodNum);
    for m=1:methodNum
        datam = dataByMethod{m};

        datam_media = zeros(systemNo:methodNum);
        for row = 1:size(datam,1)
            datam_media(row) = median(datam(row,1:colsNum));
        end

        datam_media_col = datam_media';
        data_per_util(:,m) = datam_media_col;

    end

     pos = zeros(1);
     for col = 1:methodNum
         pos(col) = (util - 1) * 2 + col + 20;
     end

     boxplot(data_per_util(:,1), 'position',pos(1), 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
     hold on
     boxplot(data_per_util(:,2), 'position',pos(2), 'widths', 0.65, 'symbol','.', 'color', colors(2,:));
     hold on

end

xlim([0 31]);
ylim([0 1]);
xticks = 1.5:2:30;
xticklables = ["10","20","30","40","50","10","20","30","40","50","10","20","30","40","50" ];

set(gca,'xtick',xticks );
set(gca,'xticklabel',xticklables);

ax = gca(); 
ax.TickLabelInterpreter = 'tex';
ax.FontSize = 12;

% text(4.2,0.9,'Profile 1')


xlabel({'\fontsize{12}Profile - linear                          Profile - S curve                 Profile - exponential';'\fontsize{14}System utilisation (%)'})
ylabel('Normalised makespan','FontSize', 14)

c = findobj(gca,'Tag','Box');
h = legend([c(2),c(1)],"WF+EO","AJLR",'FontAngle','italic','location','southeast','Orientation','horizontal');
set(h,'FontSize',14);

% title({'Recency Profile 1               Recency Profile 2               Recency Profile 3'})


set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('figs/ep_recency_fault_pattern.eps'), 'epsc');
