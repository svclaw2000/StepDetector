from sklearn.neural_network import MLPClassifier
import pandas as pd
import pickle
from sklearn.preprocessing import MinMaxScaler

df = pd.read_csv('RedefinedData.txt', sep=' ')

feature_names = [
#	'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
	'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
]
scaler = MinMaxScaler()
scaler.fit(df[feature_names])
x_train = scaler.transform(df[feature_names])
y_train = df['class']

mlp = MLPClassifier(solver='sgd', activation='relu', alpha=1e-4,
					hidden_layer_sizes=(60, 80, 100), random_state=1,
					max_iter=200, verbose=10, learning_rate_init=.1)
mlp.fit(x_train, y_train)

pickle.dump(mlp, open('mlp_model.pkl', 'wb'))
pickle.dump(scaler, open('scaler.pkl', 'wb'))

print('MLP_Test')
import MLP_Test
print('MLP_TrainingTest')
import MLP_TrainingTest
