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
package de.jcup.egradle.template;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.util.Transformer;

class TemplateFileNameTransformer implements Transformer<String> {
	private Properties properties;

	public TemplateFileNameTransformer(Properties properties) {
		if (properties == null) {
			throw new IllegalArgumentException("'properties' may not be null");
		}
		this.properties = properties;
	}

	@Override
	public String transform(String source) {
		if (source == null) {
			return null;
		}
		if (source.startsWith("_")) {
			source = StringUtils.substringAfterLast(source, "_");
		}
		if (source.startsWith("$")) {
			String key = StringUtils.substringAfterLast(source, "$");

			String found = properties.getProperty(key);

			if (StringUtils.isNotBlank(found)) {
				return found;
			}
		}
		return source;
	}

}