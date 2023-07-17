
maxUtil = 8;
systemNo = 1000;

metric = 'makespan_';

f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
    
     for taskNum = 1:4
       data = readmatrix(strcat('offline_multi/', metric, num2str(taskNum), '_0.8' ,'_TIME_DEFAULT', '.txt'));
     
       
       
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
%                 datam_media(row) = mean(datam(row,1:colsNum));
                    datam_media(row) = datam(row,5);
            end

            datam_media_col = datam_media';
            data_per_util(:,m) = datam_media_col;
        end
        
         pos = zeros(1);
         for col = 1:methodNum
             pos(col) = (taskNum - 1) * 3 + col;
         end

         boxplot(data_per_util(:,1), 'position',pos(1), 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
         hold on
         boxplot(data_per_util(:,2), 'position',pos(2), 'widths', 0.65, 'symbol','.', 'color', colors(2,:));
         hold on
         boxplot(data_per_util(:,3), 'position',pos(3), 'widths', 0.65, 'symbol','.', 'color', colors(3,:));
         hold on

     end
     
xlim([0 13]);
ylim([-0.1 1.05]);
xticks = 2:3:15;
xticklables = ["1", "2", "3", "4", "5"];

set(gca,'xtick',xticks );
set(gca,'xticklabel',xticklables);

ax = gca(); 
ax.TickLabelInterpreter = 'tex';
ax.FontSize = 12;

% text(4.2,0.9,'Profile 1')


xlabel('Number of DAG tasks in the system','FontSize', 14)
ylabel('Normalised makespan','FontSize', 14)

c = findobj(gca,'Tag','Box');
h = legend([c(3), c(2), c(1)],"WF+EO","AJLR", "AJLR-critical",'FontAngle','italic','location','southeast','Orientation','horizontal');
set(h,'FontSize',14);

% title({'Recency Profile 1               Recency Profile 2               Recency Profile 3'})


set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('figs/ep_online_offline_task.eps'), 'epsc');
% saveas(gcf,strcat('figs/ep_online_offline_task.png'));
