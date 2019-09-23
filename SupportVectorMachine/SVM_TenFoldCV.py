from sklearn.model_selection import cross_val_score
from sklearn.svm import SVC
import pandas as pd

df = pd.read_csv('RedefinedData.txt', sep=' ')

feature_names = [
#   'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
    'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
]
x_train, y_train = df[feature_names], df['class']

svm = SVC(kernel='rbf', class_weight='balanced', C=32768, gamma=0.0625)

scores = cross_val_score(svm, x_train, y_train, cv=10)

for i in scores:
	print(i)
