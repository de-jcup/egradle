/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.egradle.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SimpleMapStringTransformer implements GradleStringTransformer{

	private static final Pattern DOT_REPLACE = Pattern.compile("\\.");
	private Map<Pattern,String> xmap = new HashMap<>();
	
	/**
	 * Builds the transformer - transformer is immutable, changes to map after creation will have no effect to the transformer
	 * @param map
	 */
	public SimpleMapStringTransformer(Map<?,?> map){
		this(map, (String)null);
	}
	
	/**
	 * Builds the transformer - transformer is immutable, changes to map after creation will have no effect to the transformer
	 * @param map
	 * @param separator - separator to use. if <code>null</code> the default separator "\\$" will be used
	 */
	public SimpleMapStringTransformer(Map<?,?> map,String separator){
		if (separator==null){
			separator = "\\$";
		}
		for (Object key: map.keySet()){
			if (! (key instanceof String)){
				continue;
			}
			String strKey = key.toString();
			Object objValue = map.get(strKey);
			if (! (objValue instanceof String)){
				continue;
			}
			String value = objValue.toString();
			String newKey = DOT_REPLACE.matcher(strKey).replaceAll("\\\\.");
			String regex = separator+"\\{"+newKey+"\\}";
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
