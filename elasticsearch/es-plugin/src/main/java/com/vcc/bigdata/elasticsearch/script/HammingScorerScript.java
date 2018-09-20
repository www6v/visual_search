package com.vcc.bigdata.elasticsearch.script;

import org.apache.lucene.util.BytesRef;
import org.elasticsearch.index.fielddata.ScriptDocValues;
import org.elasticsearch.script.AbstractDoubleSearchScript;

import com.vcc.bigdata.elasticsearch.util.FeatureUtil;

import es.fea.ImgFea.ImFeaBin;
import es.fea.ImgFea.ImFeaBinArr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.StopWatch;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class HammingScorerScript extends AbstractDoubleSearchScript {
	private  final Logger logger = LoggerFactory.getLogger(HammingScorerScript.class);

	private String field;
	private long[] fea;

	public HammingScorerScript(String field, long[] fea) {
		this.field = field;
		this.fea = fea;
	}

	@Override
	public double runAsDouble() {

		String threadName = Thread.currentThread().getName();
		logger.error( "Thread.currentThread().getName() : " + threadName );

		StopWatch sw = new StopWatch();  ///

//		ImFeaBinArr feaArr = getImFeaBinArr(sw);

//		double bestScore = 0;
//		try {
//			bestScore = ayncCompute(sw);
//		} catch (InterruptedException e) {
//			bestScore = 0;
//			logger.error( e.getMessage() );
//		} catch (ExecutionException e) {
//			bestScore = 0;
//			logger.error( e.getMessage() );
//		}

		double bestScore = syncCompute(sw);

		printPerfmance(sw);

		return bestScore;
	}

	private void printPerfmance(StopWatch sw) {
		logger.info( sw.prettyPrint());
		logger.info("sw.getTotalTimeMillis()" + sw.getTotalTimeMillis());
		logger.info("sw.getTaskCount()" + sw.getTaskCount());
	}

	private Double ayncCompute(StopWatch sw) throws InterruptedException, java.util.concurrent.ExecutionException {
		/// 异步
		CompletableFuture<BytesRef> future1 = CompletableFuture.supplyAsync(() -> {
			return retrieveFeaturedbFromIndex(sw);
		});
		CompletableFuture<ImFeaBinArr> future2 =  future1.thenCompose(binaryData -> {
			return CompletableFuture.supplyAsync(() -> {
				return serializeFeaturedb(sw, binaryData);
			});
		});
		CompletableFuture<Double> future3 =  future2.thenCompose( feaArr -> {
			return CompletableFuture.supplyAsync(() -> {
				return computeScore(sw, feaArr);
			});
		});

		return future3.get();
//		System.out.println(fff.get());
	}

	private double syncCompute(StopWatch sw) {
		/// 同步
		BytesRef binaryData = retrieveFeaturedbFromIndex(sw);
		ImFeaBinArr feaArr = serializeFeaturedb(sw, binaryData);
		return computeScore(sw, feaArr);
	}

	private double computeScore(StopWatch sw, ImFeaBinArr feaArr) {
		sw.start("computeHammintonDistance task");

		if (feaArr == null)
			return -1f;
		double bestScore = 0.0;
		for (int i = 0; i < feaArr.getArrCount(); i++) {      ///
			double score = computeHammintonDistance(fea, feaArr.getArr(i));       ///
			if (score > bestScore)
				bestScore = score;
		}

		sw.stop();
		return bestScore;
	}

//	private ImFeaBinArr getImFeaBinArr(StopWatch sw) {
//		BytesRef binaryData = retrieveFeaturedbFromIndex(sw);
//
//		ImFeaBinArr feaArr = serializeFeaturedb(sw, binaryData);
//		return feaArr;
//	}

	private BytesRef retrieveFeaturedbFromIndex(StopWatch sw) {
		sw.start("retrieve feature from index.");  ///
		BytesRef binaryData = ((ScriptDocValues.BytesRefs) doc().get(field)).get(0);
		sw.stop();
		return binaryData;
	}

	private ImFeaBinArr serializeFeaturedb(StopWatch sw, BytesRef binaryData) {
		sw.start("serialize  the  featuredb.");  ///
		ImFeaBinArr feaArr = FeatureUtil.decodeFeaBinary(binaryData);
		sw.stop();  ///
		return feaArr;
	}

	/* fea: value from search
	 * arr: value in index
	 */
	private double computeHammintonDistance(long[] fea, ImFeaBin arr) {
		double score = 0.0;
		long v1, v2;

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


		for (int i = 0; i < fea.length; i++) {		///logger.error( "i: " + i);
			v1 = fea[i];   ///logger.error( "fea[i]: " + fea[i]);
			v2 = arr.getF(i); ///logger.error( "arr.getF(i): " + arr.getF(i));

			score += (32 - Long.bitCount(v1 ^ v2)); ///logger.error( "score: " + score);
		}

		logger.info( " ------------- " );
		return score;
	}
}
