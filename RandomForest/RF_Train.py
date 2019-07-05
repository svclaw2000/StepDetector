import pandas as pd

from sklearn.ensemble import RandomForestClassifier
import pickle

df = pd.read_csv('RedefinedData.txt', sep=' ')

feature_names = [
#	'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
	'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
]
x_train, y_train = df[feature_names], df['class']

rf = RandomForestClassifier(n_estimators=100, oob_score=True, random_state=123456)
rf.fit(x_train, y_train)

pickle.dump(rf, open('rf_model.pkl', 'wb'))
