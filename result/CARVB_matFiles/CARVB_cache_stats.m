close all

startIns = 1;
endIns = 100;

uStart = 1;
uEnd = 9;
uGap = 0.4;
uSpec1 = '_2.0';
uSpec2 = '_4.0';


systemNo = 500;
metric_makespan = 'cache_';

start_method = 2;


alldata1 = [];
alldata2 = [];
%% boxplot_makespan_medain
file_name = '../predict/';

for util = uStart:uEnd
    read = util + 0;
    if(read == 5)
        data = readmatrix(strcat(file_name,metric_makespan ,'1', uSpec1, '.txt'));
    elseif(read == 10)
        data = readmatrix(strcat(file_name,metric_makespan ,'1', uSpec2, '.txt'));
    else
        data = readmatrix(strcat(file_name,metric_makespan ,'1', '_',num2str(read * uGap), '.txt'));
    end
    
    rowsNum = size(data,1);
    
    methodNum = rowsNum/systemNo;
    
    dataByMethod = cell(1,methodNum);
    
    % get data by each method
    for m = 1: methodNum
        startIndex = 1 + (m-1) * systemNo;
        endIndex = m * systemNo;
        dataByMethod{m} = data(startIndex :endIndex,:);
    end
    
    data_per_util = [];
    for m=start_method:methodNum
        datam = dataByMethod{m};
        d = mean(datam(:,1:3));
        data_per_util =[data_per_util; d];
    end
    

    alldata1 = [alldata1; data_per_util(1,:)];
    alldata2 = [alldata2; data_per_util(2,:)];
end

alldata1
alldata2
