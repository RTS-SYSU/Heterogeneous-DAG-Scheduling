import torch
from torch import nn
from torch import optim
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import sys
import ast
import pickle
from sklearn.preprocessing import StandardScaler


class Net(torch.nn.Module):
    def __init__(self, n_feature, n_output, n_neuron1, n_neuron2,n_layer):  # n_feature为特征数目，这个数字不能随便取,n_output为特征对应的输出数目，也不能随便取
        self.n_feature=n_feature
        self.n_output=n_output
        self.n_neuron1=n_neuron1
        self.n_neuron2=n_neuron2
        self.n_layer=n_layer
        super(Net, self).__init__()
        self.input_layer = torch.nn.Linear(self.n_feature, self.n_neuron1) # 输入层
        self.hidden1 = torch.nn.Linear(self.n_neuron1, self.n_neuron2) # 1类隐藏层    
        self.hidden2 = torch.nn.Linear(self.n_neuron2, self.n_neuron2) # 2类隐藏
        self.predict = torch.nn.Linear(self.n_neuron2, self.n_output) # 输出层
 
    def forward(self, x):
        '''定义前向传递过程'''
        out = self.input_layer(x)
        out = torch.relu(out) # 使用relu函数非线性激活
        out = self.hidden1(out)
        out = torch.relu(out)
        for i in range(self.n_layer):
            out = self.hidden2(out)
            out = torch.relu(out) 
        out = self.predict( # 回归问题最后一层不需要激活函数
            out
        )  # 除去feature_number与out_prediction不能随便取，隐藏层数与其他神经元数目均可以适当调整以得到最佳预测效果
        return out

def predict(test_data, model_path):
    '''
    input：
           test_data:测试数据
           model_path:模型的保存路径 model_path = './save/20201104_204451.ckpt'
    output:
           score:模型输出属于某一类别的概率
    '''
    model = torch.load(model_path)#加载模型
    model.eval()
    score = model(test_data)#模型预测
    return score #返回得分

if __name__ == '__main__':
    # 获取传递的参数
    # arg = sys.argv[1]
    # 将参数转换为Python的list类型
    # lst = ast.literal_eval(arg)
    # lst = [[0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1], [0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1]]
    arr = []
    with open('data_process/midway/array.txt', 'r') as f:
        for line in f:
            arr.append([float(x) for x in line.split()])
    
    # load the model
    path = "data_process/model/deep"
    file_name = "/sota_for_SGD.pkl"
    model_path = path + file_name

    scaler1 = pickle.load(open('data_process/scaler_x.pkl', 'rb'))
    scaler2 = pickle.load(open('data_process/scaler_y.pkl', 'rb'))
    arr = scaler1.transform(arr)
    test_data = torch.tensor(arr, dtype=torch.float32)

    score = predict(test_data, model_path).tolist()
    score = scaler2.inverse_transform(np.array(score).reshape(-1, 1))
    #output_data = process_data_for_output(score)

    print(str(score.tolist()))
