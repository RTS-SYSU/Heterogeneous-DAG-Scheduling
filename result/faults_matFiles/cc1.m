

types = ["nodeET","pathET","in_degree","out_degree","in_out_degree","pathNum"];
percents = ["0.1","0.2","0.3","0.4","0.5"];

subRows = 2;
subCols = 3;

for core = cores
    f = figure('Position', [100, 100, 1200, 800]);
    subplotIndex = 1;
    
    for percent = percents
        
        subplot(subRows, subCols, subplotIndex);
        subplotIndex = subplotIndex + 1;
        
        allData = zeros(0,length(types) * 2);
        for effect = effects
            data = readmatrix(strcat('../faults_new/out_',num2str(core),'_',percent,'_',num2str(effect),'.0_',num2str(1),'.txt'));
            allData = [allData; data];
        end
        
        [rows, cols] = size(allData);
        
        minY = min(min(allData));
        maxY = max(max(allData));
        
        for i = 3:2:14
            %             boxplot(data(:,i), 'position', i, 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
            %             hold on;
            boxplot(allData(:,i+1), 'position', (i-1)/2+1, 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
            hold on;
        end
        
        xticksNum = strings(length(types),1);
        for k = 1:length(types)
            xticksNum(k) = types(k);
        end
        
        %         ylim([minY maxY])
        set(gca,'xtick',1:length(types));
        set(gca,'xticklabel',xticksNum);
        xlabel('Different node types','FontSize', 12)
        title(types(1),'FontSize', 12);
        
        %         c = findobj(gca,'Tag','Box');
        %         h = legend([c(2),c(1)],"sum of ET variation", "makespan",'location','northwest','Orientation','vertical');
        %         legend boxoff
        %         set(h,'FontSize',12,'color','none');
    end
    saveas(gcf,strcat('../faults_figs/cc_',num2str(core),'_',num2str(percent),'_all.png'));
    
end



for core = cores
    for percent = percents
        allData = zeros(0,length(types) * 2);
        
        allXCol = zeros(0);
        
        for effect = effects
            data = readmatrix(strcat('../faults_new/out_',num2str(core),'_',percent,'_',num2str(effect),'.0_',num2str(1),'.txt'));
            data = data(1:10,:);
            
            [row, col] = size(data);
            allData = [allData; data];
            
            xCol = zeros(1,row);
            xCol = xCol + (effect * str2num(percent) * 10);
            
            allXCol = [allXCol; xCol]
        end
        
%         count=1;
%         cc_result = zeros(0,2);
%         for i = 3:2:14
%             [r, p] = corrcoef(allData(:,i), allData(:,i+1));
%             cc_result(count,:) = [r(1,2),p(1,2)];
%             count = count +1;
%         end
%         disp(cc_result)
        
        count=1;
        cc_result = zeros(0,2);
        for i = 3:2:14
            [r, p] = corrcoef(allXCol, allData(:,i+1));
            cc_result(count,:) = [r(1,2),p(1,2)];
            count = count +1;
        end
        disp(cc_result)
    end
end

%     figure('Position', [100, 100, 1200, 800]);
%     for i = 1:2:cols
%         scatter(allData(:,i), allData(:,i+1));
%         hold on;
%     end
%
%     h = legend(types, "Location", "northeast");
%     legend boxoff
%     set(h,'FontSize',12,'color','none');

