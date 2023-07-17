
maxUtil = 8;


    f=figure('Position', [100, 100, wid, len]);
    set(f,'defaultAxesColorOrder',[[0,60/255,255/255];[1,51/255,51/255]]);
    
    
    for util = 1:5
        read = util + 0;
       if(read == 5)
           data = readmatrix(strcat('recency_fault_util/',metric ,'1', '_4.0', '.txt'));
       elseif(read == 10)
           data = readmatrix(strcat('recency_fault_util/',metric ,'1', '_8.0', '.txt'));
       else
           data = readmatrix(strcat('recency_fault_util/',metric ,'1', '_',num2str(read*0.8), '.txt'));
       end
       
       
       colsNum = 10;
       rowsNum = size(data,1);
       
       methodNum = rowsNum/systemNo;
       dataByMethod = cell(1,methodNum);
       
        % get data by each method
        for m = 1: methodNum
            startIndex = 1 + (m-1) * systemNo;
            endIndex = m * systemNo;
            dataByMethod{m} = data(startIndex :endIndex,:);
        end

        data_per_util = zeros(systemNo,methodNum);
        for m=1:methodNum
            datam = dataByMethod{m};

            datam_media = zeros(systemNo:methodNum);
            for row = 1:size(datam,1)
                datam_media(row) = median(datam(row,1:colsNum));
            end

            datam_media_col = datam_media';
            data_per_util(:,m) = datam_media_col;

        end
        
         pos = zeros(1);
         for col = 1:methodNum
             pos(col) = (util - 1) * 3 + col;
         end

         boxplot(data_per_util(:,1), 'position',pos(1), 'widths', 0.65, 'symbol','.', 'color', colors(1,:));
         hold on
         boxplot(data_per_util(:,2), 'position',pos(2), 'widths', 0.65, 'symbol','.', 'color', colors(2,:));
         hold on
         boxplot(data_per_util(:,3), 'position',pos(3), 'widths', 0.65, 'symbol','.', 'color', colors(3,:));
         hold on

    end
    
%     set(findobj(gca,'type','line'),'linew',1.5)
%     set(gca,'linew',1.5)
     
    xlim([0 16]);
    ylim([0 1]);
    xticks = [2 5 8 11 14];
    xticklables = ["10","20","30","40","50" ];
    
    set(gca,'xtick',xticks );
    set(gca,'xticklabel',xticklables);
    
    
    ax = gca(); 
    ax.TickLabelInterpreter = 'tex';
    ax.FontSize = 12;
    
     xlabel('System utilisation (%)','FontSize', 14)
     ylabel('Normalised makespan','FontSize', 14)
      
   c = findobj(gca,'Tag','Box');
    

    

    h = legend([c(3),c(2),c(1)],"AJLR no error","AJLR with error","AJLRE with error",'FontAngle','italic','location','southeast','Orientation','horizontal');
%     h = legend([c(3),c(2),c(1)],"WF+EO","AJLR without deviations","AJLR with deviations",'FontAngle','italic','location','southeast','Orientation','horizontal');
    set(h,'FontSize',14);

    
    set(gcf, 'PaperSize', [25 25])
    saveas(gcf,strcat('figs/ep_recency_fault_util.eps'), 'epsc');
