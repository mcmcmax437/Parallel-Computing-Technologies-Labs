import requests
import numpy as np

n = 200

matrix1 = np.random.rand(n, n)
matrix2 = np.random.rand(n, n)


data = {
    'MatrixA': matrix1.tolist(),
    'MatrixB': matrix2.tolist()
}

response = requests.post('http://localhost:5000/multiply', json=data)
print(response.json())
