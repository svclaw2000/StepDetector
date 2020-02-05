import pandas as pd
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import confusion_matrix, make_scorer
from sklearn.model_selection import cross_val_score, cross_validate
from sklearn.neural_network.multilayer_perceptron import MLPClassifier

df = pd.read_csv('RedefinedData.txt', sep=' ')

feature_names = [
   'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
#   'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
]
scaler = MinMaxScaler()
scaler.fit(df[feature_names])
x_train = scaler.transform(df[feature_names])
y_train = df['class']

# acce + gyro: hidden_layer_sizes=(190, 140, 150), acce: hidden_layer_sizes = (10, 80, 80)
mlp = MLPClassifier(solver='sgd', activation='relu', alpha=1e-5,
					hidden_layer_sizes = (10, 80, 80), random_state=123456,
					max_iter=200, verbose=10, learning_rate_init=.1)

print('accuracy')
scores = cross_val_score(mlp, x_train, y_train, cv=10, scoring='accuracy', n_jobs=-1)
print(scores)
print(np.mean(scores))

print('precision')
scores = cross_val_score(mlp, x_train, y_train, cv=10, scoring='precision', n_jobs=-1)
print(scores)
print(np.mean(scores))

print('recall')
scores = cross_val_score(mlp, x_train, y_train, cv=10, scoring='recall', n_jobs=-1)
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
scores = cross_validate(mlp, x_train, y_train, cv=10, scoring=scoring, n_jobs=-1)

for scr in scores:
    print(scr, end='')
    for i in scores[scr]:
        print(',', i, end='')
    print()
        