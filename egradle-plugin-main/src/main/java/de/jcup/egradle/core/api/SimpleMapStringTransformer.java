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
