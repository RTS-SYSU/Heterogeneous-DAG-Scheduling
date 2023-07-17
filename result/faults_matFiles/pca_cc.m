close all

colors=["b"; "r"];
types = ["nodeET","pathET","in_degree","out_degree","in_out_degree","pathNum"];
% judgement = ["0.1", "0.2", "0.3", "0.4", "0.5"];
judgements = ["0.2"];
threads = 0:3;

for core = cores
    for judgement = judgements
        
        
        
        allData = zeros(0);
        for thread = threads
            data = readmatrix(strcat('../faults_new/random_',num2str(core),'_',judgement,'_',num2str(thread),'.txt'));
            allData = [allData; data(:,1:2:13)];
        end
        [rows, cols] = size(allData);
        
        %         allDataAnalysis = zeros(0);
        %         for i = [1:2:cols ]
        %             allDataAnalysis = [allDataAnalysis data(:,i)];
        %         end
        
        
        
        allDataAnalysis = allData();
        
        
        
%         normA = allDataAnalysis - min(allDataAnalysis(:));
%         normA = normA ./ max(normA(:)); % *;

%         allDataAnalysis = (allDataAnalysis - mean(allDataAnalysis)) ./ std(allDataAnalysis);
        
        [coeff,score,latent,~,explained] = pca(allDataAnalysis);
        coeff
        explained
        
%         [r, p] = corrcoef(normA);
%         r
%         p
%         
%          f = figure('Position', [100, 100, 1200, 800]);
%          boxplot(normA)
        
        %          f = figure('Position', [100, 100, 1200, 800]);
        %         count = 1;
        %         for i = [2:2:(cols) 13]
        %             %             boxplot(data(:,i), 'position', i, 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
        %             %             hold on;
        %             boxplot(data(:,i), 'position', count, 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
        %             hold on;
        %             count = count + 1;
        %         end
        
        %         count=1;
        %         cc_result = zeros(0,2);
        %         for i = 1:2:length(types)*2
        %             [r, p] = corrcoef(data);
        %             cc_result(count,:) = [r(1,2),p(1,2)];
        %             count = count +1;
        %         end
        %         disp(cc_result)
        
        
        %
        %         xticksNum = strings(length(types),1);
        %         for k = 1:length(types)
        %             xticksNum(k) = types(k);
        %         end
        %
        %         set(gca,'xtick',1:length(types));
        %         set(gca,'xticklabel',xticksNum);
        %         xlabel('Different node types','FontSize', 12)
        %         title(types(1),'FontSize', 12);
        
        %         c = findobj(gca,'Tag','Box');
        %         h = legend([c(2),c(1)],"sum of ET variation", "makespan",'location','northwest','Orientation','vertical');
        %         legend boxoff
        %         set(h,'FontSize',12,'color','none');
        
        %         saveas(gcf,strcat('../faults_figs/cc_',num2str(core),'_',num2str(percent),'_all.png'));
    end
    
    %     for percent = judgement
    %         %         allData = zeros(0,length(types) * 2);
    %         data = readmatrix(strcat('../faults_new/cc_',num2str(core),'_',percent,'_',num2str(1),'.txt'));
    %         %         allData = [allData; data]
    %
    %         count=1;
    %         cc_result = zeros(0,2);
    %
    %         for i = 1:2:length(types)*2
    %             [r, p] = corrcoef(data(:,i), data(:,i+1));
    %             cc_result(count,:) = [r(1,2),p(1,2)];
    %             count = count +1;
    %         end
    %
    %         disp(cc_result)
    %     end
end

% percent = "0.4";
% data = readmatrix(strcat('../faults_new/cc_',num2str(4),'_',percent,'_',num2str(1),'.txt'));
% dd = zeros(0,0);
% for i = [ 1 3 5 7 9 11]
%     dd= [dd data(:,i+1)];
% end
% [coeff,score,latent,~,explained] = pca(dd);
% explained