

absolute = 1;

f=figure('Position', [100, 100, 800, 350]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

data = readmatrix(strcat('real/cores.txt'));

col = size(data,2);

if absolute == 0
    for i = 1 : 2 : col
        normalizer = max(max(data(:,i)),max(data(:,i+1)));
        
        boxplot(data(:,i)/normalizer, 'position',i, 'widths', 0.65, 'symbol','.', 'color', 'b');
        hold on
        
        boxplot(data(:,i+1)/normalizer, 'position',i+1, 'widths', 0.65, 'symbol','.', 'color', 'r');
        hold on
    end
else
    for i = 1 : col
        if(mod(i,2) == 1)
            boxplot(data(:,i), 'position',i, 'widths', 0.65, 'symbol','.', 'color', 'b');
            hold on
        else
            boxplot(data(:,i), 'position',i, 'widths', 0.65, 'symbol','.', 'color', 'r');
            hold on
        end
    end
end

xticklables= 3 : col;
set(gca,'xticklabel',xticklables,'FontSize', 14);
xticks = 1.5 : 2 : col;
set(gca,'xtick',xticks );

c = findobj(gca,'Tag','Box');
legend([c(2),c(1)],"AJLR", "WF",'location','southeast','FontSize', 14);

ylabel('Absolute Makespan','FontSize', 16)
xlabel('Number of Cores','FontSize', 16)

%% number of tasks

startTaskNum = 5;

f=figure('Position', [100, 100, 800, 350]);
set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);

data = readmatrix(strcat('real/tasks.txt'));

col = size(data,2);

if absolute == 0
    for i = 1 : 2 : col
        normalizer = max(max(data(:,i)),max(data(:,i+1)));
        
        boxplot(data(:,i)/normalizer, 'position',i, 'widths', 0.65, 'symbol','.', 'color', 'b');
        hold on
        
        boxplot(data(:,i+1)/normalizer, 'position',i+1, 'widths', 0.65, 'symbol','.', 'color', 'r');
        hold on
    end
else
    for i = 1 : col
        if(mod(i,2) == 1)
            boxplot(data(:,i), 'position',i, 'widths', 0.65, 'symbol','.', 'color', 'b');
            hold on
        else
            boxplot(data(:,i), 'position',i, 'widths', 0.65, 'symbol','.', 'color', 'r');
            hold on
        end
    end
end

xticklables= startTaskNum : col;
set(gca,'xticklabel',xticklables,'FontSize', 14);
xticks = 1.5 : 2 : col;
set(gca,'xtick',xticks );

c = findobj(gca,'Tag','Box');
legend([c(2),c(1)],"AJLR", "WF",'location','southeast','FontSize', 14);

ylabel('Absolute Makespan','FontSize', 16)
xlabel('Number of Tasks','FontSize', 16)
