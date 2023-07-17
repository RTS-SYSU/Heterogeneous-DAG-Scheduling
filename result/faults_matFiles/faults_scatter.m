close all

cores = [4];
percents = [0.1,0.2,0.3,0.4,0.5];
effects = [0.1,0.2,0.3,0.4,0.5];
instanceNums = [1,3,5,10];

for core = cores
    data = readmatrix(strcat('faults/out_',num2str(core),'_',num2str(0.5),'_1.0_',num2str(1),'.txt'));
    colors=[[0.8500 0.3250 0.0980]; [0 0.4470 0.7410];  [0.9290 0.6940 0.1250]; [0.4660, 0.6740, 0.1880]; [0.3010, 0.7450, 0.9330]; [0.6350, 0.0780, 0.1840]];

    [noRows, noColumn] = size(data);

    figure('Position', [100, 100, 1200, 350]);
    for i = 1: 2
        if mod(i,2) ==0
            plot(data(:,i),'x','MarkerSize',5,'color','m');
        else
            plot(data(:,i),'x','MarkerSize',5,'color','k');
        end
        
        hold on
    end
    
  
    for i = 4 : 2 : noColumn
        figure('Position', [100, 100, 1200, 350]);
        for k = i-1 : i
            if mod(k,2) ==0
                plot(data(:,k),'x','MarkerSize',5,'color','r');
            else
                plot(data(:,k),'x','MarkerSize',5,'color','b');
            end
            hold on
        end
        
    end    

   
%     ylabel('error','FontSize', 14)
    ylabel('makespan','FontSize', 14)
    
%     c = findobj(gca,'Tag','Box');
%     h = legend([c(4), c(3)],"error on low","error on high", "AJLR+offline",'FontAngle','italic','location','northeast','Orientation','horizontal');
%     set(h,'FontSize',14);


    % saveas(gcf,strcat('faults/faults_1.eps'), 'epsc');
    % saveas(gcf,strcat('faults/out_',num2str(core),'_',num2str(0.5),'_',num2str(0.5),'_',num2str(1),'.png'));
    saveas(gcf,strcat('faults/scatter_out_',num2str(core),'.png'));
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

