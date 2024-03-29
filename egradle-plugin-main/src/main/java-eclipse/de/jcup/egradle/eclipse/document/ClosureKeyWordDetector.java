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
package de.jcup.egradle.eclipse.document;

import org.eclipse.jface.text.rules.IWordDetector;

public class ClosureKeyWordDetector implements IWordDetector {

    @Override
    public boolean isWordStart(char c) {
        if (!Character.isLetter(c)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isWordPart(char c) {
        if (Character.isWhitespace(c)) {
            return false;
        }
        if (c == '_') {// we allow _ so "api_" detects not "api" only but "api_" , means different
            return true;
        }
        if (!Character.isLetter(c)) {
            return false;
        }
        return true;
    }
}
