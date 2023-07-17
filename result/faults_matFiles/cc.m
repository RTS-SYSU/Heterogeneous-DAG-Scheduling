close all

types = ["nodeET","pathET","in_degree","out_degree","in_out_degree","pathNum"];
percents = ["0.1", "0.2", "0.3", "0.4", "0.5"];

subRows = 2;
subCols = 3;

for core = cores
    
    f = figure('Position', [100, 100, 1200, 800]);
    subplotIndex = 1;
    for percent = percents
        
        subplot(subRows, subCols, subplotIndex);
        subplotIndex = subplotIndex + 1;
        
        data = readmatrix(strcat('../faults_new/cc_',num2str(core),'_',percent,'_',num2str(1),'.txt'));
        [rows, cols] = size(data);
        
        for i = 1:2:cols
            %             boxplot(data(:,i), 'position', i, 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
            %             hold on;
            boxplot(data(:,i+1), 'position', (i-1)/2+1, 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
            hold on;
        end
        
        xticksNum = strings(length(types),1);
        for k = 1:length(types)
            xticksNum(k) = types(k);
        end
        
        set(gca,'xtick',1:length(types));
        set(gca,'xticklabel',xticksNum);
        xlabel('Different node types','FontSize', 12)
        title(types(1),'FontSize', 12);
        
        %         c = findobj(gca,'Tag','Box');
        %         h = legend([c(2),c(1)],"sum of ET variation", "makespan",'location','northwest','Orientation','vertical');
        %         legend boxoff
        %         set(h,'FontSize',12,'color','none');
        
        saveas(gcf,strcat('../faults_figs/cc_',num2str(core),'_',num2str(percent),'_all.png'));
        
    end
    
    
    for percent = percents
        %         allData = zeros(0,length(types) * 2);
        data = readmatrix(strcat('../faults_new/cc_',num2str(core),'_',percent,'_',num2str(1),'.txt'));
        %         allData = [allData; data]
        
        count=1;
        cc_result = zeros(0,2);
        
        for i = 1:2:length(types)*2
            [r, p] = corrcoef(data(:,i), data(:,i+1));
            cc_result(count,:) = [r(1,2),p(1,2)];
            count = count +1;
        end
        
        disp(cc_result)
    end
    
end

percent = "0.4";
data = readmatrix(strcat('../faults_new/cc_',num2str(4),'_',percent,'_',num2str(1),'.txt'));
dd = zeros(0,0);
for i = [ 1 3 5 7 9 11]
    dd= [dd data(:,i+1)];
end
[coeff,score,latent,~,explained] = pca(dd);
explained

