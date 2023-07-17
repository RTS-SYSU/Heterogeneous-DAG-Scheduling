clc
close all

cores = 4;
colors=["b"; "r"];
types = ["$C_j$","$\hat{L}(v_j)$","$D^{in}_j$","$D^{out}_j$","$D_j$","$||G(v_j)||$"];
types_names = ["nodeET","pathET","in-degree","out-degree","in_out_degree","pathNum"];

% judgement = ["0.1", "0.2", "0.3", "0.4", "0.5"];

judgements = 0.9;
dummy_judgements = "0.1";
threads = 0:4;

% effects = ["0.5","0.8","1.0","2.0","4.0","5.0","10.0"];
effects = ["-1.0","1.0"];

displaySize = 100000;

for core = cores
    for dummy_judgement = dummy_judgements
        allData = zeros(0);
        for effect = effects
            for thread = threads
                data = readmatrix(strcat('../faults_new/random_',num2str(core),'_',dummy_judgement,'_',effect,'_',num2str(thread),'.txt'));
                allData = [allData; data()];
            end
        end
        
        [datarow, datacol] = size(allData);
        
        
        dataDis1 = allData(:,[1:2:datacol-2, datacol]);
        dataDis2 = allData(:,[2:2:datacol-2, datacol]);
        
        displayBegin = 1; % datarow/2-displaySize;
        displayEnd = datarow; % datarow/2+displaySize;
        
        for i = 1:6
            %%%%%%%%%%%%%%%% Scatter %%%%%%%%%%%%%%%%
            %             figure('Position', [100, 100, 400, 200]);
            %             scatter(dataDis1(displayBegin:displayEnd,i),dataDis1(displayBegin:displayEnd,7));
            %             ylabel('$\Delta{R_i}(\%)$', 'FontSize', 12 ,'Interpreter','latex');
            %             set(gcf, 'PaperSize', [25 25])
            %             saveas(gcf,strcat('../CARVB_figs/carvb_cc_scatter1_',num2str(core),'_','0.1','_',types_names(i),'.eps'), 'epsc');
            %             saveas(gcf,strcat('../CARVB_figs/carvb_cc_scatter1_',num2str(core),'_','0.1','_',types_names(i),'.png'));
            
            
            
            %             figure('Position', [100, 100, 400, 200]);
            %             scatter(dataDis2(displayBegin:displayEnd,i),dataDis2(displayBegin:displayEnd,7));
            %
            %             ylabel('$\Delta{R_i}(\%)$', 'FontSize', 12 ,'Interpreter','latex');
            %             xlabel('Normalised Ranking', 'FontSize', 12);
            %
            %             set(gcf, 'PaperSize', [25 25])
            %             saveas(gcf,strcat('../CARVB_figs/carvb_cc_scatter2_',num2str(core),'_','0.1','_',types_names(i),'.eps'), 'epsc');
            %             saveas(gcf,strcat('../CARVB_figs/carvb_cc_scatter2_',num2str(core),'_','0.1','_',types_names(i),'.png'));
            
            
            
            
            %             figure('Position', [100, 100, 400, 200]);
            %             histogram(dataDis1(:,i),100)
            %
            %             ylabel('frequency', 'FontSize', 12 ,'Interpreter','latex');
            %             xlabel('Normalised Ranking', 'FontSize', 12);
            %
            %             set(gcf, 'PaperSize', [25 25])
            %             saveas(gcf,strcat('../CARVB_figs/carvb_cc_scatter_',num2str(core),'_','0.1','_',types_names(i),'.eps'), 'epsc');
            %             saveas(gcf,strcat('../CARVB_figs/carvb_cc_scatter_',num2str(core),'_','0.1','_',types_names(i),'.png'));
            
            
            %%%%%%%%%%%%%%%% histogram %%%%%%%%%%%%%%%%
            %             f=figure('Position', [100, 100, 400, 200]);
            %
            %             y = dataDis2(dataDis2(:,i)>=0.9,:);
            %             y = [y; dataDis2(dataDis2(:,i)<=-0.9,:)];
            %             %
            %             %             y = [y; dataDis2(dataDis2(:,i)==-1,:)];
            %
            %             y1 = y(:,7);
            %
            %
            %             histogram(y1,100);
            %
            %             xlim([-0.3 0.3])
            %             %             ylim([0 28000])
            %
            %             xticksNum = -0.3:0.1:0.3;
            %             set(gca,'xtick',xticksNum );
            %             set(gca,'xticklabel',["-0.3", "-0.2", "-0.1", "0", "0.1", "0.2", "0.3"]);
            %
            %             ylabel('Frequency', 'FontSize', 12);
            %             xticklabels([-0.3:0.1:0.3])
            %             xlabel(strcat('$\Delta{R_i}(\%)$'), 'FontSize', 12,'Interpreter','latex');
            %
            %             set(gcf, 'PaperSize', [25 25])
            %             saveas(gcf,strcat('../CARVB_figs/carvb_cc_hist_',num2str(core),'_','0.1','_',types_names(i),'.eps'), 'epsc');
            %             saveas(gcf,strcat('../CARVB_figs/carvb_cc_hist_',num2str(core),'_','0.1','_',types_names(i),'.png'));
            
        end
        
        [r,p] = corrcoef(dataDis1);
        r(:,7)'
%         fcnCorrMatrixPlot(dataDis1(), [types_names, "makespan"],"caption")
%         FigFile = 'SamplePlots'; %orient tall
%         saveas(gcf, FigFile,'png')
        
        [r,p] = corrcoef(dataDis2);
        r(:,7)'
%         fcnCorrMatrixPlot(dataDis2(), [types_names, "makespan"],"caption")
        


        %         y = dataDis2(:, [1:6]);
        %         y(y>=0.9 | y<=-0.9) = 1;
        %         y(-0.9<y & y<0.9) = 0;
        
        %                    [coeff,score,latent,tsquared,explained,mu] = pca(allData(:,1:6));
        %                    anova1(allData);
        %                    explained
        
    end
end