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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GradleResourceLinkCalculatorTest {

    private GradleResourceLinkCalculator calculator;

    @Before
    public void before() {
        calculator = new GradleResourceLinkCalculator();
    }

    @Test
    public void static_methodcall_from_javaclass_link_to_class_possible() {
        /* prepare */
        String text = "System.println(\"hello world\");";

        /* execute */
        GradleHyperLinkResult result = calculator.createResourceLinkString(text, 0);

        /* test */
        assertNotNull(result);
        assertEquals("System", result.linkContent);
        assertEquals(0, result.linkOffsetInLine);
        assertEquals(6, result.linkLength);
    }

    @Test
    public void file_with_variables_works_at_first_pos() {
        String text = "File f = new File(rootProject.projectDir,'custom_allprojects.gradle');";
        GradleHyperLinkResult result = calculator.createResourceLinkString(text, 0);
        assertNotNull(result);
        assertEquals("File", result.linkContent);
        assertEquals(0, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void file_with_variables_works_after_new() {
        String text = "File f = new File(rootProject.projectDir,'custom_allprojects.gradle');";
        GradleHyperLinkResult result = calculator.createResourceLinkString(text, 15);
        assertNotNull(result);
        assertEquals("File", result.linkContent);
        assertEquals(13, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void indented_by_tabs_file_with_variables_works_after_new() {
        String text = "\t\t\tFile f = new File(rootProject.projectDir,'custom_allprojects.gradle');";
        GradleHyperLinkResult result = calculator.createResourceLinkString(text, 18);
        assertNotNull(result);
        assertEquals("File", result.linkContent);
        assertEquals(16, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void indented_by_spaces_file_with_variables_works_after_new() {
        String text = "   File f = new File(rootProject.projectDir,'custom_allprojects.gradle');";
        GradleHyperLinkResult result = calculator.createResourceLinkString(text, 18);
        assertNotNull(result);
        assertEquals("File", result.linkContent);
        assertEquals(16, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void null_0_returns_null() {
        assertNull(calculator.createResourceLinkString(null, 0));
    }

    @Test
    public void null_n1_returns_null() {
        assertNull(calculator.createResourceLinkString(null, -1));
    }

    @Test
    public void Test_n1_returns_null() {
        assertNull(calculator.createResourceLinkString("Test", -1));
    }

    @Test
    public void Test_0_returns_NOT_null() {
        assertNotNull(calculator.createResourceLinkString("Test", 0));
    }

    @Test
    public void Test_commata_0_returns_NOT_null__text_contains_only_Test() {
        GradleHyperLinkResult result = calculator.createResourceLinkString("Test,", 0);
        assertNotNull(result);
        assertEquals("Test", result.linkContent);
        assertEquals(0, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void TestBracketOpen_0_returns_NOT_null__text_contains_only_Test() {
        GradleHyperLinkResult result = calculator.createResourceLinkString("Test(", 0);
        assertNotNull(result);
        assertEquals("Test", result.linkContent);
        assertEquals(0, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void Test_lt_0_returns_NOT_null__text_contains_only_Test() {
        GradleHyperLinkResult result = calculator.createResourceLinkString("Test<", 0);
        assertNotNull(result);
        assertEquals("Test", result.linkContent);
        assertEquals(0, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void lt_Test_gt_0_returns_NOT_null__text_contains_only_Test() {
        GradleHyperLinkResult result = calculator.createResourceLinkString("<Test>", 1);
        assertNotNull(result);
        assertEquals("Test", result.linkContent);
        assertEquals(1, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void BracketOpenTestBracketClose_1_returns_NOT_null__text_contains_only_Test() {
        GradleHyperLinkResult result = calculator.createResourceLinkString("(Test)", 1);
        assertNotNull(result);
        assertEquals("Test", result.linkContent);
        assertEquals(1, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void TestArray_1_returns_NOT_null__text_contains_only_Test() {
        GradleHyperLinkResult result = calculator.createResourceLinkString("Test[]", 1);
        assertNotNull(result);
        assertEquals("Test", result.linkContent);
        assertEquals(0, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void Test_space_Array_1_returns_NOT_null__text_contains_only_Test() {
        GradleHyperLinkResult result = calculator.createResourceLinkString("Test []", 1);
        assertNotNull(result);
        assertEquals("Test", result.linkContent);
        assertEquals(0, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void Test_curly_brace_0_returns_NOT_null__text_contains_only_Test() {
        GradleHyperLinkResult result = calculator.createResourceLinkString("Test{", 0);
        assertNotNull(result);
        assertEquals("Test", result.linkContent);
        assertEquals(0, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void dot_Test_newline_returns_NOT_null__text_contains_only_Test() {
        GradleHyperLinkResult result = calculator.createResourceLinkString(".Test\n", 1);
        assertNotNull(result);
        assertEquals("Test", result.linkContent);
        assertEquals(1, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void dot_Test_end_returns_NOT_null__text_contains_only_Test() {
        GradleHyperLinkResult result = calculator.createResourceLinkString(".Test", 1);
        assertNotNull(result);
        assertEquals("Test", result.linkContent);
        assertEquals(1, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void Test_space_curly_brace_0_returns_NOT_null__text_contains_only_Test() {
        GradleHyperLinkResult result = calculator.createResourceLinkString("Test {", 0);
        assertNotNull(result);
        assertEquals("Test", result.linkContent);
        assertEquals(0, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void BracketOpenTestArrayBracketClose_2_returns_NOT_null__text_contains_only_Test() {
        GradleHyperLinkResult result = calculator.createResourceLinkString("(Test[])", 2);
        assertNotNull(result);
        assertEquals("Test", result.linkContent);
        assertEquals(1, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }

    @Test
    public void Test_3_returns_NOT_null() {
        assertNotNull(calculator.createResourceLinkString("Test", 3));
    }

    @Test
    public void Test_4_returns_null() {
        assertNull(calculator.createResourceLinkString("Test", 4));
    }

    @Test
    public void Test_5_returns_null() {
        assertNull(calculator.createResourceLinkString("Test", 5));
    }

    @Test
    public void hello_World_is_good__pos0_returns_null() {

        String line = "hello World is good";
        // 0123456
        GradleHyperLinkResult result = calculator.createResourceLinkString(line, 0);
        assertNull(result);
    }

    @Test
    public void hello_World_is_good__pos4_returns_null() {

        String line = "hello World is good";
        // 0123456
        GradleHyperLinkResult result = calculator.createResourceLinkString(line, 4);
        assertNull(result);
    }

    @Test
    public void hello_World_is_good__pos5_returns_null() {

        String line = "hello World is good";
        // 0123456
        GradleHyperLinkResult result = calculator.createResourceLinkString(line, 5);
        if (result != null) {
            fail("expected null but result was:" + result);
        }
    }

    @Test
    public void hello_World_is_good__pos6_returns_not_null__World_is_linkContent_offset_6_length_5() {

        String line = "hello World is good";
        // 0123456
        GradleHyperLinkResult result = calculator.createResourceLinkString(line, 6);
        assertNotNull(result);
        assertEquals("World", result.linkContent);
        assertEquals(6, result.linkOffsetInLine);
        assertEquals(5, result.linkLength);
    }

    @Test
    public void hello_World_is_good__pos8_returns_not_null__World_is_linkContent_offset_6_length_5() {

        String line = "hello World is good";
        // 012345678901234
        GradleHyperLinkResult result = calculator.createResourceLinkString(line, 8);
        assertNotNull(result);
        assertEquals("World", result.linkContent);
        assertEquals(6, result.linkOffsetInLine);
        assertEquals(5, result.linkLength);
    }

    @Test
    public void hello_World_is_good__pos10_returns_not_null__World_is_linkContent_offset_6_length_5() {

        String line = "hello World is good";
        // 012345678901234
        GradleHyperLinkResult result = calculator.createResourceLinkString(line, 10);
        assertNotNull(result);
        assertEquals("World", result.linkContent);
        assertEquals(6, result.linkOffsetInLine);
        assertEquals(5, result.linkLength);
    }

    @Test
    public void hello_World_is_good__pos11_returns_null() {

        String line = "hello World is good";
        // 0123456789012345678
        GradleHyperLinkResult result = calculator.createResourceLinkString(line, 15);
        assertNull(result);
    }

    @Test
    public void hello_World_is_good__pos15_returns_null() {

        String line = "hello World is good";
        // 0123456789012345678
        GradleHyperLinkResult result = calculator.createResourceLinkString(line, 15);
        assertNull(result);
    }

    @Test
    public void hello_World_is_Good__pos10_returns_not_null__World_is_linkContent_offset_6_length_5() {

        String line = "hello World is Good";
        // 012345678901234
        GradleHyperLinkResult result = calculator.createResourceLinkString(line, 10);
        assertNotNull(result);
        assertEquals("World", result.linkContent);
        assertEquals(6, result.linkOffsetInLine);
        assertEquals(5, result.linkLength);
    }

    @Test
    public void hello_World_is_Good__pos15_returns_not_null__Good_is_linkContent_offset_15_length_4() {

        String line = "hello World is Good";
        // 0123456789012345
        GradleHyperLinkResult result = calculator.createResourceLinkString(line, 15);
        assertNotNull(result);
        assertEquals("Good", result.linkContent);
        assertEquals(15, result.linkOffsetInLine);
        assertEquals(4, result.linkLength);
    }
}
