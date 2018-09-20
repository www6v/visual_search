//package com.vcc.bigdata.elasticsearch.script;
//
//
//import com.lenovo.ar.reco.JniReco;
//import com.vcc.bigdata.elasticsearch.util.FeatureUtil;
//import es.fea.ImgFea.ImFeaBin;
//import es.fea.ImgFea.ImFeaBinArr;
//import org.apache.lucene.util.BytesRef;
//import org.elasticsearch.index.fielddata.ScriptDocValues;
//import org.elasticsearch.script.AbstractDoubleSearchScript;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//
//public class CosineJniScorerScript extends AbstractDoubleSearchScript {
//	private  final Logger logger = LoggerFactory.getLogger(CosineJniScorerScript.class);
//
//	private String field;
//	private double[] fea;
//	private float queryNorm = 0f;
//
//	static{
////		System.loadLibrary("Reco");
////		System.load("/home/wei/workspace/jniReco/libReco.so");
//	}
//
//	private JniReco jniReco = new JniReco();
//
//	public CosineJniScorerScript(String field, double[] fea) {
//		this.field = field;
//		this.fea = fea;
//		this.queryNorm = computeNorm();
//	}
//
//	private float computeNorm() {
//		float n = 0f;
//		for (int i = 0; i < fea.length; i++)
//			n += fea[i] * fea[i];
//		if (n == 0)
//			return 0.0001f;
//		return (float) Math.sqrt(n);
//	}
//
//	@Override
//	public double runAsDouble() {
////		System.loadLibrary("Reco");
//		if (queryNorm == 0)
//			return 0f;
//
//		BytesRef binaryData = ((ScriptDocValues.BytesRefs) doc().get(field)).get(0);
//		ImFeaBinArr feaArr = FeatureUtil.decodeFeaBinary(binaryData);
////		ImFeaArr feaArr = FeatureUtil.decodeFea(binaryData);  /// original
//
//		if (feaArr == null)
//			return -1f;
//		double bestScore = 0.0;
//		for (int i = 0; i < feaArr.getArrCount(); i++) {
////			double score = computeCosineScore(fea, feaArr.getArr(i));
//			double score = computeCosineScoreWithJni(fea, feaArr.getArr(i));
//			if (score > bestScore)
//				bestScore = score;
//		}
//		return bestScore;
//	}
//
//	/* fea: value from search
//     * arr: value in index
//    */
////	private double computeCosineScore(double[] fea, ImFea arr) { /// original
//	private double computeCosineScore(double[] fea, ImFeaBin arr) {
//		double score = 0.0;
//		double n2 = 0.0;
//		double v1, v2;
//
//		logger.info("ImFeaBin.getFList()" + arr.getFList());
//
//		///
//		int sizeList = arr.getFList().size();
//		int feaLength = fea.length;
//		if( feaLength != sizeList) {
//			return score;
//		}
//		///
//
//		for (int i = 0; i < fea.length; i++) {
//			v1 = fea[i];
//			v2 = arr.getF(i);
//			score += v1 * v2;
//			n2 += v2 * v2;
//		}
//		if (n2 == 0)
//			return 0.0;
//		return score / (queryNorm * Math.sqrt(n2));
//	}
//
//	/* fea: value from search
//     * arr: value in index
//    */
//	private double computeCosineScoreWithJni(double[] fea, ImFeaBin arr) {
//		List<Long> fList = arr.getFList();
//		double[] arrayInIndex = fList.stream().mapToDouble(Long::longValue).toArray();
//
//		logger.info( "arrayInIndex length :" + arrayInIndex.length );
//		logger.info( "arrayInIndex :" + arrayInIndex );
//
//		return jniReco.featureSimilarity(fea, arrayInIndex);
//	}
//}
