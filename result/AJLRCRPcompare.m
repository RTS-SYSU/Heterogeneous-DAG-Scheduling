close all

f=figure('Position', [100, 100, 600, 350]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

false = readmatrix(strcat('real/false_crp.txt'));

boxplot(false)

xticklables = ["Synthetic","Real"];
set(gca,'xticklabel',xticklables,'FontSize', 12);

% ylim([5700 5900])

xlabel('AJLR with different CRP profile','FontSize', 14)
ylabel('Makespan','FontSize', 14)

c = findobj(gca,'Tag','Box');

set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('figs/compareCRP.png'));
saveas(gcf,strcat('figs/compareCRP.eps'), 'epsc');


