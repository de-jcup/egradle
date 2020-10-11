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
package de.jcup.egradle.codeassist;

import org.apache.commons.lang3.StringUtils;

/**
 * Reduces code to relevant parts for code completion
 * 
 * @author albert TODO ATR, 08.01.2016: return result should be a dedicated type
 *         instead of string so it would be possible to differ between variable
 *         inserts and method calls (xyz versus xyz.). Also point should be also
 *         a seperator...
 */
public class RelevantCodeCutter {

    /**
     * <pre>
    				   element fi bb 
    				      ^-- fi
    				   element fi bb 
    				        ^-- fi
    				   element .fi bb     
    				      ^--- .fi
    				   element fi  bb
    				         ^--- empty 
    				         
    				   element
    				   ^-- offset first character in line
    				         ^-- other: given offset
    				    01234 678 0123  
    				    |
    				    |-- offset 0 ->right 0, left nothing!
     * </pre>
     * 
     * @param code   when null always an empty string will be returned
     * @param offset when negative always an empty string will be returned
     * @return relevant code or empty
     */
    String getRelevantCode(String code, int offset) {
        if (code == null) {
            return StringUtils.EMPTY;
        }
        if (offset < 0) {
            return StringUtils.EMPTY;
        }
        String leftText = getLeftText(code, offset);
        String rightText = getRightText(code, offset);
        return leftText + rightText;
    }

    /**
     * Return start offset for given code or -1
     * 
     * @param code
     * @param offset
     * @return start offset for given code or -1s
     */
    public int getRelevantCodeStartOffset(String code, int offset) {
        if (code == null) {
            return -1;
        }
        if (offset < 0) {
            return -1;
        }
        String leftText = getLeftText(code, offset);
        int newOffset = offset - leftText.length();
        return newOffset;
    }

    private String getLeftText(String code, int offset) {
        char[] chars = code.toCharArray();
        int start = offset - 1;
        if (start < 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = start; i >= 0; i--) {
            char c = chars[i];
            if (isDelimiter(c)) {
                break;
            }
            sb.append(c);
        }
        String result = StringUtils.reverse(sb.toString());
        return result;
    }

    private String getRightText(String code, int offset) {
        char[] chars = code.toCharArray();
        if (chars.length <= offset) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < chars.length; i++) {
            char c = chars[i];
            if (isDelimiter(c)) {
                break;
            }
            sb.append(c);
        }
        String data = StringUtils.removeEndIgnoreCase(sb.toString(), "\r");
        return data;
    }

    private boolean isDelimiter(char c) {
        if (c == ' ') {
            return true;
        }
        if (c == '\n') {
            return true;
        }
        if (c == '\t') {
            return true;
        }
        return false;
    }
}
