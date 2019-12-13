from sklearn.neural_network import MLPClassifier
import pandas as pd
import numpy as np
import pickle
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import GridSearchCV
from datetime import datetime

df = pd.read_csv('RedefinedData.txt', sep=' ')

feature_names = [
	'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
	'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
]
scaler = MinMaxScaler()
scaler.fit(df[feature_names])
x_train = scaler.transform(df[feature_names])
y_train = df['class']

mlp = MLPClassifier(solver='sgd', activation='relu', alpha=1e-2, random_state=123456,
					max_iter=200, verbose=0, learning_rate_init=.1)
hidden_layer_sizes_range = [(x,y) for x in range(10,200,10) for y in range(10,200,10)]
param_grid = [{'hidden_layer_sizes':hidden_layer_sizes_range}]
grid = GridSearchCV(mlp, param_grid=param_grid, cv=3, n_jobs=-1)
print('Start fitting')
print(datetime.now())
grid.fit(x_train, y_train)
print(datetime.now())

pickle.dump(grid, open('mlp_model.pkl', 'wb'))
pickle.dump(scaler, open('scaler.pkl', 'wb'))

print(grid.best_params_)

print('MLP_Test')
import MLP_Test
print('MLP_TrainingTest')
import MLP_TrainingTest
