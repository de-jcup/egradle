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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class RelevantCodeCutterTest {

    private static final String _01234_678_0123 = "01234 678 0123";
    private RelevantCodeCutter codeCutterToTest;

    @Before
    public void before() {
        codeCutterToTest = new RelevantCodeCutter();
    }

    @Test
    public void test_0__pos0__returns_0() {
        String code = "0";
        String relevant = codeCutterToTest.getRelevantCode(code, 0);
        assertEquals("0", relevant);
    }

    @Test
    public void test_01234_678_0123__pos5__returns_01234() {
        String code = _01234_678_0123;
        String relevant = codeCutterToTest.getRelevantCode(code, 5);
        assertEquals("01234", relevant);
    }

    @Test
    public void test_01234_678_0123__pos6__returns_6() {
        String code = _01234_678_0123;
        int relevant = codeCutterToTest.getRelevantCodeStartOffset(code, 8);
        assertEquals(6, relevant);
    }

    @Test
    public void start_offset_test_01234_678_0123__pos6__returns_678() {
        String code = _01234_678_0123;
        String relevant = codeCutterToTest.getRelevantCode(code, 6);
        assertEquals("678", relevant);
    }

    @Test
    public void test_01234_678_0123__pos7__returns_678() {
        String code = _01234_678_0123;
        String relevant = codeCutterToTest.getRelevantCode(code, 7);
        assertEquals("678", relevant);
    }

    @Test
    public void test_01234_678_0123__pos8__returns_678() {
        String code = _01234_678_0123;
        String relevant = codeCutterToTest.getRelevantCode(code, 8);
        assertEquals("678", relevant);
    }

    @Test
    public void test_gradle_example1() {
        // @formatter:off
		String code =
		"dependencies{\n"+
		"\tcompile project(':code2doc-api')\n"+
		"\tcompile project(':code2doc-core')\n"+
		"\n"+
		"\tcompile 'org.freemarker:freemarker:2.3.22'\n"+
		"\tcompile 'commons-io:commons-io:2.4'\n"+
        "\n"+
		"}\n"+
        "\n"+
		"file = new File(\"element\")\n";
		int index=code.length();
		code = code +
        "  \n"+
		"bla = \"xxxx\"\n"+
		"java.lang.String gedoeons = \"hallo welt\n\";\n";
		// @formatter:on

        String relevant = codeCutterToTest.getRelevantCode(code, index);
        assertEquals("", relevant);
    }

}
