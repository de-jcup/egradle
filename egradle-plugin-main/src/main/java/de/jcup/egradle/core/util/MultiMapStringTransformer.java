package de.jcup.egradle.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiMapStringTransformer implements GradleStringTransformer {

	List<SimpleMapStringTransformer> transformers;

	public MultiMapStringTransformer(List<Map<String, String>> maps) {
		transformers = new ArrayList<>();
		if (maps == null) {
			return;
		}
		for (Map<String, String> map : maps) {
			if (map == null) {
				continue;
			}
			SimpleMapStringTransformer simpleTransformer = new SimpleMapStringTransformer(map);
			transformers.add(simpleTransformer);
		}
	}

	@Override
	public String transform(String text) {
		if (text==null){
			return "";
		}
		for (SimpleMapStringTransformer transformer: transformers){
			String transformed = transformer.transform(text);
			if (! text.equals(transformed)){
				return transformed;
			}
		}
		return text;
	}

}
