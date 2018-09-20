package com.vcc.bigdata.elasticsearch.script;

import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.NativeScriptFactory;
import org.elasticsearch.script.ScriptException;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wangwei110 on 2018/9/19.
 */
public class CosineScorerFromAlgFactory implements NativeScriptFactory {
    public static final String SCRIPT_NAME = "cosine_score_from_alg";

    public boolean needsScores() {
        return false;
    }

    public String getName() {
        return SCRIPT_NAME;
    }

    public ExecutableScript newScript(Map<String, Object> param) {

        String field = (java.lang.String) param.get("f");
        if (field == null) {
            throw new ScriptException("Field data param [f] is missing", null, null, SCRIPT_NAME, "native");
        }

//		double[] fea = (double[]) param.get("fea");
//		if (fea == null)
//			throw new ScriptException("Param [fea] is missing", null, null, SCRIPT_NAME, "native");

        ///
        @SuppressWarnings("unchecked")
        ArrayList<Number> f = (ArrayList<Number>) param.get("fea");
        if (f == null)
            throw new ScriptException("Param [fea] is missing", null, null, SCRIPT_NAME, "native");
        double[] fea = new double[f.size()];
        for (int i = 0; i < f.size(); i++)
            fea[i] = f.get(i).doubleValue();
        ///

        CosineScorerFormAlgScript script = new CosineScorerFormAlgScript(field, fea);
        return script;
    }

}
