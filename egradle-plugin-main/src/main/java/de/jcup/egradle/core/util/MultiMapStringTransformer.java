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
        if (text == null) {
            return "";
        }
        for (SimpleMapStringTransformer transformer : transformers) {
            String transformed = transformer.transform(text);
            if (!text.equals(transformed)) {
                return transformed;
            }
        }
        return text;
    }

}
