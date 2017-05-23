package de.jcup.egradle.template;


import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.util.Transformer;

class TemplateFileNameTransformer implements Transformer<String>{
	private Properties properties;

	public TemplateFileNameTransformer(Properties properties){
		if (properties == null){
			throw new IllegalArgumentException("'properties' may not be null");
		}
		this.properties=properties;
	}
	
	@Override
	public String transform(String source) {
		if (source==null){
			return null;
		}
		if (source.startsWith("_")){
			source = StringUtils.substringAfterLast(source,"_");
		}
		if (source.startsWith("$")){
			String key = StringUtils.substringAfterLast(source,"$");
			
			String found = properties.getProperty(key);
			
			if (StringUtils.isNotBlank(found)){
				return found;
			}
		}
		return source;
	}
	
}