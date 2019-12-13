from sklearn.model_selection import cross_val_score
from sklearn.svm import SVC
from sklearn.preprocessing import MinMaxScaler
import pandas as pd
import numpy as np

df = pd.read_csv('RedefinedData.txt', sep=' ')

feature_names = [
   'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
    'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
]
scaler = MinMaxScaler()
scaler.fit(df[feature_names])
x_train = scaler.transform(df[feature_names])
y_train = df['class']

svm = SVC(kernel='rbf', class_weight='balanced', C=0.5, gamma=4.0)

print('accuracy')
scores = cross_val_score(svm, x_train, y_train, cv=10, scoring='accuracy')
print(scores)
print(np.mean(scores))

print('precision')
scores = cross_val_score(svm, x_train, y_train, cv=10, scoring='precision')
print(scores)
print(np.mean(scores))

print('recall')
scores = cross_val_score(svm, x_train, y_train, cv=10, scoring='recall')
print(scores)
print(np.mean(scores))
