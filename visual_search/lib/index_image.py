import sys
import cPickle as pickle
import numpy as np

from math import ceil
from base64 import b64encode

from es.ImFea_pb2 import ImFea, ImFeaArr, \
    ImFeaBinArr, ImFeaBin
    
from elasticsearch import Elasticsearch
from elasticsearch import helpers    

def binarize_fea(fea, thres=0.1):
    "Binarize and pack feature vector"
    return _binarize_fea(fea, thres)

def _binarize_fea(x, thresh):
    '''binary and pack feature vector'''
    binary_vec = np.where(x >= thresh, 1, 0)
    f_len = binary_vec.shape[0]; print f_len; print binary_vec.shape ###     
    if f_len % 32 != 0:
        new_size = int(ceil(f_len / 32.) * 32); print new_size ###
        num_pad = new_size - f_len; print num_pad ###
        ###binary_vec = np.pad(binary_vec, (num_pad, 0), 'constant'); print binary_vec.shape;  print binary_vec; 
        ###binary_vec = np.pad(binary_vec, (1, 0), 'constant'); print binary_vec ###  no 32 dimension
                
    ###print np.packbits(binary_vec).view('uint32').shape;
    ###print np.packbits(binary_vec).view('uint32');
    
    return np.packbits(binary_vec).view('uint32')
    
# add     
###def transform_fea(fea, thres=0.1):
def transform_fea(fea, thres=0.0005):      
    "Binarize and pack feature vector"
    return _transform_fea(fea, thres)
    
def _transform_fea(x, thresh):
    binary_array = np.where(x >= thresh, 1, 0)
    return binary_array;
    

def create_doc(im_src, tag, coords, fea_arr, fea_bin_arr, targetId, appId):
    """
    Create elasticsearch doc

    Params:
        im_src: image file name
        tag: tag or class for image
        coords: list of boxes corresponding to a tag
        fea_arr: list of ImFea objects
        fea_bin_arr: list of ImFeaBin objects
    """
    doc = {}
    doc['coords'] = coords
    
    
    f_bin = ImFeaBinArr()
    f = ImFeaArr()
    f.arr.extend(fea_arr)
    f_bin.arr.extend(fea_bin_arr)   # dimensionality reduction;   
    obj_bin_str = b64encode(f_bin.SerializeToString()); ###print "f_bin"; print  f_bin;
    obj_str = b64encode(f.SerializeToString()); ###print "f"; print  f;
    
    
    ###doc['sigs'] = obj_str 
    doc['bin_sigs'] = obj_bin_str
    ###doc['im_src'] = im_name
    doc['im_src'] = im_src    
    doc['cl'] = tag
    
    doc['target_id'] = targetId
    doc['app_id'] = appId
    
    return doc

def arrayToVector( im_fea ):
  ## array to vector
  length = len(im_fea)
  im_feaVector = im_fea.reshape(1, length)
  return im_feaVector;


##########        
es = Elasticsearch(hosts='{}:{}'.format("10.4.65.226", "9200"))

es_index = "im_data"
es_type = "obj"

actions = [] 
   
#################
def regionsFeatureToES(): 
    vector = [ [0.234, 0.345,1.3453,5.23324, 6.334], [0.234, 0.345,1.3453,5.23324, 6.334] ];
    v1 = np.array([[1,2]])
    v2 = np.array([0.001, 0.002,1.3453,5.23324, 6.334]) 
    v3 = np.array([0.001, 0.002,1.3453,5.23324, 0.003])
    v5 = np.array([0.001, 0.002,0.002,0.003, 5.23324]) 


    #f_bin = extractor.binarize_fea(f) 
    #print( binarize_fea(v5) )
    

        
    ######## for -> out
    coords = []
    ar = []
    ar_bin = []
                
    ########## for -> in1 
    
    coord_box = {}
    coord_box['c'] = 'lt' + 'rb'
    coord_box['score'] = float(0.3)
    coords.append(coord_box)
                    
                    
    f_bin = binarize_fea( v5 );
    
    
    im_fea = ImFea()
    im_fea_bin = ImFeaBin()
    #im_fea.f.extend(f)
    im_fea.f.extend( v5 ) ###
    im_fea_bin.f.extend(f_bin)
    
    ar.append(im_fea)
    ar_bin.append(im_fea_bin)
    
    ########## for -> in2
    coord_box = {}
    coord_box['c'] = 'lt' + 'rb'
    coord_box['score'] = float(0.3)
    coords.append(coord_box)
    
    f_bin = binarize_fea( arrayToVector(v3) );
    
    im_fea = ImFea()
    im_fea_bin = ImFeaBin()
    #im_fea.f.extend(f)
    im_fea.f.extend( v3 )  ###
    im_fea_bin.f.extend(f_bin)
    
    ar.append(im_fea)
    ar_bin.append(im_fea_bin)
    
    ##########
    doc = create_doc("imageName1", "tag1", coords, ar, ar_bin)
    
    # create index action
    action = {
                    "_index": es_index,
                    "_type": es_type,
                    "_source": doc
    }
    
    actions.append(action)
    
    ###if len(actions) == 1000:
                    #logger.info('Bulking {} docs to sever, indexed: {}'
                    #            .format(len(actions), num_docs))
    helpers.bulk(es, actions)
    del actions[:]



####################

def photoFeatureToES(): 

    im_fea = np.array([0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324])  
    im_fea1 = np.array([0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,0.002,0.003, 5.23324])
    im_fea2 = np.array([0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,0.002,0.003, 5.23324])
    im_fea3 = np.array([0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003])
    im_fea4 = np.array([0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324, 0.001, 0.002,1.3453,5.23324, 0.003, 0.001, 0.002,0.002,0.003, 5.23324])
    
    fileFullName = sys.argv[1]
    imageSimpleName = sys.argv[2]
    targetId = sys.argv[3]
    appId = sys.argv[4]    
    
    print fileFullName
    print imageSimpleName    
    print targetId  
    print appId    
        
    #im_fea = np.loadtxt('/home/wangwei/jniTest/callCup/array.txt')    
    im_fea = np.loadtxt(fileFullName)
          
    # index document ifself
    # im_fea = extractor.extract_imfea(im)  /// 1-demesion  array
    ###im_fea_bin = binarize_fea( arrayToVector(im_fea) )  # dimensionality reduction;    
    im_fea_bin = transform_fea( im_fea )
    
    doc = {}
    
    
    
    #(w, h, _) = im.shape
    #coords = [{'c': [0, 0, h, w], 'score': 1.0}]
    coords = [{'c': [0, 0, 367, 689], 'score': 1.0}]
    fea_bin = ImFeaBin()
    fea = ImFea()
    fea_bin.f.extend(im_fea_bin) 
    fea.f.extend(im_fea)
    
    
    doc = create_doc(imageSimpleName, 'whole', coords, [fea], [fea_bin], targetId, appId)
    #num_docs += 1
    
    # create index action
    action = {
                "_index": es_index,
                "_type": es_type,
                "_source": doc
    }
    
    actions.append(action)
            
    #if len(actions) == 1000:
    #            logger.info('Bulking {} docs to sever, indexed {}'
    #                        .format(len(actions), num_docs))
    helpers.bulk(es, actions)
    del actions[:]


###regionsFeatureToES();
photoFeatureToES();




