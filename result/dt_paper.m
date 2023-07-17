close all

f=figure('Position', [100, 100, 400, 350]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

data = readmatrix(strcat('real/real.txt'));

% boxplot(data);

normalizer = max(max(data(:,1:2)));

boxplot([data(:,1), data(:,2)],  'symbol' , '-'); % , 'Whisker', 5

xticklables = ["AJLR","WFD"];
set(gca,'xticklabel',xticklables,'FontSize', 12);

% ylim([5700 5900])


ylabel('Makespan','FontSize', 14)

c = findobj(gca,'Tag','Box');

set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('figs/simu.png'));
saveas(gcf,strcat('figs/simu.eps'), 'epsc');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

f=figure('Position', [100, 100, 400, 350]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

col = 1;

AJLR = load("AJLR.out");
WFD = load("WFD.out");
FIFO = load("FIFO.out");

boxplot([AJLR(:,col), WFD(:,col)],  'symbol' , '-'); % , 'Whisker', 5

xticklables = ["AJLR","WFD"];
set(gca,'xticklabel',xticklables,'FontSize', 12);

ylim([5700 5900])

ylabel('Makespan','FontSize', 14)

set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('figs/testbed.png'));
saveas(gcf,strcat('figs/testbed.eps'), 'epsc');
