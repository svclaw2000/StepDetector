from sklearn.model_selection import cross_val_score, cross_validate
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import confusion_matrix, make_scorer
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

# acce: n_estimators=160, max_depth=14, acce+gyro: n_estimators=100, max_depth=14
rf = RandomForestClassifier(n_estimators=160, max_depth=14, random_state=123456)

print('accuracy')
scores = cross_val_score(rf, x_train, y_train, cv=10, scoring='accuracy', n_jobs=-1)
print(scores)
print(np.mean(scores))

print('precision')
scores = cross_val_score(rf, x_train, y_train, cv=10, scoring='precision', n_jobs=-1)
print(scores)
print(np.mean(scores))

print('recall')
scores = cross_val_score(rf, x_train, y_train, cv=10, scoring='recall', n_jobs=-1)
print(scores)
print(np.mean(scores))

print('confusion matrix')
def acc(y_true, y_pred): 
    cf = confusion_matrix(y_true, y_pred)
    return (cf[0,0] + cf[1,1])/np.sum(cf)
def pre_0(y_true, y_pred): 
    cf = confusion_matrix(y_true, y_pred)
    return cf[0,0]/(cf[0,0]+cf[1,0])
def rec_0(y_true, y_pred): 
    cf = confusion_matrix(y_true, y_pred)
    return cf[0,0]/(cf[0,0]+cf[0,1])
def f1_0(y_true, y_pred): 
    cf = confusion_matrix(y_true, y_pred)
    pre = cf[0,0]/(cf[0,0]+cf[1,0])
    rec = cf[0,0]/(cf[0,0]+cf[0,1])
    return 2*pre*rec/(pre+rec)
def pre_1(y_true, y_pred): 
    cf = confusion_matrix(y_true, y_pred)
    return cf[1,1]/(cf[0,1]+cf[1,1])
def rec_1(y_true, y_pred): 
    cf = confusion_matrix(y_true, y_pred)
    return cf[1,1]/(cf[1,0]+cf[1,1])
def f1_1(y_true, y_pred): 
    cf = confusion_matrix(y_true, y_pred)
    pre = cf[1,1]/(cf[0,1]+cf[1,1])
    rec = cf[1,1]/(cf[1,0]+cf[1,1])
    return 2*pre*rec/(pre+rec)
scoring = {'accuracy' : make_scorer(acc), 
           'precision_0' : make_scorer(pre_0),
           'recall_0' : make_scorer(rec_0), 
           'f1_score_0' : make_scorer(f1_0), 
           'precision_1' : make_scorer(pre_1),
           'recall_1' : make_scorer(rec_1), 
           'f1_score_1' : make_scorer(f1_1)}
scores = cross_validate(rf, x_train, y_train, cv=10, scoring=scoring, n_jobs=-1)

for scr in scores:
    print(scr, np.mean(scores[scr]))

'''
for scr in scores:
    print(scr, end='')
    for i in scores[scr]:
        print(',', i, end='')
    print()
'''