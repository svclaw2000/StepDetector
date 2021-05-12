import pickle

data = '-0.010345 0.01248667 -0.0024900003 0.00831486 4.3000001E-4 0.0026665754'
data = data.split()
rf = pickle.load(open('rf_model.pkl', 'rb'))
result = rf.predict([data])[0]
print(result)
