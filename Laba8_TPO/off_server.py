from flask import Flask, request
import numpy as np
from multiprocessing import Pool
import time

app = Flask(__name__)

def multiply_rows(args):
    row, matrix2 = args
    return np.dot(row, matrix2)
    

@app.route('/multiply', methods=['POST'])
def matrix_multiply():
    start_time = time.time()

    matrix1 = np.array(request.json['MatrixA'])
    matrix2 = np.array(request.json['MatrixB'])

    matrix2_T = np.transpose(matrix2)

    pool = Pool()
    args = [(row, matrix2_T) for row in matrix1]
    results = pool.map(multiply_rows, args)
    pool.close()
    pool.join()

    result_rows = results
    result = np.vstack(result_rows)

    end_time = time.time()
    elapsed_time = end_time - start_time
    print("Time: {} ms".format(elapsed_time))

    return {'RESULT': result.tolist()}


if __name__ == '__main__':
    app.run()
