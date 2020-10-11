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
package de.jcup.egradle.core.model.groovyantlr;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;

/**
 * A simple reflection util which does create a map of names for all fields
 * defined in {@link GroovyTokenTypes}. So it's easier to show debug information
 * - e.g. instead of type 7, a "SLIST" can be printed
 * 
 * @author Albert Tregnaghi
 *
 */
class GroovyTokenTypeDebugInfoInspector implements GroovyTokenTypes {

    private Map<Integer, String> map = new TreeMap<>();

    public GroovyTokenTypeDebugInfoInspector() {
        Field[] fields = GroovyTokenTypes.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                Object r = field.get(this);
                if (r instanceof Integer) {
                    Integer i = (Integer) r;
                    map.put(i, field.getName());
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                break;
            }
        }
    }

    public String getGroovyTokenTypeName(int type) {
        String data = map.get(type);
        if (data == null) {
            return "???";
        }
        return data;
    }
}