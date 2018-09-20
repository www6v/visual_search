//package com.vcc.bigdata.elasticsearch.script;
//
//import org.elasticsearch.script.ExecutableScript;
//import org.elasticsearch.script.NativeScriptFactory;
//import org.elasticsearch.script.ScriptException;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//public class CosineJniScorerFactory implements NativeScriptFactory {
//	public static final String SCRIPT_NAME = "cosine_jni_score";
//
//	public boolean needsScores() {
//		return false;
//	}
//
//	public String getName() {
//		return SCRIPT_NAME;
//	}
//
//	public ExecutableScript newScript(Map<String, Object> param) {
//
////		System.loadLibrary("Reco");
//
//		String field = (String) param.get("f");
//		if (field == null) {
//			throw new ScriptException("Field data param [f] is missing", null, null, SCRIPT_NAME, "native");
//		}
//
////		double[] fea = (double[]) param.get("fea");
////		if (fea == null)
////			throw new ScriptException("Param [fea] is missing", null, null, SCRIPT_NAME, "native");
//
//		///
//		@SuppressWarnings("unchecked")
//		ArrayList<Number> f = (ArrayList<Number>) param.get("fea");
//		if (f == null)
//			throw new ScriptException("Param [fea] is missing", null, null, SCRIPT_NAME, "native");
//		double[] fea = new double[f.size()];
//		for (int i = 0; i < f.size(); i++)
//			fea[i] = f.get(i).doubleValue();
//		///
//
//		CosineJniScorerScript script = new CosineJniScorerScript(field, fea);
//		return script;
//	}
//
//}
