
effect = ["0.6"];

for core = cores
    for percent = percents
        
        data = readmatrix(strcat('../faults_new/out_',num2str(core),'_',num2str(percent),'_',num2str(effect),'_',num2str(1),'.txt'));
        ways = length(types) * 2;
        
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
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
        
        f = figure('Position', [100, 100, 1200, 500]);
        
        minY = 10000000;
        maxY = 0;
        
        for i = 1:2:ways
            maxLocal = max(max(prctile(data(:,i),percentile,"all")), max(prctile(data(:,i+1),percentile,"all")));
            minLocal = min(min(prctile(data(:,i),percentile,"all")), min(prctile(data(:,i+1),percentile,"all")));
            
            maxY = max(maxY, maxLocal);
            minY = min(minY, minLocal);
        end
        
        subplotIndex = 1;
        for i = 1:2:ways
            if any(ignoredType==(i+1)/2)
                continue;
            end
            subplot(row,col,subplotIndex);
            subplotIndex = subplotIndex + 1;
            
            %         plot(xtick, prctile(data(:,i),percentile,"all") ,'-x', xtick, prctile(data(:,i+1),percentile,"all"),'-o',xtick, prctile(data(:,1),percentile,"all"),'-+',xtick, prctile(data(:,2),percentile,"all"),'-*', 'LineWidth', 2, 'MarkerSize', 10);
            plot(xtick, prctile(data(:,i),percentile,"all") ,'-x', xtick, prctile(data(:,i+1),percentile,"all"),'-o', 'LineWidth', 2, 'MarkerSize', 10);
            set(gca,'xtick', 1:length(xtick) );
            set(gca,'xticklabel',percentile,'fontsize',12);
            
            xlim([1 length(xtick)])
            ylim([minY-5000 maxY+10000])
            
            xlabel('level of assurance (%)','FontSize', 12)
            ylabel('makespan','FontSize', 12)
            title(types((i+1)/2),'FontSize', 14);
            
            if i==1
                h = legend("vary no node", "vary all nodes");
            else
                h = legend(strcat("low ", types((i+1)/2)), strcat("high ", types((i+1)/2)));
            end

            set(h,'FontSize',12, 'FontAngle','italic','location','northwest','Orientation','vertical');
            legend boxoff
        end
        
%         text(-10, -2, strcat("The worst-case makespan with different level of assurance, percentage of varied nodes = ", num2str(percent * 100),'%') ,'FontSize',14);
        saveas(gcf,strcat('../faults_figs/predict_',num2str(core),'_',num2str(percent),'_all.png'));
        
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
        %     figure('Position', [100, 100, 2000, 1200]);
        %     for i = 1:2:ways
        %
        %         if i==1
        %             subplot(row,col,[1,2,3])
        %         elseif i==3
        %             subplot(row,col,4)
        %         elseif i==5
        %             subplot(row,col,5)
        %         elseif i==7
        %             subplot(row,col,6)
        %         elseif i==9
        %             subplot(row,col,7)
        %         elseif i==11
        %             subplot(row,col,8)
        %         elseif i==13
        %             subplot(row,col,9)
        %         elseif i==15
        %             subplot(row,col,10)
        %         elseif i==17
        %             subplot(row,col,12)
        %         end
        %
        %         histogram(data(:,i));
        %         hold on
        %         histogram(data(:,i+1));
        %
        %         xlabel('makespan','FontSize', 14)
        %         ylabel('frequency','FontSize', 14)
        %
        %         if i==1
        %             h = legend("error free","error on all nodes");
        %         elseif i==3
        %             h = legend("errror on LOW nodeET","error on HIGH nodeET");
        %         elseif i==5
        %             h = legend("error on LOW pathET","error on HIGH pathET");
        %         elseif i==7
        %             h = legend("error on LOW in\_degree","error on HIGH in\_degree");
        %         elseif i==9
        %             h = legend("error on LOW out\_degree","error on HIGH out\_degree");
        %         elseif i==11
        %             h = legend("error on LOW in\_out\_degree","error on HIGH in\_out\_degree");
        %         elseif i==13
        %             h = legend("error on LOW pathNum","error on HIGH pathNum");
        %         elseif i==15
        %             h = legend("error on LOW statSensitive","error on HIGH statSensitive");
        %         elseif i==17
        %             h = legend("error on LOW statSensitive","error on HIGH statSensitive");
        %         end
        %         set(h,'FontSize',12, 'FontAngle','italic','location','northeast','Orientation','vertical' );
        %
        %     end
        %
        %     saveas(gcf,strcat('../faults_figs/hist_out_',num2str(core),'_all.png'));
        
    end
end
