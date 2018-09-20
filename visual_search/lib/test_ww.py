
import numpy as np
#from pylab import * 
from compiler.ast import flatten    

def vectorChange():
  im_fea = np.array([0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324])
  length = len(im_fea)
  print length;

  im_feaVector = im_fea.reshape(1, length)
  #print im_fea;
  #print im_feaVector;
  
  return im_feaVector

  
  
def read_data(dir_str):
    data_temp=[]
    with open(dir_str) as fdata:
        while True:
            line=fdata.readline()
            if not line:
                break
            data_temp.append([float(i) for i in line.split()])
    return np.array(data_temp)
    

#print read_data("/home/wangwei/jniTest/callCup/array.txt")
'''
a = np.loadtxt('/home/wangwei/jniTest/callCup/array.txt')
print a.size
'''

im_feaVector = vectorChange();
print  'abc';
print  flatten(im_feaVector);

im_fea = np.array([0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324])  
binary_array = np.where(im_fea >= 0.1, 1, 0); 
print binary_array

'''
list = [(1,2,3),(4,5,6)]    
matrix = mat(list)     
listc = matrix.getA()
'''   