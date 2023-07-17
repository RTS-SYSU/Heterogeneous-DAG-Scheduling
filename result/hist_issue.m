close all

f=figure('Position', [100, 100, 600, 350]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

small = readmatrix(strcat('real/issue_small.txt'));
big = readmatrix(strcat('real/issue_big.txt'));

small_diff = small(:,2) - small(:,1); 
big_diff = big(:,2) - big(:,1); 

hist([small_diff,big_diff]);

xlim([-50 1000])

ylabel('Frequency (count)','FontSize', 14)
xlabel('Makespan Differential','FontSize', 14)



legend("statemate", "mpeg2",'Orientation','vertical','location','northeast','FontSize', 14);


set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('figs/issue.png'));
saveas(gcf,strcat('figs/issue.eps'), 'epsc');


