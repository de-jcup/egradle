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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.text.OffsetCalculator;

public class ExtendedSourceBufferTest {

    private ExtendedSourceBuffer bufferToTest;
    private OffsetCalculator mockedCalculator;

    @Before
    public void before() {
        bufferToTest = new ExtendedSourceBuffer();
        mockedCalculator = mock(OffsetCalculator.class);
        bufferToTest.calculator = mockedCalculator;
    }

    @Test
    public void appendMissingLineEndingToLastLine_appends_newline_when_no_content() {
        write("", bufferToTest);
        bufferToTest.ensureFrozen();
        /* check */
        assertEquals(1, bufferToTest.frozenLinesAsArray.length);
        assertEquals("", bufferToTest.frozenLinesAsArray[0].toString());
        /* execute */
        bufferToTest.appendLineEndToLastLineIfMissing();
        assertEquals(1, bufferToTest.frozenLinesAsArray.length);
        assertEquals("\n", bufferToTest.frozenLinesAsArray[0].toString());

    }

    @Test
    public void appendMissingLineEndingToLastLine_appends_newline_when_content_but_newlinew_is_missing() {
        write("abc", bufferToTest);
        bufferToTest.ensureFrozen();
        /* check */
        assertEquals(1, bufferToTest.frozenLinesAsArray.length);
        assertEquals("abc", bufferToTest.frozenLinesAsArray[0].toString());
        /* execute */
        bufferToTest.appendLineEndToLastLineIfMissing();
        assertEquals(1, bufferToTest.frozenLinesAsArray.length);
        assertEquals("abc\n", bufferToTest.frozenLinesAsArray[0].toString());

    }

    @Test
    public void appendMissingLineEndingToLastLine_appends_no_additional_newline_when_newline_exists() {
        write("\n", bufferToTest);
        /* check */
        bufferToTest.ensureFrozen();
        assertEquals(2, bufferToTest.frozenLinesAsArray.length);
        assertEquals("\n", bufferToTest.frozenLinesAsArray[0].toString());
        /* execute */
        bufferToTest.appendLineEndToLastLineIfMissing();
        assertEquals(2, bufferToTest.frozenLinesAsArray.length);
        assertEquals("\n", bufferToTest.frozenLinesAsArray[0].toString());

    }

    @Test
    public void appendMissingLineEndingToLastLine_appends_no_additional_newline_when_newline_with_content_exists() {
        write("abc\n", bufferToTest);
        /* check */
        bufferToTest.ensureFrozen();
        assertEquals(2, bufferToTest.frozenLinesAsArray.length);
        assertEquals("abc\n", bufferToTest.frozenLinesAsArray[0].toString());
        /* execute */
        bufferToTest.appendLineEndToLastLineIfMissing();
        assertEquals(2, bufferToTest.frozenLinesAsArray.length);
        assertEquals("abc\n", bufferToTest.frozenLinesAsArray[0].toString());

    }

    @Test
    public void getOffset_for_text_EMPTY_line_10_column_20__calculator_is_called_with_frozenLinesAsArray__and_line_10_column_20() {
        /* prepare */
        write("", bufferToTest);

        /* execute */
        bufferToTest.getOffset(10, 20);

        /* test */

        // remark: StringBuilder and StringBuffer do not override equals of
        // Object. So a mockito verify(mockedCalculator).getOffset(aryEq(new
        // StringBuilder[]{new StringBuilder()}...) DOES NOT WORK!
        // so test frozen array content before and then check mocked called with
        // the frozen array
        assertEquals(1, bufferToTest.frozenLinesAsArray.length);
        assertEquals("", bufferToTest.frozenLinesAsArray[0].toString());

        verify(mockedCalculator).calculatetOffset(bufferToTest.frozenLinesAsArray, 10, 20);
    }

    @Test
    public void writing_text_HELLOWORLD_cr_NEWLINE_results_in_frozenLinesArray_length2() {
        /* prepare */
        write("HELLOWORLD\nNEWLINE", bufferToTest);

        /* execute */
        bufferToTest.ensureFrozen();

        /* test */
        assertEquals(2, bufferToTest.frozenLinesAsArray.length);
    }

    @Test
    public void unix_writing_text_12345_lf_NEWLINE_results_in_frozenLinesArray_first_line_has_length_of_6() {
        /* prepare */
        write("12345\nNEWLINE", bufferToTest);

        /* execute */
        bufferToTest.ensureFrozen();

        /* test */
        assertEquals(6, bufferToTest.frozenLinesAsArray[0].length());
    }

    @Test
    public void mac_writing_text_12345_cr_NEWLINE_results_in_frozenLinesArray_first_line_has_length_of_6() {
        /* prepare */
        write("12345\rNEWLINE", bufferToTest);

        /* execute */
        bufferToTest.ensureFrozen();

        /* test */
        assertEquals(6, bufferToTest.frozenLinesAsArray[0].length());
    }

    @Test
    public void windows_writing_text_12345_cr_lf_NEWLINE_results_in_frozenLinesArray_first_line_has_length_of_7() {
        /* prepare */
        write("12345\r\nNEWLINE", bufferToTest);

        /* execute */
        bufferToTest.ensureFrozen();

        /* test */
        assertEquals(7, bufferToTest.frozenLinesAsArray[0].length());
    }

    @Test
    public void write4LinesSeparatedByLf_results_in_4_frozen_lines() {
        /* prepare */
        String text = "def variable1='value1'\n\n\ndef variable2='value2'";

        /* execute */
        write(text, bufferToTest);
        bufferToTest.ensureFrozen();

        /* test */
        assertEquals(4, bufferToTest.frozenLinesAsArray.length);

    }

    private void write(String string, ExtendedSourceBuffer bufferToTest) {
        for (char c : string.toCharArray()) {
            bufferToTest.write(c);
        }

    }

}
