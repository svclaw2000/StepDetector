import pandas as pd
from sklearn.ensemble import RandomForestClassifier
import pickle
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import GridSearchCV
import RedefineTestData
import RF_TrainingTest

df = pd.read_csv('RedefinedData.txt', sep=' ')

feature_names = [
	'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
#	'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
]
scaler = MinMaxScaler()
scaler.fit(df[feature_names])
x_train = scaler.transform(df[feature_names])
y_train = df['class']

rf = RandomForestClassifier(random_state=123456)
n_estimators_range = [x for x in range(10, 200, 10)]
max_depth_range = [x for x in range(1, 15)]
param_grid = [{'n_estimators': n_estimators_range, 'max_depth':max_depth_range}]
grid = GridSearchCV(rf, param_grid, cv=10, n_jobs=-1)
grid.fit(x_train, y_train)

print(grid.best_params_)

pickle.dump(grid, open('rf_model.pkl', 'wb'))
pickle.dump(scaler, open('scaler.pkl', 'wb'))

print("RedefineTestData")
RedefineTestData.run()
print("RF_TrainingTest")
RF_TrainingTest.run()
