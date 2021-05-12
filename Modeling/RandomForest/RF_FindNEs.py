import pandas as pd
from sklearn.ensemble import RandomForestClassifier
import pickle
from sklearn.preprocessing import MinMaxScaler
import RedefineTestData
import RF_TrainingTest

df = pd.read_csv('RedefinedData.txt', sep=' ')

feature_names = [
	'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
	'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
]
scaler = MinMaxScaler()
scaler.fit(df[feature_names])
x_train = scaler.transform(df[feature_names])
y_train = df['class']

for i in range(70, 130):
	rf = RandomForestClassifier(n_estimators=i, oob_score=True, random_state=123456)
	rf.fit(x_train, y_train)
	print(i)

	pickle.dump(rf, open('rf_model.pkl', 'wb'))
	pickle.dump(scaler, open('scaler.pkl', 'wb'))

#	print("RedefineTestData")
	RedefineTestData.run()
#	print("RF_TrainingTest")
	RF_TrainingTest.run()
