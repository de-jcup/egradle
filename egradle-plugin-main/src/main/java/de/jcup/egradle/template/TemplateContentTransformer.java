package de.jcup.egradle.template;


import java.util.Properties;

import de.jcup.egradle.core.util.SimpleMapStringTransformer;
import de.jcup.egradle.core.util.Transformer;

class TemplateContentTransformer implements Transformer<String>{
	
	private SimpleMapStringTransformer simpleMapStringTransformer;

	/**
	 * Creates a template content transformer. Given properties will be used to build the transformer. The transformer
	 * is immutable - means changes to properties after creation will not influence the transformer.
	 * @param properties
	 */
	public TemplateContentTransformer(Properties properties){
		if (properties == null){
			throw new IllegalArgumentException("'properties' may not be null");
		}
		this.simpleMapStringTransformer = new SimpleMapStringTransformer(properties, "#");
	}
	
	@Override
	public String transform(String source) {
		if (source==null){
			return null;
		}
		String transformed = simpleMapStringTransformer.transform(source);
		return transformed;
	}
	
}