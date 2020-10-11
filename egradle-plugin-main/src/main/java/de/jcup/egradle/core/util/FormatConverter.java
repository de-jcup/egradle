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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FormatConverter {

    /**
     * Converts given string to integer. If not parseable the result will be 0
     * 
     * @param string
     * @return converted string or 0 if not parseable
     */
    public int convertToInt(String string) {
        return convertToInt(string, 0);
    }

    /**
     * Converts given string to integer. If not parseable the result will be given
     * defaultValue
     * 
     * @param string
     * @param defaultValue
     * @return converted string or defaultvalue if not parseable
     */
    public int convertToInt(String string, int defaultValue) {
        int result = defaultValue;
        if (string == null) {
            return result;
        }
        string = string.trim();
        try {
            result = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            handleException(e);
        }
        return result;
    }

    void handleException(NumberFormatException e) {
        /* do nothing per default */
    }

    /**
     * Converts given string to double. If not parseable the result will be 0
     * 
     * @param string
     * @return converted string or 0 if not parseable
     */
    public double convertToDouble(String string) {
        double result = 0;
        if (string == null) {
            return result;
        }
        string = string.trim();
        try {
            result = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            handleException(e);
        }
        return result;
    }

    public List<String> convertToStringList(String string) {
        if (string == null || string.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String[] result = StringUtilsAccess.split(string, ",");
        return Arrays.asList(result);
    }

}
