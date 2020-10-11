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
package de.jcup.egradle.junit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EGradleJUnitTaskVariableReplacement {

    public static final String TASKS_VARIABLE = "clean test"; // backward
                                                              // compatible -
                                                              // the
                                                              // "variable" is
                                                              // same as the
                                                              // default
                                                              // before, so it
                                                              // will always
                                                              // be replaced

    private static final Pattern PATTERN = Pattern.compile(TASKS_VARIABLE);

    /**
     * Replaces origin parts where {@link #TASKS_VARIABLE} is used
     * 
     * @param origin
     * @param replacement
     * @return replaced tasks string
     */
    public String replace(String origin, String replacement) {
        if (origin == null) {
            return null;
        }
        if (replacement == null) {
            return origin;
        }
        Matcher matcher = PATTERN.matcher(origin);
        String result = matcher.replaceAll(replacement);
        return result;
    }

}
