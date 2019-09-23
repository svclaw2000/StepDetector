import pandas as pd
from sklearn.neural_network.multilayer_perceptron import MLPClassifier
from sklearn.model_selection import cross_val_score

df = pd.read_csv('RedefinedData.txt', sep=' ')
feature_names = [
#	'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
	'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
]
x_train, y_train = df[feature_names], df['class']

mlp = MLPClassifier(solver='sgd', activation='relu', alpha=1e-4,
					hidden_layer_sizes=(60, 80, 100), random_state=1,
					max_iter=200, verbose=10, learning_rate_init=.1)

scores = cross_val_score(mlp, x_train, y_train, cv=10)

for i in scores:
	print('%.3f' %i)

print(scores)
