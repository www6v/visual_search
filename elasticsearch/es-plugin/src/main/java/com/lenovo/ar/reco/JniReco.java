package com.lenovo.ar.reco;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.Instant;
import java.time.Duration;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JniReco {
  protected final Log logger = LogFactory.getLog(getClass());

	long nativeCFeatExtractor;

	private static final String ROOT_DIR = "/home/wangwei/jniTest/jniReco";
    private static final String SEPARATOR = "/";

    private String fileDir;
    private String targetId;

    public JniReco(){
//        System.out.println("JniReco createNativeObject.");
//        String strModelTag = "DenseNet_121";
//        nativeCFeatExtractor = createNativeObject(strModelTag);
    }

	public static void main(String[] args) throws IOException{
		///System.load("/home/wangwei/jniTest/jniReco/libReco.so");
    System.loadLibrary("Reco");  

    Instant initStart = Instant.now();
    JniReco jniReco = new JniReco();
    Instant initEnd = Instant.now();
    System.out.println("Duration.between(initStart, initEnd): " + Duration.between(initStart, initEnd).toMillis());

    if( args.length != 2 ) {
        return ;
    }

    jniReco.fileDir = args[0];
    jniReco.targetId = args[1];
    
    try{
        List<File> fileList = getFiles(jniReco.fileDir);

        for(int i =0; i <= fileList.size() -1 ; i++) {
            File file = fileList.get(i);
            //String imageSimpleName = file.getName();
            //String imageFullName = fileDir + "/" + imageSimpleName; System.out.println("imageSimpleName" + imageSimpleName);

            jniReco.extractAndSave(file);
        }
    }catch (Exception e){
       System.out.println(e);
    }

    jniReco.destroyExtractor();
    ///jniReco.forTest();
    }

private void extractAndSave(File file) {
    String imageSimpleName = file.getName();
    String imageFullName = fileDir + SEPARATOR + imageSimpleName;
            
    double[] featureVector = extract(imageFullName);
    
    doubleArrayToFile(featureVector, imageSimpleName);

    System.out.println( "featureVector.length : " + featureVector.length );
}

private static  List<File>  getFiles( String filePath ) throws Exception {
    List<File> fileList = new ArrayList<File>();
    File file = new File(  filePath );
    if(file.isDirectory()){
        File []files = file.listFiles();
        for(File fileIndex:files){        
	          if(fileIndex.isDirectory()){
	              getFiles(fileIndex.getPath());
	          }else {
                fileList.add(fileIndex);
	          }
        }
    }
    return fileList;
}

  public  void doubleArrayToFile(double[] arr, String imageSimpleName)  {
      String fileFullName = ROOT_DIR  + SEPARATOR + "featuredb" + SEPARATOR + this.targetId + "-" + imageSimpleName +".txt";

      System.out.println( "fileFullName:" + fileFullName);
      
      saveVectorToFile(arr, fileFullName);

      ProcessorCall.indexCall(fileFullName);
  }

    public void saveVectorToFile(double[] arr, String fileFullName) {
        try {
          File file = new File(fileFullName);
          FileWriter out = new FileWriter(file);

          for(int i=0;i<arr.length;i++){
            out.write(arr[i]+"\n");
          }
          out.close();
        } catch(Exception ex ) {
        } finally {
        }
    }

    public long createNativeObj(String strModelTag) {
        return createNativeObject(strModelTag);
    }

        public double[] extract(String fileName){
                System.out.println("JniReco extract.");
                double[] result = nativeExtract(nativeCFeatExtractor, fileName);
                System.out.println("double[] size" + result.length );
                return result;                
        }

        public double[] extractFromByte( byte[]  photo, int width, int height ){                
               double[] result = nativeExtractFromByte(nativeCFeatExtractor, photo, width, height);
               return result; 
        }

        public void destroyExtractor(){
                System.out.println("JniReco destroyExtractor.");
                nativeDestroyExtractor(nativeCFeatExtractor);                                                
        }
        
        public float  featureSimilarity(double[] feaA, double[] feaB) {
               logger.info("JniReco featureSimilarity.");
               return nativeFeatureSimilarity(feaA, feaB);                             
        }
        
        public long loadImage(String fileName) {
           System.out.println("JniReco LoadImage." + fileName);
           return nativeLoadImage(fileName);
        }
        
        public float imgSimilarity(long img1, long img2) {
          System.out.println("JniReco imgSimilarity.");          
          return nativeImgSimilarity(img1, img2);
        }

        public  void forTest() {
          nativeForTest(); 
        }

        private native long createNativeObject(String strModelTag);
        private native double[] nativeExtract(long nativeCFeatExtractor,String fileName);
        private native double[] nativeExtractFromByte(long nativeCFeatExtractor, byte[]  photo, int width, int height);
        private native void  nativeDestroyExtractor(long nativeCFeatExtractor);                
        private native float nativeFeatureSimilarity(double[] tFeatureVectorA,double[] tFeatureVectorB);
        
        private native long nativeLoadImage(String fileName);                       
        private native float nativeImgSimilarity(long img1, long img2);
        
        private native void nativeForTest();
}

