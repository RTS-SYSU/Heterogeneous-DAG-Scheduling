close all

cores = [4];
percents = [0.1,0.2,0.3,0.4,0.5];
effects = [0.1,0.2,0.3,0.4,0.5];
instanceNums = [1,3,5,10];

percent = '1.0'

for core = cores
    data = readmatrix(strcat('../faults/out_',num2str(core),'_',num2str(0.5),'_',num2str(percent),'_',num2str(1),'.txt'));
    colors=[[0.8500 0.3250 0.0980]; [0 0.4470 0.7410];  [0.9290 0.6940 0.1250]; [0.4660, 0.6740, 0.1880]; [0.3010, 0.7450, 0.9330]; [0.6350, 0.0780, 0.1840]];

    figure('Position', [100, 100, 1200, 350]);

    [noRows, noColumn] = size(data);

    for i = 1: noColumn
        if i < 3
            if mod(i,2) ==0
                boxplot(data(:,i), 'position', i, 'widths', 0.65, 'symbol','.','color','m');
            else
                boxplot(data(:,i), 'position', i, 'widths', 0.65, 'symbol','.','color','k');
            end
        else    
            if mod(i,2) ==0
                boxplot(data(:,i), 'position', i, 'widths', 0.65, 'symbol','.','color','r');
            else
                boxplot(data(:,i), 'position', i, 'widths', 0.65, 'symbol','.','color','b');
            end
        end
        hold on;
    end

    % set(gca,'XTick',["no error", "all error"], 'YTick', [])
    

    line([2.5 2.5], [0 max(max(data))+20000],'color','k','LineWidth',1);
    line([4.5 4.5], [0 max(max(data))+20000],'LineStyle',':','color','k','LineWidth',1);
    line([6.5 6.5], [0 max(max(data))+20000],'LineStyle',':','color','k','LineWidth',1);
    line([8.5 8.5], [0 max(max(data))+20000],'LineStyle',':','color','k','LineWidth',1);

    xticks = [1.5 3.5 5.5 7.5 9.5];

    row1 = {'no/all nodes error' 'pathET' 'pathNum' 'nodeET' 'sensitive'};
   

    set(gca,'xtick',xticks );
    set(gca,'xticklabel',row1,'fontsize',14);

%     ylabel('error','FontSize', 14)
    ylabel('makespan','FontSize', 14)
    
    c = findobj(gca,'Tag','Box');
    h = legend([c(4), c(3)],"error on low","error on high", "AJLR+offline",'FontAngle','italic','location','northeast','Orientation','horizontal');
    set(h,'FontSize',14);


    % saveas(gcf,strcat('faults/faults_1.eps'), 'epsc');
    % saveas(gcf,strcat('faults/out_',num2str(core),'_',num2str(0.5),'_',num2str(0.5),'_',num2str(1),'.png'));
    saveas(gcf,strcat('faults/box_out_',num2str(core),'.png'));
end

% coreNum = 4;
% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% figure('Position', [100, 100, 1200, 350]);
% count =1;
% for percent = percents
%     data = readmatrix(strcat('faults/out_',num2str(coreNum),'_',num2str(percent),'_',num2str(0.5),'_',num2str(1),'.txt'));
%     
%     boxplot(data(:,3), 'position', 1+2*(count-1), 'widths', 0.65, 'symbol','.','color','b');
%     hold on;
%     boxplot(data(:,4), 'position', 2*(count), 'widths', 0.65, 'symbol','.', 'color','r');
%     hold on;
%     
%     count= count + 1;
% end
% 
% xticks = 1.5 : 2 : 10;
% 
% row1 = {'10%' '20%' '30%' '40%' '50%'};
% 
% 
% set(gca,'xtick',xticks );
% set(gca,'xticklabel',row1,'fontsize',14);
% 
% ylabel('makespan','FontSize', 14)
% xlabel('percentage of nodes - pathET','FontSize', 14)
% saveas(gcf,strcat('faults/out_percentage_pathET.png'));
% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% figure('Position', [100, 100, 1200, 350]);
% count =1;
% for percent = percents
%     data = readmatrix(strcat('faults/out_',num2str(coreNum),'_',num2str(percent),'_',num2str(0.5),'_',num2str(1),'.txt'));
%     
%     boxplot(data(:,5), 'position', 1+2*(count-1), 'widths', 0.65, 'symbol','.','color','b');
%     hold on;
%     boxplot(data(:,6), 'position', 2*(count), 'widths', 0.65, 'symbol','.', 'color','r');
%     hold on;
%     
%     count= count + 1;
% end
% 
% xticks = 1.5 : 2 : 10;
% 
% row1 = {'10%' '20%' '30%' '40%' '50%'};
% 
% 
% set(gca,'xtick',xticks );
% set(gca,'xticklabel',row1,'fontsize',14);
% 
% ylabel('makespan','FontSize', 14)
% xlabel('percentage of nodes - number of paths','FontSize', 14)
% saveas(gcf,strcat('faults/out_percentage_path.png'));
% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% figure('Position', [100, 100, 1200, 350]);
% count =1;
% for percent = percents
%     data = readmatrix(strcat('faults/out_',num2str(coreNum),'_',num2str(percent),'_',num2str(0.5),'_',num2str(1),'.txt'));
%     
%     boxplot(data(:,7), 'position', 1+2*(count-1), 'widths', 0.65, 'symbol','.','color','b');
%     hold on;
%     boxplot(data(:,8), 'position', 2*(count), 'widths', 0.65, 'symbol','.', 'color','r');
%     hold on;
%     
%     count= count + 1;
% end
% 
% xticks = 1.5 : 2 : 10;
% 
% row1 = {'10%' '20%' '30%' '40%' '50%'};
% 
% 
% set(gca,'xtick',xticks );
% set(gca,'xticklabel',row1,'fontsize',14);
% 
% ylabel('makespan','FontSize', 14)
% xlabel('percentage of nodes - ET','FontSize', 14)
% saveas(gcf,strcat('faults/out_percentage_et.png'));
% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% figure('Position', [100, 100, 1200, 350]);
% count =1;
% for percent = [0.1,0.2,0.3,0.4, 0.5]
%     data = readmatrix(strcat('faults/out_',num2str(coreNum),'_',num2str(percent),'_',num2str(0.5),'_',num2str(1),'.txt'));
%     
%     boxplot(data(:,9), 'position', 1+2*(count-1), 'widths', 0.65, 'symbol','.','color','b');
%     hold on;
%     boxplot(data(:,10), 'position', 2*(count), 'widths', 0.65, 'symbol','.', 'color','r');
%     hold on;
%     
%     count= count + 1;
% end
% 
% xticks = 1.5 : 2 : 10;
% 
% row1 = {'10%' '20%' '30%' '40%' '50%'};
% 
% 
% set(gca,'xtick',xticks );
% set(gca,'xticklabel',row1,'fontsize',14);
% 
% ylabel('makespan','FontSize', 14)
% xlabel('percentage of nodes - sensitivity','FontSize', 14)
% saveas(gcf,strcat('faults/out_percentage_sensitivity.png'));
% %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

