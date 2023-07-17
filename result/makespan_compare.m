close all;
taskNum = 8;

for duration = 1 : 8

    f=figure('Position', [100, 100, 1200, 400]);
    set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

    data = readmatrix(strcat('taskNum/','util_compare_',num2str(duration), '.txt'));

    boxplot(data);
    
    colsNum = size(data,2) - 1;

    xlim([1 colsNum+1]);
    xlabel({'Instances of DAG-1'})
%     ylim([min(min(data))-0.1 max(max(data))+0.1]);
    ylabel('Speed Up based on Normalised makespan','FontSize', 12)



    saveas(gcf,strcat('taskNum/','Z_duration_compare_',num2str(duration),'.png'));
end
