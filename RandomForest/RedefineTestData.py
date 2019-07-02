WINDOW_SIZE = 20

WALK_PATH = '../../Test_data/data_190625_154306.tsv'
STOP_PATH = '../../Test_data/data_190625_154408.tsv'

with open('RedefinedStopData.txt', 'w') as output:
	with open(STOP_PATH, 'r') as f:
		f.readline()
		lineNum = 0
		datas = [[]] * WINDOW_SIZE
		while True:
			line = f.readline()
			if not line: break
			colums = line.split('\t')
			
			# Cut the first and the last 1 sec
			if colums[1] == 'Walk':
				if int(colums[0]) < 1000 or int(colums[0]) > 29000: continue
			elif colums[1] == 'Stop':
				if colums[2] == 'Stop':
					if int(colums[0]) < 1000 or int(colums[0]) > 29000: continue
				elif colums[2] == 'Typing':
					if int(colums[0]) < 1000: continue
				else:
					if int(colums[0]) < 1000 or int(colums[0]) > 4000: continue
			
			datas[lineNum % WINDOW_SIZE] = [float(colums[6]), float(colums[7]), float(colums[8])]
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

with open('RedefinedWalkData.txt', 'w') as output:
	with open(WALK_PATH, 'r') as f:
		f.readline()
		lineNum = 0
		datas = [[]] * WINDOW_SIZE
		while True:
			line = f.readline()
			if not line: break
			colums = line.split('\t')
			
			# Cut the first and the last 1 sec
			if colums[1] == 'Walk':
				if int(colums[0]) < 1000 or int(colums[0]) > 29000: continue
			elif colums[1] == 'Stop':
				if colums[2] == 'Stop':
					if int(colums[0]) < 1000 or int(colums[0]) > 29000: continue
				elif colums[2] == 'Typing':
					if int(colums[0]) < 1000: continue
				else:
					if int(colums[0]) < 1000 or int(colums[0]) > 4000: continue
			
			datas[lineNum % WINDOW_SIZE] = [float(colums[6]), float(colums[7]), float(colums[8])]
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