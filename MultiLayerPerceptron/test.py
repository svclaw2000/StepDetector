import pandas as pd
from sklearn.preprocessing import MinMaxScaler

df = pd.read_csv('RedefinedData.txt', sep=' ')
scaler = MinMaxScaler()
scaler.fit(df)
data = scaler.transform(df)
print(data)
