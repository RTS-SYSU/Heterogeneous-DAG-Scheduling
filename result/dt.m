close all

absolute = 1;

f=figure('Position', [100, 100, 800, 350]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

data = readmatrix(strcat('real/cores.txt'));
col = size(data,2);

index = 1;
if absolute == 0
    for i = 1 : 3 : col
        normalizer = max(max(data(:,i:i+1)));
        
        boxplot(data(:,i)/normalizer, 'position',index, 'widths', 0.65, 'symbol','.', 'color', 'b');
        hold on
        
        boxplot(data(:,i+1)/normalizer, 'position',index+1, 'widths', 0.65, 'symbol','.', 'color', 'r');
        hold on
        index = index +2;
%         boxplot(data(:,i+2)/normalizer, 'position',i+2, 'widths', 0.65, 'symbol','.', 'color', 'k');
%         hold on
    end
    ylabel('Normalised Makespan','FontSize', 16)
else
    for i = 1 : 3 : col
        boxplot(data(:,i), 'position',index, 'widths', 0.65, 'symbol','.', 'color', 'b');
        hold on
        
        boxplot(data(:,i+1), 'position',index+1, 'widths', 0.65, 'symbol','.', 'color', 'r');
        hold on
        index = index +2;
%         boxplot(data(:,i+2), 'position',i+2, 'widths', 0.65, 'symbol','.', 'color', 'k');
%         hold on
    end
    
    ylabel('Makespan','FontSize', 16)
end

xticklables= 3 : 8;
set(gca,'xticklabel',xticklables,'FontSize', 14);
xticks = 1.5:2: 12;

set(gca,'xtick',xticks );

c = findobj(gca,'Tag','Box');
legend([c(2),c(1)],"AJLR", "WFD",'Orientation','horizontal','location','northoutside','FontSize', 14);

xlabel('Number of Cores','FontSize', 16)

set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('figs/simu_cores.png'));
saveas(gcf,strcat('figs/simu_cores.eps'), 'epsc');

%% number of tasks

startTaskNum = 3;

f=figure('Position', [100, 100, 800, 350]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

data = readmatrix(strcat('real/tasks.txt'));

col = size(data,2);

index = 1;
if absolute == 0
    for i = 1 :3 : col
        normalizer = max(max(data(:,i:i+1)));
        
        boxplot(data(:,i)/normalizer, 'position',index, 'widths', 0.65, 'symbol','.', 'color', 'b');
        hold on
        
        boxplot(data(:,i+1)/normalizer, 'position',index+1, 'widths', 0.65, 'symbol','.', 'color', 'r');
        hold on
        index = index +2;
%         boxplot(data(:,i+2)/normalizer, 'position',i+2, 'widths', 0.65, 'symbol','.', 'color', 'k');
%         hold on
    end
    ylabel('Normalised Makespan','FontSize', 16)
else
    for i = 1 : 3: col
        boxplot(data(:,i), 'position',index, 'widths', 0.65, 'symbol','.', 'color', 'b');
        hold on
        
        boxplot(data(:,i+1), 'position',index+1, 'widths', 0.65, 'symbol','.', 'color', 'r');
        hold on
        index = index +2;
%         boxplot(data(:,i+2), 'position',i+2, 'widths', 0.65, 'symbol','.', 'color', 'k');
%         hold on
    end
    ylabel('Makespan','FontSize', 16)
end

xticklables= startTaskNum : col;
set(gca,'xticklabel',xticklables,'FontSize', 14);
xticks = 1.5 : 2 : col;
set(gca,'xtick',xticks );

c = findobj(gca,'Tag','Box');
legend([c(2),c(1)],"AJLR", "WFD",'Orientation','horizontal','location','northoutside','FontSize', 14);

xlabel('Number of Tasks','FontSize', 16)

set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('figs/simu_tasks.png'));
saveas(gcf,strcat('figs/simu_tasks.eps'), 'epsc');

%% Utilization
f=figure('Position', [100, 100, 800, 350]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

data = readmatrix(strcat('real/utils.txt'));

col = size(data,2);

index=1;
if absolute == 0
    for i = 1 :3 : col
        normalizer = max(max(data(:,i:i+1)));
        
        boxplot(data(:,i)/normalizer, 'position',index, 'widths', 0.65, 'symbol','.', 'color', 'b');
        hold on
        
        boxplot(data(:,i+1)/normalizer, 'position',index+1, 'widths', 0.65, 'symbol','.', 'color', 'r');
        hold on
        
        index = index + 2;
%         boxplot(data(:,i+2)/normalizer, 'position',i+2, 'widths', 0.65, 'symbol','.', 'color', 'k');
%         hold on
    end
    ylabel('Normalised Makespan','FontSize', 16)
else
    for i = 1 : 3: col
        boxplot(data(:,i), 'position',index, 'widths', 0.65, 'symbol','.', 'color', 'b');
        hold on
        
        boxplot(data(:,i+1), 'position',index+1, 'widths', 0.65, 'symbol','.', 'color', 'r');
        hold on
        
        index = index +2;
%         boxplot(data(:,i+2), 'position',i+2, 'widths', 0.65, 'symbol','.', 'color', 'k');
%         hold on
    end
    ylabel('Makespan','FontSize', 16)
end

xticklables = 0.5 : 0.5 : 3.0;
set(gca,'xticklabel',xticklables,'FontSize', 14);
xticks = 1.5 : 2 : col;
set(gca,'xtick',xticks );

c = findobj(gca,'Tag','Box');
legend([c(2),c(1)],"AJLR", "WFD", "FIFO",'Orientation','horizontal','location','northoutside','FontSize', 14);

xlabel('System Utilization','FontSize', 16)

set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('figs/simu_utils.png'));
saveas(gcf,strcat('figs/simu_utils.eps'), 'epsc');