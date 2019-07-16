import pickle
import pandas as pd

feature_names = [
	#	'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
		'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
	]

rf = pickle.load(open('mlp_model.pkl', 'rb'))
data = pd.read_csv('RedefinedData.txt', sep=' ')
predicted = rf.predict(data[feature_names])
matched = 0
for i in range(len(data)):
	if predicted[i] == data['class'][i]:
		matched += 1
print('**%.3f%%** (%d/%d)' % (matched / len(data) * 100, matched, len(data)))