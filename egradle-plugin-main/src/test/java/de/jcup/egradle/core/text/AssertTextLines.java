/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.egradle.core.text;

import static org.junit.Assert.*;

import java.util.Collection;

public class AssertTextLines {

    private Collection<TextLine> lines;

    private AssertTextLines(Collection<TextLine> lines) {
        this.lines = lines;
    }

    public AssertTextLines hasLines(int amount) {
        assertEquals("Not expected amounts of lines!", amount, lines.size());
        return this;
    }

    public AssertTextLines containsOnly(TextLine... expectedLines) {
        if (expectedLines == null) {
            throw new IllegalArgumentException("test case wrong written! lines may not be null!");
        }
        hasLines(expectedLines.length);
        return contains(expectedLines);
    }

    public AssertTextLines contains(TextLine... expectedLines) {
        if (expectedLines == null) {
            throw new IllegalArgumentException("test case wrong written! lines may not be null!");
        }
        String failureMessage = null;
        for (TextLine line : expectedLines) {
            if (!this.lines.contains(line)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Did not found:");
                sb.append(line.toString());
                sb.append(" but only:\n");

                for (TextLine existingLine : lines) {
                    sb.append(existingLine.toString());
                    sb.append("\n");
                }
                failureMessage = sb.toString();
                break;
            }
        }
        if (failureMessage != null) {
            fail(failureMessage);
        }
        return this;
    }

    public static AssertTextLines assertLines(Collection<TextLine> lines) {
        assertNotNull("given lines was null!", lines);
        return new AssertTextLines(lines);
    }

}