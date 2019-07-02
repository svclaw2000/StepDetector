import pandas as pd
import numpy as np
import os

from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
from sklearn.externals import joblib
import pickle

df = pd.read_csv('RedefinedData.txt', sep=' ')

feature_names = ['gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var']
x_train, x_test, y_train, y_test = train_test_split(df[feature_names], df['class'], test_size=0.25, stratify=df['class'], random_state=123456)

rf = RandomForestClassifier(n_estimators=100, oob_score=True, random_state=123456)
rf.fit(x_train, y_train)

predicted = rf.predict(x_test)
accuracy = accuracy_score(y_test, predicted)

print('Out-of-bag score estimate: %.3f' %rf.oob_score_)
print('Mean accuracy score: %.3f' %accuracy)

