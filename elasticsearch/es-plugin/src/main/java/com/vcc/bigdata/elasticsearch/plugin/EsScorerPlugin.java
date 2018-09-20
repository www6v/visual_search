package com.vcc.bigdata.elasticsearch.plugin;

import java.util.ArrayList;
import java.util.List;

import com.vcc.bigdata.elasticsearch.script.CosineScorerFromAlgFactory;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.ScriptPlugin;
import org.elasticsearch.script.NativeScriptFactory;

import com.vcc.bigdata.elasticsearch.script.CosineScorerFactory;
import com.vcc.bigdata.elasticsearch.script.HammingScorerFactory;
//import com.vcc.bigdata.elasticsearch.script.CosineJniScorerFactory;

public class EsScorerPlugin extends Plugin implements ScriptPlugin {
	public List<NativeScriptFactory> getNativeScripts() {
		List<NativeScriptFactory> list = new ArrayList<NativeScriptFactory>();
		list.add(new CosineScorerFactory());
		list.add(new HammingScorerFactory());
		list.add(new CosineScorerFromAlgFactory());

//		list.add(new CosineJniScorerFactory());

		return list;
	}

}
