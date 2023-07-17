close all

len = 600;
wid = 1200;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
for core = cores
    for percent = percents
        allData = zeros(0,length(types) * 2);
        xCol = zeros(0);
        
        maxY = 0;
        minY = 0;
        
        for effect = fine_effects
            data = readmatrix(strcat('../faults_new/out_',num2str(core),'_',num2str(percent),'_',num2str(effect),'_',num2str(1),'.txt'));
           
            data = data(1,:);
            [row, col] = size(data);
            allData = [allData; data];
            
            subxCol = zeros(0);
            for i = 1:row
                subxCol = [subxCol; effect];
            end
            
            xCol = [xCol; subxCol];
        end
        
%         f = figure('Position', [100, 100, wid, len]);
        
        maxY = max(max(allData));
        minY = min(min(allData));
        
        subplotIndex = 1;
        for i = 1:length(types)
            if any(ignoredType==i)
                continue;
            end
            
            f = figure('Position', [100, 100, 400, 250]);
            
%             subplot((length(types)-length(ignoredType))/3,3,subplotIndex);
%             subplotIndex = subplotIndex + 1;
            
            col1 = (i-1) * 2 + 1;
            col2 = (i-1) * 2 + 2;
            
            dataCol1 = allData(:,col1);
            dataCol2 = allData(:,col2);
            
            scatter(xCol, dataCol1, 5, 'MarkerFaceColor','b');
            hold on
            scatter(xCol, dataCol2, 5, 'MarkerFaceColor','r');
            
            xticksNum = 0:max(fine_effects)/5:max(fine_effects);
            
            ylim([minY maxY]);
            set(gca,'xtick',xticksNum );
            set(gca,'xticklabel',xticksNum  / 10);
            xlabel('variation (%) on ET of chosen nodes','FontSize', 12)
            ylabel('makespan','FontSize', 12)
%             title(types(i),'FontSize', 12,'Interpreter','latex');
            
            c = findobj(gca,'Tag','Box');
            
            h = legend("low", "high",'location','northwest','Orientation','vertical');
            legend boxoff
            
            set(h,'FontSize',12,'color','none');
            
            saveas(gcf,strcat('../faults_figs/carvb_scatter_',num2str(core),'_',num2str(percent),'_',types_names(i),'.png'));
        end
        
        %     text(strcat("DAG makespan when varations occur on different nodes, percentage of varied nodes = ", num2str(percent * 100),'%') ,'FontSize',14,'Position',[-25.2141078838174 -252820.791666667 0]);
%         saveas(gcf,strcat('../faults_figs/scatter_vary_',num2str(core),'_',num2str(percent),'.png'));
    end
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%