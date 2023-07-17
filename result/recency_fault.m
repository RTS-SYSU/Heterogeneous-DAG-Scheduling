% close all
f=figure('Position', [100, 100, 850, 450]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

x = readmatrix("recency_fault/x0.txt");
y = readmatrix("recency_fault/y0.txt");
z = readmatrix("recency_fault/z0.txt");
% r = readmatrix("recency_fault/r0.txt");


% mesh(x,y,r)
% hold on,
mesh(y,x,z)

colormap(lines(5))

colorbar

xlabel({'Deviation rate P_r (%)'},'FontSize', 14);
ylabel({'Deviation effect P_e (%)'},'FontSize', 14)
zlabel({'Differential in normalised makespan'},'FontSize', 14)

% h = legend("WFD",'FontAngle','italic','location','west','Orientation','horizontal');
% set(h,'FontSize',14);

% axis tight
% view(26.1,20.5)
grid on

xh = get(gca,'XLabel'); % Handle of the x label
set(xh, 'Units', 'Normalized')
pos = get(xh, 'Position');
set(xh, 'Position',pos.*[0.9,0.4,1],'Rotation',-4)
yh = get(gca,'YLabel'); % Handle of the y label
set(yh, 'Units', 'Normalized')
pos = get(yh, 'Position');
set(yh, 'Position',pos.*[1.05,-0.3,1],'Rotation',20.5)

ax = gca(); 
ax.TickLabelInterpreter = 'tex';
ax.FontSize = 12; 


saveas(gcf,strcat('figs/ep_recency_fault.eps'), 'epsc');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% f=figure('Position', [100, 100, 800, 400]);
% set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
% 
% colormap(lines(5))
% 
% mesh(x,y,z)
% % hold on,
% % 
% % mesh(x,y,r)
% 
% view(0,90)
% colorbar
% 
% ax = gca(); 
% ax.TickLabelInterpreter = 'tex';
% ax.FontSize = 12; 
% 
% xlabel({'Deviation in percentage'},'FontSize', 14);
% ylabel({'Probability in percentage'},'FontSize', 14)
% zlabel({'Normalised makespan'},'FontSize', 14)
% 
% 
% 
% 
% saveas(gcf,strcat('figs/ep_recency_fault_2d.eps'), 'epsc');
