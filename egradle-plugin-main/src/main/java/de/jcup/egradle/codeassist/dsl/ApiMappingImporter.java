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
package de.jcup.egradle.codeassist.dsl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class ApiMappingImporter {

    /**
     * Imports mapping
     * 
     * @param stream
     * @return map never <code>null</code>. Map keys are shortnames, values are long
     *         names
     * @throws IOException
     */
    public Map<String, String> importMapping(InputStream stream) throws IOException {
        Map<String, String> map = new TreeMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                /* file contains as following "shortName:longName;" */
                String[] parts = StringUtils.split(line, ":;");
                if (parts == null) {
                    continue;
                }
                if (parts.length < 2) {
                    continue;
                }
                map.put(parts[0], parts[1]);
            }
            return map;
        }
    }

}
