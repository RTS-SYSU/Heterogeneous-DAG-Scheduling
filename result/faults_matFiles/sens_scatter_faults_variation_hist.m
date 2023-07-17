close all

len = 300;
wid = 1200;

percents = [0.3];

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
for core = cores
    for percent = percents
        allData = zeros(0,length(types) * 2);
        xCol = zeros(0);
        
        maxY = 0;
        minY = 0;
        
        for effect = effects
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
        
        f = figure('Position', [100, 100, wid, len]);
        
        maxY = max(max(allData));
        minY = min(min(allData));
        
        subplotIndex = 1;
        for i = 8:length(sens_types)
            
            subplot(1,3,subplotIndex);
            subplotIndex = subplotIndex + 1;
            
            col1 = (i-1) * 2 + 1;
            col2 = (i-1) * 2 + 2;
            
            dataCol1 = allData(:,col1);
            dataCol2 = allData(:,col2);
            
            scatter(xCol, dataCol1, 5, 'MarkerFaceColor','b');
            hold on
            scatter(xCol, dataCol2, 5, 'MarkerFaceColor','r');
            
            xticksNum = 0:max(effects)/5:max(effects);
            
            ylim([minY maxY]);
            set(gca,'xtick',xticksNum );
            set(gca,'xticklabel',xticksNum  / 10);
            xlabel('variation (%) on ET of chosen nodes','FontSize', 12)
            ylabel('makespan','FontSize', 12)
            title(sens_types(i),'FontSize', 12);
            
            c = findobj(gca,'Tag','Box');
            
            h = legend("vary low", "vary high",'location','northoutside','Orientation','horizontal');
            legend boxoff
            
            set(h,'FontSize',12,'color','none');
        end
        
        %     text(strcat("DAG makespan when varations occur on different nodes, percentage of varied nodes = ", num2str(percent * 100),'%') ,'FontSize',14,'Position',[-25.2141078838174 -252820.791666667 0]);
        saveas(gcf,strcat('../faults_figs/scatter_vary_',num2str(core),'_',num2str(percent),'.png'));
        
        
        
        [h0,p0,ks2stat0] = kstest2(allData(:,7), allData(:,8));
        [h1,p1,ks2stat1] = kstest2(allData(:,15), allData(:,16));
        [h2,p2,ks2stat2] = kstest2(allData(:,17), allData(:,18));
        [h3,p3,ks2stat3] = kstest2(allData(:,19), allData(:,20));
        
        ks2stat1
        ks2stat2
        ks2stat3
    end
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%