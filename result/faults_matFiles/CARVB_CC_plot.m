clc
close all

cores = [4]
colors=["b"; "r"];
types = ["$C_j$","$\hat{L_k}$","$D^{in}_j$","$D^{out}_j$","$D^{in}_j + D^{out}_j$","$||G(v_j)||$"];
types_names = ["nodeET","pathET","in_degree","out_degree","in_out_degree","pathNum"];

% judgement = ["0.1", "0.2", "0.3", "0.4", "0.5"];
judgements = ["0.1"];
threads = 0:3;

% effects = ["0.5","0.8","1.0","2.0","4.0","5.0","10.0"];
effects = ["1.0"];

for core = cores
    for judgement = judgements
        for effect = effects
            
            allData = zeros(0);
            for thread = threads
                data = readmatrix(strcat('../faults_new/random_',num2str(core),'_',judgement,'_',effect,'_',num2str(thread),'.txt'));
                allData = [allData; data(:,1:2:13)];
            end
            
            %             figure;
            %
            %             allY= zeros(0);
            %             for i = 1:6
            %                 y = allData(allData(:,i)==1,:);
            %                 y1 = y(:,7);
            %                 boxplot(y1, "Position", i, 'Whisker',1)
            %                 hold on
            %                 %             allY = [allY y1];
            %             end
            
            %             f=figure('Position', [100, 100, wid, len]);
            for i = 1:6
                %                 subplot(6,1,i)
                
                f=figure('Position', [100, 100, 400, 200]);
                
                y = allData(allData(:,i)==1,:);
                y1 = y(:,7);
                
                histogram(y1,100);
                xlim([-0.2 0.3])
              ylim([0 4000])
%                 ylim([0 600])
                
                ylabel('frequency', 'FontSize', 12);
                xlabel(strcat('impact on makespan'), 'FontSize', 12);
                %                 legend(types(i), 'FontSize', 12,'Interpreter','latex')
                
                
                %             allY = [allY y1];
                
                set(gcf, 'PaperSize', [25 25])
                
                saveas(gcf,strcat('../faults_figs/carvb_cc_distribution_',num2str(core),'_','0.1','_',types_names(i),'.eps'), 'epsc');
                saveas(gcf,strcat('../faults_figs/carvb_cc_distribution_',num2str(core),'_','0.1','_',types_names(i),'.png'));
                
            end
            
            
            
            
            [r,p] = corrcoef(allData);
            r
            
            p
            %            [coeff,score,latent,tsquared,explained,mu] = pca(allData(:,1:6));
            %
            %            anova1(allData);
            %            explained
        end
    end
end