import pickle
import pandas as pd
from sklearn.metrics import accuracy_score

WINDOW_SIZE = 20

DATA_ROOT = '../../ML_Training/'
TEST_DATA_LIST = ['test01.tsv', 'test02.tsv', 'test03.tsv', 'test04.tsv',
				  'test05.tsv', 'test06.tsv', 'test07.tsv', 'test08.tsv', ]

total = 0
correct = 0

for dataName in TEST_DATA_LIST:
	TEST_DATA = DATA_ROOT + dataName
	with open('test.txt', 'w') as output:
		output.write('class '
#					 'acce0mean acce0var acce1mean acce1var acce2mean acce2var '
					 'gyro0mean gyro0var gyro1mean gyro1var gyro2mean gyro2var\n')
		with open(TEST_DATA, 'r') as f:
			f.readline()
			lineNum = 0
			datas = [[]] * WINDOW_SIZE
			while True:
				line = f.readline()
				if not line: break
				colums = line.split('\t')
				
				# Cut the first and the last 1 sec
				datas[lineNum % WINDOW_SIZE] = [
#								float(colums[3]), float(colums[4]), float(colums[5]),
								float(colums[6]), float(colums[7]), float(colums[8])
							]
				lineNum += 1
				if lineNum >= WINDOW_SIZE:
					sum = [0] * len(datas[0])
					sqrsum = [0] * len(datas[0])
					for data in datas:
						for i in range(len(data)):
							sum[i] += data[i]
							sqrsum[i] += data[i] * data[i]
					if colums[1] == 'Walk':
						output.write('1')
					elif colums[1] == 'Stop':
						output.write('0')
					for i in range(len(datas[0])):
						mean = sum[i] / WINDOW_SIZE
						var = sqrsum[i] - mean * mean
						output.write(' %f %f' % (mean, var))
					output.write('\n')
	
	feature_names = [
#		'acce0mean', 'acce0var', 'acce1mean', 'acce1var', 'acce2mean', 'acce2var',
		'gyro0mean', 'gyro0var', 'gyro1mean', 'gyro1var', 'gyro2mean', 'gyro2var'
	]
	
	rf = pickle.load(open('rf_model.pkl', 'rb'))
	data = pd.read_csv('test.txt', sep=' ')
	cor_data = data['class']
	scaler = pickle.load(open('scaler.pkl', 'rb'))
	data = scaler.transform(data[feature_names])
	predicted = rf.predict(data)
	matched = 0
	for i in range(len(cor_data)):
		if predicted[i] == cor_data[i]:
			matched += 1
	total += len(cor_data)
	correct += matched
	# print('**%.3f%%** (%d/%d)' % (matched / len(data) * 100, matched, len(data)))
	
print('\n**%.3f%%** (%d/%d)' % (correct / total * 100, correct, total))
