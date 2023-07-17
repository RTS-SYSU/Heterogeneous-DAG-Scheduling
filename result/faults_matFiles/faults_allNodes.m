%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
for core = cores
    f = figure('Position', [100, 100, 400, 200]);
    allData = cell(length(effects),1);
    
    minY = 0;
    maxY = 0;
    
    count =1;
    for effect = effects
        data = {readmatrix(strcat('../faults_new/out_',num2str(4),'_',num2str(0.1),'_',effect,'_',num2str(1),'.txt'))};
        allData(count) = data;
        count = count + 1;
        maxY = max(maxY,max(max(data{1})));
        minY = min(minY,min(min(data{1})));
    end
    
    for j = 1:length(effects)
        data = allData{j};
        
        dataCol1 = data(:,1);
        dataCol2 = data(:,2);
        
        boxplot(data(:,1), 'position', (j-1) * 2 + 1, 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
        hold on;
        boxplot(data(:,2), 'position', (j-1) * 2 + 2, 'widths', 0.65, 'symbol','.', 'color', colors(2,:));
        hold on;
    end
    
    xticksNum = strings(length(effects),1);
    for k = 1:length(effects)
        xticksNum(k) = strcat(num2str(str2num(effects(k)) * 200),"%");
    end
    
    ylim([minY-5000 maxY+10000])
    set(gca,'xtick',1.5:2:length(effects)*2);
    set(gca,'xticklabel',xticksNum);
    xlabel('variation on node ET','FontSize', 12)
    ylabel('makespan','FontSize', 12)
    title(types(1),'FontSize', 12);
    
    c = findobj(gca,'Tag','Box');
    h = legend([c(2),c(1)],"vary no node", "vary all nodes",'location','northwest','Orientation','vertical');
    legend boxoff
    
    set(h,'FontSize',12,'color','none');
    saveas(gcf,strcat('../faults_figs/vary_',num2str(core),'_all.png'));
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%