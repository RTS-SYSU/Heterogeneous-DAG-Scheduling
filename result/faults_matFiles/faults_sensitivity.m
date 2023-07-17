ignoredType = [1, 2, 3, 4, 5, 6, 7];
wid = 800;
len = 300*4.2;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

for core = cores
    f = figure('Position', [100, 100, wid, len]);
    subplotIndex = 1;
    
    for percent = percents
        noColumn = length(types) *2;
        allData = cell(length(effects),1);
        
        minY = 0;
        maxY = 0;
        
        count =1;
        for effect = effects
            data = {readmatrix(strcat('../faults_new/out_',num2str(core),'_',num2str(percent),'_',effect,'_',num2str(1),'.txt'))};
            allData(count) = data;
            count = count + 1;
            maxY = max(maxY,max(max(data{1})));
            minY = min(minY,min(min(data{1})));
        end
       
        for i = 1:length(types)
            if any(ignoredType==i)
                continue;
            end
            
            col1 = (i-1) * 2 + 1;
            col2 = (i-1) * 2 + 2;
            
            subplot(5,2,subplotIndex);
            subplotIndex = subplotIndex + 1
            
            %     nexttile
            for j = 1:length(effects)
                data = allData{j};
                
                dataCol1 = data(:,col1);
                dataCol2 = data(:,col2);
                
                boxplot(dataCol1, 'position', (j-1) * 2 + 1, 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
                hold on;
                boxplot(dataCol2, 'position', (j-1) * 2 + 2, 'widths', 0.65, 'symbol','.', 'color', colors(2,:));
                hold on;
            end
            
            xticksNum = strings(length(effects),1);
            for k = 1:length(effects)
                xticksNum(k) = strcat(num2str(str2num(effects(k)) * 200),"%");
            end
            
            ylim([minY-5000 maxY+10000])
            set(gca,'xtick',1.5:2:length(effects)*2);
            set(gca,'xticklabel',xticksNum);
            xlabel('variation of ET on chosen nodes','FontSize', 12)
            ylabel('makespan','FontSize', 12)
            title(types(i),'FontSize', 12);
            
            c = findobj(gca,'Tag','Box');
            
            if i==1
                h = legend([c(2),c(1)],"vary no node", "vary all nodes",'location','northwest','Orientation','vertical');
            else
                h = legend([c(2),c(1)],strcat("low"), strcat("high"),'location','northwest','Orientation','vertical');
            end
            legend boxoff
            
            set(h,'FontSize',12,'color','none');
        end
        
        saveas(gcf,strcat('../faults_figs/vary_',num2str(core),'_',num2str(percent),'_sensivitity.png'));
    end
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
for core = cores
    
    f = figure('Position', [100, 100, wid, len]);
    subplotIndex = 1;
    
    
    for percent = percents
        data = readmatrix(strcat('../faults_new/out_',num2str(core),'_',num2str(percent),'_',num2str(effect),'_',num2str(1),'.txt'));
        ways = length(types) * 2;
        
        count=1;
        stats_pair = zeros(0,3);
        for i = 1:2:ways
            [h,p,ks2stat] = kstest2(data(:,i), data(:,i+1),'Alpha',0.1);
            stats_pair(count,:) = [h,p,ks2stat];
            count = count +1;
        end
        
        count=1;
        stats = zeros(0,3);
        for i = 1:2:(ways-2)
            [h,p,ks2stat] = kstest2(data(:,i), data(:,i+2));
            stats_low(count,:) = [h,p,ks2stat];
            count = count +1;
        end
        
        count=1;
        normalizedStatus=zeros(0,1)
        for i = 1:length(stats_pair)
            normalizedStatus(count,:) = (stats_pair(i,3) *10000) / ( stats_pair(2,3)*10000 + stats_pair(3,3)*10000+stats_pair(6,3)*10000+stats_pair(7,3)*10000)
            count = count +1;
        end
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
        percentile = [80 90 95 99 99.9 99.99 100];
        xtick = 1: length(percentile);
        
        minY = 10000000;
        maxY = 0;
        
        for i = 1:2:ways
            maxLocal = max(max(prctile(data(:,i),percentile,"all")), max(prctile(data(:,i+1),percentile,"all")));
            minLocal = min(min(prctile(data(:,i),percentile,"all")), min(prctile(data(:,i+1),percentile,"all")));
            
            maxY = max(maxY, maxLocal);
            minY = min(minY, minLocal);
        end
        
        for i = 1:2:ways
            if any(ignoredType==(i+1)/2)
                continue;
            end
            subplot(5,2,subplotIndex);
            subplotIndex = subplotIndex + 1;
            
            %         plot(xtick, prctile(data(:,i),percentile,"all") ,'-x', xtick, prctile(data(:,i+1),percentile,"all"),'-o',xtick, prctile(data(:,1),percentile,"all"),'-+',xtick, prctile(data(:,2),percentile,"all"),'-*', 'LineWidth', 2, 'MarkerSize', 10);
            plot(xtick, prctile(data(:,i),percentile,"all") ,'-x', xtick, prctile(data(:,i+1),percentile,"all"),'-o', 'LineWidth', 2, 'MarkerSize', 10);
            set(gca,'xtick', 1:length(xtick) );
            set(gca,'xticklabel',percentile,'fontsize',12);
            
            xlim([1 length(xtick)])
            ylim([minY-5000 maxY+10000])
            
            xlabel('level of confidence (%)','FontSize', 12)
            ylabel('makespan','FontSize', 12)
            title(types((i+1)/2),'FontSize', 12);
            
            if i==1
                h = legend("vary no node", "vary all nodes");
            else
                h = legend(strcat("low"), strcat("high"));
            end
            
            set(h,'FontSize',12, 'FontAngle','italic','location','northwest','Orientation','vertical');
            legend boxoff
        end
        
        saveas(gcf,strcat('../faults_figs/predict_',num2str(core),'_',num2str(percent),'_sensivitity.png'));
        
    end
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%