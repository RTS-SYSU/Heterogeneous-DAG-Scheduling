close all
colors=[[0.8500 0.3250 0.0980]; [0 0.4470 0.7410];  [0.9290 0.6940 0.1250]; [0.9290, 0.6940, 0.1250]; [0.4940, 0.1840, 0.5560]; [0.4660, 0.6740, 0.1880]; [0.3010, 0.7450, 0.9330]; [0.6350, 0.0780, 0.1840]];


f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);


recency_value = readmatrix("recency_value.txt");
recency_value1 = recency_value(recency_value<=0.5);

recency_distance = readmatrix("recency_distance.txt")/1000;
recency_distance1 = recency_distance(recency_distance<=10);
recency_distance1_show = recency_distance1/5;

plot(recency_distance1_show,(recency_value1*100),'LineWidth',2,'color',colors(1,:));
hold on;


recency_distance2_temp = recency_distance(10<recency_distance);
recency_distance2 = recency_distance2_temp(recency_distance2_temp<=100);
recency_distance2_show = recency_distance2/20 + 1.5 ;

%%%%%%%%% fully covered lines %%%%%%%%%%
recency_distance2_show = rescale(recency_distance2_show,2,6.5);

recency_value2_temp = recency_value(0.5<recency_value);
recency_value2 = recency_value2_temp(recency_value2_temp<=0.8);
recency_value2_show = (recency_value2*100);
recency_value2_show = rescale(recency_value2_show,50,80);

plot(recency_distance2_show,recency_value2_show,'LineWidth',2,'color',colors(2,:));
hold on;


recency_distance3 = recency_distance(recency_distance>100);
recency_distance3_show = recency_distance3/50 + 4.5;

%%%%%%%%% fully covered lines %%%%%%%%%%
recency_distance3_show = rescale(recency_distance3_show,6.5,14.5);

recency_value3 = recency_value(recency_value>0.8);
recency_value3_show = (recency_value3*100);
recency_value3_show = rescale(recency_value3_show,80,100);

plot(recency_distance3_show,recency_value3_show,'LineWidth',2,'color',colors(3,:));
hold on;

%%%%%%%%% equal dashed lines %%%%%%%%%%
recency_distance2_show1 = rescale(recency_distance2_show,0,2);
recency_value2_show1 = rescale(recency_value2_show,50,50);
plot(recency_distance2_show1,recency_value2_show1,'--','LineWidth',2,'color',colors(2,:));
hold on;

recency_distance3_show1 = rescale(recency_distance3_show,0,6.5);
recency_value3_show1 = rescale(recency_value3_show,80,80);
plot(recency_distance3_show1,recency_value3_show1,'--','LineWidth',2,'color',colors(3,:));
hold on;

line([0 14.5], [30 30],'LineStyle',':','color','k');
line([0 14.5], [50 50],'LineStyle',':','color','k');
line([0 14.5], [80 80],'LineStyle',':','color','k');
line([2 2], [0 100],'LineStyle',':','color','k');
line([6.5 6.5], [0 100],'LineStyle',':','color','k');

xtciks = zeros(1);
xtciklables=string(1);
count = 1;
for i = 0 :0.1: 15
    xtciks(count) = i;
    if (i<=2)
        xtciklables(count) = num2str(i);
    elseif (i<=6.5)
         xtciklables(count) = num2str(i*10);
    else
         xtciklables(count) = num2str(i*100);
    end
    
    count=count+1;
    
end

xlim([0,14.5]);
set(gca,'xtick',[0, 2, 6.5 14.5] );
set(gca,'xticklabel',[0, 10, 100, 500]);

ylim([0, 100]);

ax = gca;
ax.FontSize = 12; 

xlabel({'Recency distance'},'FontSize', 14);
ylabel({'Speedup over WCET (%)'},'FontSize', 14)

h=legend("Recency for core", "Recency for cluster", "Recency for system",'location','southeast');
set(h,'FontSize',14);

saveas(gcf,strcat('figs/ep_recency.eps'), 'epsc');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

x = linspace(0,2);
y = x.*x.*x;
y_show = rescale(y,30,50);
plot(x,y_show,'LineWidth',2,'color',colors(1,:))
hold on 

x = linspace(2,6.5);
y = x.*x.*x;
y_show = rescale(y,50,80);
plot(x,y_show,'LineWidth',2,'color',colors(2,:))
hold on

x = linspace(6.5,14.5);
y = x.*x.*x;
y_show = rescale(y,80,100);
plot(x,y_show,'LineWidth',2,'color',colors(3,:))
hold on

%%%%%%%%% equal dashed lines %%%%%%%%%%
recency_distance2_show1 = rescale(recency_distance2_show,0,2);
recency_value2_show1 = rescale(recency_value2_show,50,50);
plot(recency_distance2_show1,recency_value2_show1,'--','LineWidth',2,'color',colors(2,:));
hold on;

recency_distance3_show1 = rescale(recency_distance3_show,0,6.5);
recency_value3_show1 = rescale(recency_value3_show,80,80);
plot(recency_distance3_show1,recency_value3_show1,'--','LineWidth',2,'color',colors(3,:));
hold on;

line([0 14.5], [30 30],'LineStyle',':','color','k');
line([0 14.5], [50 50],'LineStyle',':','color','k');
line([0 14.5], [80 80],'LineStyle',':','color','k');
line([2 2], [0 100],'LineStyle',':','color','k');
line([6.5 6.5], [0 100],'LineStyle',':','color','k');

xtciks = zeros(1);
xtciklables=string(1);
count = 1;
for i = 0 :0.1: 15
    xtciks(count) = i;
    if (i<=2)
        xtciklables(count) = num2str(i);
    elseif (i<=6.5)
         xtciklables(count) = num2str(i*10);
    else
         xtciklables(count) = num2str(i*100);
    end
    
    count=count+1;
    
end

xlim([0,14.5]);
set(gca,'xtick',[0, 2, 6.5 14.5] );
set(gca,'xticklabel',[0, 10, 100, 500]);

ylim([0, 100]);

ax = gca;
ax.FontSize = 12; 

xlabel({'Recency distance'},'FontSize', 14);
ylabel({'Speedup over WCET (%)'},'FontSize', 14)

h=legend("Recency for core", "Recency for cluster", "Recency for system",'location','southeast');
set(h,'FontSize',14);

saveas(gcf,strcat('figs/ep_recency_curve.eps'), 'epsc');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

f=figure('Position', [100, 100, wid, len]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

x = linspace ( 0 ,1 ,10000);
x_cal = rescale(x,-2,2);
x_show = rescale(x,0,2);
y=erf(x_cal);
y_show = rescale(rescale(y,30,50),30,50);
plot(x_show,y_show,'LineWidth',2,'color',colors(1,:))
hold on

x = linspace ( 10000 ,1 ,100000);
x_cal = rescale(x,-2,2);
x_show = rescale(x,2,6.5);
y = erf(x_cal);
y_show = rescale(y,50,80);
plot(x_show,y_show,'LineWidth',2,'color',colors(2,:))
hold on

x = linspace ( 100000 ,1 ,500000);
x_cal = rescale(x,-2,2);
x_show = rescale(x,6.5,14.5);
y = erf(x_cal);
y_show = rescale(y,80,100);
plot(x_show,y_show,'LineWidth',2,'color',colors(3,:))
hold on

%%%%%%%%% equal dashed lines %%%%%%%%%%
recency_distance2_show1 = rescale(recency_distance2_show,0,2);
recency_value2_show1 = rescale(recency_value2_show,50,50);
plot(recency_distance2_show1,recency_value2_show1,'--','LineWidth',2,'color',colors(2,:));
hold on;

recency_distance3_show1 = rescale(recency_distance3_show,0,6.5);
recency_value3_show1 = rescale(recency_value3_show,80,80);
plot(recency_distance3_show1,recency_value3_show1,'--','LineWidth',2,'color',colors(3,:));
hold on;

line([0 14.5], [30 30],'LineStyle',':','color','k');
line([0 14.5], [50 50],'LineStyle',':','color','k');
line([0 14.5], [80 80],'LineStyle',':','color','k');
line([2 2], [0 100],'LineStyle',':','color','k');
line([6.5 6.5], [0 100],'LineStyle',':','color','k');

xtciks = zeros(1);
xtciklables=string(1);
count = 1;
for i = 0 :0.1: 15
    xtciks(count) = i;
    if (i<=2)
        xtciklables(count) = num2str(i);
    elseif (i<=6.5)
         xtciklables(count) = num2str(i*10);
    else
         xtciklables(count) = num2str(i*100);
    end
    
    count=count+1;
    
end

xlim([0,14.5]);
set(gca,'xtick',[0, 2, 6.5 14.5] );
set(gca,'xticklabel',[0, 10, 100, 500]);

ylim([0, 100]);

ax = gca;
ax.FontSize = 12; 

xlabel({'Recency distance'},'FontSize', 14);
ylabel({'Speedup over WCET (%)'},'FontSize', 14)

h=legend("Recency for core", "Recency for cluster", "Recency for system",'location','southeast');
set(h,'FontSize',14);

saveas(gcf,strcat('figs/ep_recency_step.eps'), 'epsc');