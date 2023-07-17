close all

% colors=[[0.8500 0.3250 0.0980]; [0 0.4470 0.7410];  [0.9290 0.6940 0.1250]; [0.4660, 0.6740, 0.1880]; [0.3010, 0.7450, 0.9330]; [0.6350, 0.0780, 0.1840]];
colors=["b"; "r"];
types = ["all","$C_j$","$\hat{L_k}$","$D^{in}_j$","$D^{out}_j$","$D^{in}_j + D^{out}_j$","$||G(v_j)||$","sensivitiy","sensivitiy*"];
types_names = ["all","nodeET","pathET","in_degree","out_degree","in_out_degree","pathNum","sensivitiy","stats_sensivitiy"];

% types = ["all","nodeET","pathET","in_degree","out_degree","in_out_degree","pathNum","basic sensivitity","K-S based sensivitiy","C-C based sensivitiy*"];

ignoredType = [1];

cores = [4];

% percents = [0.1,0.2,0.3,0.4,0.5];
percents = [0.3];

effects = 200:200:1000;%["0.2","0.4","0.6","0.8","1.0"];
instanceNums = [1,3,5,10];

row = 2;
col = 3;

fine_effects = 0:5:1000;

len = 600;
wid = 1200;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
for core = cores
    for percent = percents
        
        noColumn = length(types) *2;
        allData = cell(length(effects),1);
        
        minY = 0;
        maxY = 0;
        
        count =1;
        for effect = effects
            data = {readmatrix(strcat('../faults_new/out_',num2str(core),'_',num2str(percent),'_',num2str(effect),'_',num2str(1),'.txt'))};
            allData(count) = data;
            count = count + 1;
            maxY = max(maxY,max(max(data{1})));
            minY = min(minY,min(min(data{1})));
        end
        
        %         f = figure('Position', [100, 100, wid, len]);
        
        %         subplotIndex = 1;
        for i = 1:length(types)
            if any(ignoredType==i)
                continue;
            end
            
            col1 = (i-1) * 2 + 1;
            col2 = (i-1) * 2 + 2;
            
            
            f = figure('Position', [100, 100, 400, 200]);
            
            %             subplot(row,col,subplotIndex);
            %             subplotIndex = subplotIndex + 1
            
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
                xticksNum(k) = strcat(num2str(str2num(num2str(effects(k))) / 10), '%');
            end
            
            %             ylim([minY-5000 maxY+10000])
            set(gca,'xtick',1.5:2:length(effects)*2);
            set(gca,'xticklabel',xticksNum);
            xlabel('variation of ET on chosen nodes','FontSize', 12)
            ylabel('makespan','FontSize', 12)
            %             title(types(i),'FontSize', 12);
            
            c = findobj(gca,'Tag','Box');
            
            if i==1
                h = legend([c(2),c(1)],"vary no node", "vary all nodes",'location','northwest','Orientation','vertical');
            else
                h = legend([c(2),c(1)],strcat("low"), strcat("high"),'location','northwest','Orientation','vertical');
            end
            legend boxoff
            
            set(h,'FontSize',12,'color','none');
            
            saveas(gcf,strcat('../faults_figs/carvb_box_',num2str(core),'_',num2str(percent),'_',types_names(i),'.png'));
            saveas(gcf,strcat('../faults_figs/carvb_box_',num2str(core),'_',num2str(percent),'_',types_names(i),'.eps'),'epsc');
        end
        
        %     text(strcat("DAG makespan when varations occur on different nodes, percentage of varied nodes = ", num2str(percent * 100),'%') ,'FontSize',14,'Position',[-25.2141078838174 -252820.791666667 0]);
        %         saveas(gcf,strcat('../faults_figs/vary_',num2str(core),'_',num2str(percent),'.png'));
    end
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


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
            
            f = figure('Position', [100, 100, 400, 200]);
            
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
            saveas(gcf,strcat('../faults_figs/carvb_scatter_',num2str(core),'_',num2str(percent),'_',types_names(i),'.eps'),'epsc');
        end
        
        %     text(strcat("DAG makespan when varations occur on different nodes, percentage of varied nodes = ", num2str(percent * 100),'%') ,'FontSize',14,'Position',[-25.2141078838174 -252820.791666667 0]);
%         saveas(gcf,strcat('../faults_figs/scatter_vary_',num2str(core),'_',num2str(percent),'.png'));
    end
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
