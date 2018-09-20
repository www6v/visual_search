import numpy as np
import sys

from elasticsearch import Elasticsearch
from math import ceil

###def binarize_fea(fea, thres=0):
def binarize_fea(fea, thres=0.1):
    "Binarize and pack feature vector"
    return _binarize_fea(fea, thres)

def _binarize_fea(x, thresh):
    '''binary and pack feature vector'''
    binary_vec = np.where(x >= thresh, 1, 0)
    f_len = binary_vec.shape[0]; ###print f_len      
    if f_len % 32 != 0:
        new_size = int(ceil(f_len / 32.) * 32); ### print new_size 
        num_pad = new_size - f_len; ### print num_pad 
        ###binary_vec = np.pad(binary_vec, (num_pad, 0), 'constant'); ###print binary_vec         
        ###binary_vec = np.pad(binary_vec, (1, 0), 'constant'); print "123"; print binary_vec ###  no 32 dimension

    ###return np.packbits(binary_vec).view('uint32')
    return np.packbits(binary_vec).view('uint32')   

def arrayToVector( im_fea ):
  ## array to vector
  length = len(im_fea)
  im_feaVector = im_fea.reshape(1, length)
  return im_feaVector;


# add     
###def transform_fea(fea, thres=0.1): 
def transform_fea(fea, thres=0.0005):    
    "Binarize and pack feature vector"
    return _transform_fea(fea, thres)
    
def _transform_fea(x, thresh):
    binary_array = np.where(x >= thresh, 1, 0)
    return binary_array;      

QUERY = """
{
"_source": ["im_src", "cl", "coords"],
"query": {
  "function_score" : {
    "query" : {
      "match_all" : {
        "boost" : 1.0
      }
    },
    "functions" : [
      {
        "filter" : {
          "match_all" : {
            "boost" : 1.0
          }
        },
        "script_score" : {
          "script" : {
            "inline" : "hamming_score",
            "lang" : "native",
            "params" : {
              "f" : "bin_sigs",
              "fea" : [##fea##],
              "verbose" : true
            }
          }
        }
      }
    ],
    "score_mode" : "sum",
    "boost_mode" : "replace",
    "max_boost" : 3.4028235E38,
    "boost" : 1.0
  }
}
}
"""



QUERY1 = """
{
"_source": ["im_src", "cl", "coords"],
"query": {
  "function_score" : {
    "query" : {
    
        "dis_max" : {
            "tie_breaker" : 0.7,
            "boost" : 1.2,
            
            "queries" : [
                {
                    "term" : { "cl" : "whole" }
                }
            ]
                        
        }
              
    },
    "functions" : [
      {
        "filter" : {
          "match_all" : {
            "boost" : 1.0
          }
        },
        "script_score" : {
          "script" : {
            "inline" : "hamming_score",
            "lang" : "native",
            "params" : {
              "f" : "bin_sigs",
              "fea" : [##fea##],
              "verbose" : true
            }
          }
        }
      }
    ],
    "score_mode" : "sum",
    "boost_mode" : "replace",
    "max_boost" : 3.4028235E38,
    "boost" : 1.0
  }
}
}
"""


#im_fea = np.array([0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324])
im_fea = np.array([0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324])

if(len(sys.argv)==2):  
  fileName = sys.argv[1]
else:
  fileName = '/home/wangwei/jniTest/jniReco/featuredb/11111-99.png.txt' 

#im_fea = np.loadtxt('/home/wangwei/jniTest/callCup/array.txt')
#im_fea = np.loadtxt('/home/wangwei/jniTest/jniReco/featuredb/11111-99.png.txt')
im_fea = np.loadtxt(fileName)


#fea = extractor.extract_imfea(img)
#fea = extractor.binarize_fea(fea)
###fea = binarize_fea( arrayToVector(im_fea) );  ###print fea;
fea = transform_fea( im_fea );
fea_str = ','.join([str(int(t)) for  t in fea]); print fea_str;
query = QUERY.replace('##fea##', fea_str)

###print query

###es = Elasticsearch(hosts='10.4.65.226:9200')
#result = es.search(index='image_index', doc_type='image_type', body=query)
###result = es.search(index='im_data', doc_type='obj', body=query,  request_timeout=60)


###print str(result)