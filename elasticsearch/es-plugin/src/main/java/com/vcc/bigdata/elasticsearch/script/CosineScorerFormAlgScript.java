package com.vcc.bigdata.elasticsearch.script;


import com.vcc.bigdata.elasticsearch.util.FeatureUtil;
import es.fea.ImgFea.ImFeaBin;
import es.fea.ImgFea.ImFeaBinArr;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.index.fielddata.ScriptDocValues;
import org.elasticsearch.script.AbstractDoubleSearchScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import es.fea.ImgFea;
//import es.fea.ImgFea.ImFea;

public class CosineScorerFormAlgScript extends AbstractDoubleSearchScript {
	private  final Logger logger = LoggerFactory.getLogger(CosineScorerFormAlgScript.class);

	private String field;
	private double[] fea;
	private float queryNorm = 0f;

	public CosineScorerFormAlgScript(String field, double[] fea) {
		this.field = field;
		this.fea = fea;
		this.queryNorm = computeNorm();
	}

	private float computeNorm() {
		float n = 0f;
		for (int i = 0; i < fea.length; i++)
			n += fea[i] * fea[i];
		if (n == 0)
			return 0.0001f;
		return (float) Math.sqrt(n);
	}

	@Override
	public double runAsDouble() {
		if (queryNorm == 0)
			return 0f;

		BytesRef binaryData = ((ScriptDocValues.BytesRefs) doc().get(field)).get(0);
		ImFeaBinArr feaArr = FeatureUtil.decodeFeaBinary(binaryData);
//		ImFeaArr feaArr = FeatureUtil.decodeFea(binaryData);  /// original

		if (feaArr == null)
			return -1f;
		double bestScore = 0.0;
		for (int i = 0; i < feaArr.getArrCount(); i++) {
			double score = computeCosineScore(fea, feaArr.getArr(i));
//			double score = computeCosineScoreWithJni(fea, feaArr.getArr(i));
			if (score > bestScore)
				bestScore = score;
		}
		return bestScore;
	}

	/* fea: value from search
     * arr: value in index
    */
//	private double computeCosineScore(double[] fea, ImFea arr) { /// original
	private double computeCosineScore(double[] fea, ImFeaBin arr) {
		double score = 0.0;
		double n2 = 0.0;
		double v1, v2;


		///
		logger.info("ImFeaBin.getFList()" + arr.getFList());

		int sizeList = arr.getFList().size();
		int feaLength = fea.length;
		if( feaLength != sizeList) {
			logger.info(  "feaLength:" + feaLength +  "  sizeList: " + sizeList);
			return score;
		}

		logger.info(  "feaLength:" + feaLength +  "  sizeList: " + sizeList);
		///

//		for (int i = 0; i < fea.length; i++) {
//			v1 = fea[i];
//			v2 = arr.getF(i);
//			score += v1 * v2;
//			n2 += v2 * v2;
//		}
//		if (n2 == 0)
//			return 0.0;
//		return score / (queryNorm * Math.sqrt(n2));

		for (int i = 0; i < fea.length; i++) {
			v1 = fea[i];
			v2 = arr.getF(i);
			score += v1 * v2;
		}

		return score;
	}
}
