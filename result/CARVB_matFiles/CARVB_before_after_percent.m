clc
close all

core = 4;
colors=["b"; "r"];
types = ["$C_j$","$\hat{L}(v_j)$","$D^{in}_j$","$D^{out}_j$","$D_j$","$||G(v_j)||$"];
types_names = ["nodeET","pathET","in-degree","out-degree","in_out_degree","pathNum"];
judgements = 0.9;
dummy_judgement = "0.1";

threads = 0:4;
effects = ["-1.0","1.0"];
percents = [ "-0.1", "0.1", "0.3", "0.4", "0.5", "1.0" ];

for percent = percents
    disp(strcat("percent:"," ",percent))
    allData = zeros(0);
    for effect = effects
        for thread = threads
            data = readmatrix(strcat('../faults_new/before_after_percent_ajlr_ajlr_',num2str(core),'_',dummy_judgement,'_',effect,'_',percent,'_',num2str(thread),'.txt'));
            allData = [allData; data()];
        end
    end
    [datarow, datacol] = size(allData);
    dataDis1 = allData(:,[1:2:datacol-2, datacol]);
    [r,p] = corrcoef(dataDis1);
    
    disp("AJLR and AJLR")
    disp(r(:,7)')
    
    allData = zeros(0);
    for effect = effects
        for thread = threads
            data = readmatrix(strcat('../faults_new/before_after_percent_carvb_carvb_',num2str(core),'_',dummy_judgement,'_',effect,'_',percent,'_',num2str(thread),'.txt'));
            allData = [allData; data()];
        end
    end
    [datarow, datacol] = size(allData);
    dataDis1 = allData(:,[1:2:datacol-2, datacol]);
    [r,p] = corrcoef(dataDis1);
    
    disp("CARVB and CARVB")
    disp(r(:,7)')
    
    allData = zeros(0);
    for effect = effects
        for thread = threads
            data = readmatrix(strcat('../faults_new/before_after_percent_carvb_ajlr_',num2str(core),'_',dummy_judgement,'_',effect,'_',percent,'_',num2str(thread),'.txt'));
            allData = [allData; data()];
        end
    end
    [datarow, datacol] = size(allData);
    dataDis1 = allData(:,[1:2:datacol-2, datacol]);
    [r,p] = corrcoef(dataDis1);
    
    disp("CARVB and AJLR")
    disp(r(:,7)')
    
    allData = zeros(0);
    for effect = effects
        for thread = threads
            data = readmatrix(strcat('../faults_new/before_after_percent_ajlr_carvb_',num2str(core),'_',dummy_judgement,'_',effect,'_',percent,'_',num2str(thread),'.txt'));
            allData = [allData; data()];
        end
    end
    [datarow, datacol] = size(allData);
    dataDis1 = allData(:,[1:2:datacol-2, datacol]);
    [r,p] = corrcoef(dataDis1);
    
    disp("AJLR and CARVB")
    disp(r(:,7)')
    
end