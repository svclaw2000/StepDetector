import pickle
import pandas as pd

feature_names = [
#		'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
		'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
	]

svm = pickle.load(open('svm_model.pkl', 'rb'))
data = pd.read_csv('RedefinedData.txt', sep=' ')
cor_data = data['class']
scaler = pickle.load(open('scaler.pkl', 'rb'))
data = scaler.transform(data[feature_names])
predicted = svm.predict(data)
matched = 0
for i in range(len(cor_data)):
	if predicted[i] == cor_data[i]:
		matched += 1
print('**%.3f%%** (%d/%d)' % (matched / len(cor_data) * 100, matched, len(cor_data)))
