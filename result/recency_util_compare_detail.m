maxUtil = 8;

f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
file_name = 'util_compare/sum_detail_';

count = 1;
for util = 1:5
    read = util + 0;
   if(read == 5)
       data = readmatrix(strcat(file_name, '4.0', '.txt'));
   elseif(read == 10)
       data = readmatrix(strcat(file_name, '8.0', '.txt'));
   else
       data = readmatrix(strcat(file_name, num2str(read*0.8), '.txt'));
   end

   rowsNum = size(data,1);
   
    for c = 1:2
        boxplot((data(:,c)), 'position',count, 'widths', 0.65, 'symbol','-', 'color', colors(c,:));
        hold on
        count = count + 1;
    end   

end

xlim([0 11]);
ylim([-10 600])
% axis 'auto y'
xticks = 1.5 : 2 : 11;
xticklables = ["10","20","30","40","50" ];

set(gca,'xtick',xticks );
set(gca,'xticklabel',xticklables);


ax = gca(); 
ax.TickLabelInterpreter = 'tex';
ax.FontSize = 12;

 xlabel('System utilisation (%)','FontSize', 14)
 ylabel('Delay','FontSize', 14)

c = findobj(gca,'Tag','Box');



 h = legend([c(2),c(1)],"AJLR v1.0","AJLR v2.0",'FontAngle','italic','location','northwest','Orientation','horizontal');
% h = legend([c(3),c(2),c(1)],"AJLR no error","AJLR with error","AJLRE with error",'FontAngle','italic','location','southeast','Orientation','horizontal');
%     h = legend([c(3),c(2),c(1)],"WF+EO","AJLR without deviations","AJLR with deviations",'FontAngle','italic','location','southeast','Orientation','horizontal');
set(h,'FontSize',14);


set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('figs/ep_util_compare_delay.png'));
saveas(gcf,strcat('figs/ep_util_compare_delay.eps'), 'epsc');



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
file_name = 'util_compare/sum_detail_';

count = 1;
for util = 1:5
    read = util + 0;
   if(read == 5)
       data = readmatrix(strcat(file_name, '4.0', '.txt'));
   elseif(read == 10)
       data = readmatrix(strcat(file_name, '8.0', '.txt'));
   else
       data = readmatrix(strcat(file_name, num2str(read*0.8), '.txt'));
   end

   rowsNum = size(data,1);
   
    for c = 3:4
        boxplot((data(:,c)), 'position',count, 'widths', 0.65, 'symbol','-', 'color', colors((c-2),:));
        hold on
        count = count + 1;
    end   

end

xlim([0 11]);
ylim([-100 10000])
% axis 'auto y'
xticks = 1.5 : 2 : 11;
xticklables = ["10","20","30","40","50" ];

set(gca,'xtick',xticks );
set(gca,'xticklabel',xticklables);


ax = gca(); 
ax.TickLabelInterpreter = 'tex';
ax.FontSize = 12;

 xlabel('System utilisation (%)','FontSize', 14)
 ylabel('Computation Time','FontSize', 14)

c = findobj(gca,'Tag','Box');



 h = legend([c(2),c(1)],"AJLR v1.0","AJLR v2.0",'FontAngle','italic','location','northwest','Orientation','horizontal');
% h = legend([c(3),c(2),c(1)],"AJLR no error","AJLR with error","AJLRE with error",'FontAngle','italic','location','southeast','Orientation','horizontal');
%     h = legend([c(3),c(2),c(1)],"WF+EO","AJLR without deviations","AJLR with deviations",'FontAngle','italic','location','southeast','Orientation','horizontal');
set(h,'FontSize',14);


set(gcf, 'PaperSize', [25 25])
saveas(gcf,strcat('figs/ep_util_compare_et.png'));
saveas(gcf,strcat('figs/ep_util_compare_et.eps'), 'epsc');