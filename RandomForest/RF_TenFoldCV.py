from sklearn.model_selection import cross_val_score
from sklearn.ensemble import RandomForestClassifier
import pandas as pd

df = pd.read_csv('RedefinedData.txt', sep=' ')

feature_names = [
#   'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
    'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
]
x_train, y_train = df[feature_names], df['class']

rf = RandomForestClassifier(n_estimators=100, oob_score=True, random_state=123456)

scores = cross_val_score(rf, x_train, y_train, cv=10)

for i in scores:
	print(i)
