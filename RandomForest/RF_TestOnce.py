import pickle
import pandas as pd

rf = pickle.load(open('rf_model.pkl', 'rb'))
raw_data = '0.8974951 29.163437 7.287815 1021.06036 6.5012903 825.0307 0.045284998 1.0569286 -0.035634995 1.2157503 0.14816497 2.3020961'
data = pd.read_csv('test.txt', sep=' ', header=None)
print(rf.predict(data))