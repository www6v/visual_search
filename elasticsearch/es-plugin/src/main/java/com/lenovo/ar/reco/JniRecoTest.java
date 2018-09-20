package com.lenovo.ar.reco;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class JniRecoTest {

  //protected final Log logger = LogFactory.getLog(getClass());

	long nativeCFeatExtractor;
  static long nativeTImageUA;
  static long nativeTImageUB;

	private static final String ROOT_DIR = "/home/wangwei/jniTest/jniReco";
    private static final String SEPARATOR = "/";

  private String fileDir;
  private String targetId;

  static double[] tFeatureVectorA;
  static double[] tFeatureVectorB;
  static double[] tFeatureVector;

  JniReco jniReco = new JniReco();

    public JniRecoTest(){
        ///System.out.println("JniReco createNativeObject.");
        ///String strModelTag = "DenseNet_121";
        ///nativeCFeatExtractor = jniReco.createNativeObj(strModelTag);
    }

	public static void main(String[] args) throws IOException{
		///System.load("/home/wangwei/jniTest/jniReco/libReco.so");
    System.loadLibrary("Reco");  
        
//    Instant initStart = Instant.now();
    
//    Instant initEnd = Instant.now();
//    System.out.println("Duration.between(initStart, initEnd): " + Duration.between(initStart, initEnd).toMillis());

        JniRecoTest jniRecoTest = new JniRecoTest();
        jniRecoTest.testMethod(jniRecoTest.jniReco);
    ///jniReco.forTest();                           
	}

    private  void testMethod( JniReco jniReco) {
        double[] arr = extractFromByteTest(jniReco);
        jniReco.saveVectorToFile(arr, "/home/wangwei/jniTest/jniReco/tmp/tmp.txt");  /// test

        this.perTest(jniReco);
        this.similarityTest(jniReco);
        this.imgSimilarityTest(jniReco);
        this.extractFromByteTest(jniReco);
                
        jniReco.destroyExtractor();
    }

    private  double[] extractFromByteTest(JniReco jniReco) {
     System.out.println("extractFromByteTest----------------");
     Random rand =new Random(25);
     
     byte[] photo= new byte[1024];     
     rand.nextBytes(photo);
     int width = 1024;
     int height = 768;
     
     double[] result = jniReco.extractFromByte(photo, 1024, 768);
     System.out.println("result: " + result.length);
     return result;
 }

  private  void perTest(JniReco jniReco) {
    System.out.println("perTest----------------");
    String filePath = "/home/wangwei/jniTest/jniReco/pic";
    for(int i=1; i<=10; i++ ) {
      Instant extractStart = Instant.now();        System.out.println("file : "+"img" + String.valueOf(i) + ".jpg");
      tFeatureVector = jniReco.extract(filePath + "/" + "img" + String.valueOf(i) + ".jpg"); 
      Instant extractEnd = Instant.now();
      System.out.println("Duration.between(extractStart, extractEnd) " + i + ":" + Duration.between(extractStart, extractEnd).toMillis()); 
      System.out.println("-------------------------------- " );         
    }
    
    ///jniReco.destroyExtractor();
  }

  private  void similarityTest(JniReco jniReco) {
    System.out.println("similarityTest----------------");
    String filePath = "/home/wangwei/jniTest/jniReco/pic";
    
    Instant extractStartA = Instant.now();
    tFeatureVectorA = jniReco.extract(filePath + "/" + "img1.jpg");
    Instant extractEndA = Instant.now();
    System.out.println("Duration.between(extractStartA, extractEndA) " + Duration.between(extractStartA, extractEndA).toMillis());        

    Instant extractStartB = Instant.now();
    tFeatureVectorB = jniReco.extract(filePath + "/" + "img2.jpg");
    Instant extractEndB = Instant.now();
    System.out.println("Duration.between(extractStartB, extractEndB) " + Duration.between(extractStartB, extractEndB).toMillis());

    jniReco.doubleArrayToFile(tFeatureVectorA, "img1.jpg");
         
    ///tFeatureVectorB = jniReco.extract("abc.jpg"); 

    ///jniReco.destroyExtractor();
 
    Instant similarityStart = Instant.now();   
    jniReco.featureSimilarity(tFeatureVectorA, tFeatureVectorB);
    Instant similarityEnd = Instant.now();
    System.out.println("Duration.between(similarityStart, similarityEnd) " + Duration.between(similarityStart, similarityEnd).toMillis());   
  }

  private  void imgSimilarityTest(JniReco jniReco) {
    System.out.println("imgSimilarityTest----------------");
    String filePath = "/home/wangwei/jniTest/jniReco/pic";
      
    nativeTImageUA = jniReco.loadImage(filePath + "/" +"img1.jpg");
    nativeTImageUB = jniReco.loadImage(filePath + "/" +"img2.jpg"); 
    //nativeTImageUB = jniReco.loadImage("img1.jpg"); 
    float imgSimilarity= jniReco.imgSimilarity(nativeTImageUA, nativeTImageUB);   
    
    System.out.println("imgSimilarity(nativeTImageUA, nativeTImageUB): " + imgSimilarity); 
  }


}

