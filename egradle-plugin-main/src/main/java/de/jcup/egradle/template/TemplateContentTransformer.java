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

import de.jcup.egradle.core.util.SimpleMapStringTransformer;
import de.jcup.egradle.core.util.Transformer;

class TemplateContentTransformer implements Transformer<String> {

    private SimpleMapStringTransformer simpleMapStringTransformer;

    /**
     * Creates a template content transformer. Given properties will be used to
     * build the transformer. The transformer is immutable - means changes to
     * properties after creation will not influence the transformer.
     * 
     * @param properties
     */
    public TemplateContentTransformer(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("'properties' may not be null");
        }
        this.simpleMapStringTransformer = new SimpleMapStringTransformer(properties, "#");
    }

    @Override
    public String transform(String source) {
        if (source == null) {
            return null;
        }
        String transformed = simpleMapStringTransformer.transform(source);
        return transformed;
    }

}