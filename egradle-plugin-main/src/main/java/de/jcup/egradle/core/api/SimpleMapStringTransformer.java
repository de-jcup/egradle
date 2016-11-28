package de.jcup.egradle.core.api;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SimpleMapStringTransformer implements GradleStringTransformer{

	private static final Pattern DOT_REPLACE = Pattern.compile("\\.");
	private Map<Pattern,String> xmap = new HashMap<>();
	
	public SimpleMapStringTransformer(Map<String,String> map){
		for (String key: map.keySet()){
			String value = map.get(key);
			String newKey = DOT_REPLACE.matcher(key).replaceAll("\\\\.");
			String regex = "\\$\\{"+newKey+"\\}";
			Pattern p = Pattern.compile(regex);
			xmap.put(p, value);
		}
	}
	
	@Override
	public String transform(String text) {
		String transformed=text;
		for (Pattern p: xmap.keySet()){
			String replacement = xmap.get(p);
			transformed = p.matcher(transformed).replaceAll(replacement);
		}
		return transformed;
	}

}
