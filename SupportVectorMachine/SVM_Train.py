from sklearn.svm import SVC
import pandas as pd
import pickle
import numpy as np
from sklearn.model_selection import GridSearchCV
from sklearn.preprocessing import MinMaxScaler

df = pd.read_csv('RedefinedData.txt', sep=' ')

feature_names = [
#    'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
    'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
]
scaler = MinMaxScaler()
scaler.fit(df[feature_names])
x_train = scaler.transform(df[feature_names])
y_train = df['class']

svm = SVC(kernel='rbf', class_weight='balanced')
c_range = np.logspace(-5, 15, 11, base=2)
gamma_range = np.logspace(-9, 3, 13, base=2)
param_grid = [{'kernel':['rbf'], 'C': c_range, 'gamma': gamma_range}]
grid = GridSearchCV(svm, param_grid, cv=3, n_jobs=-1)
print('Start fitting')
grid.fit(x_train, y_train)

pickle.dump(grid, open('svm_model.pkl', 'wb'))
pickle.dump(scaler, open('scaler.pkl', 'wb'))

print(grid.best_params_)

print('SVM_Test')
import SVM_Test
print('SVM_TrainingTest')
import SVM_TrainingTest
