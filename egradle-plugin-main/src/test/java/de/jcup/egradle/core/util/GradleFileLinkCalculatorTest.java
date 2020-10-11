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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class GradleFileLinkCalculatorTest {

    private GradleFileLinkCalculator calcToTest;

    @Before
    public void before() {
        calcToTest = new GradleFileLinkCalculator();
    }

    @Test
    public void transformer_not_null_when_calculator_link_content_is_empty_no_transformer_called() {
        /* prepare */
        GradleStringTransformer transformer = mock(GradleStringTransformer.class);
        calcToTest.setTransformer(transformer);

        /* execute */
        calcToTest.createFileLinkString("apply from: ' '", 0);

        /* test */
        verify(transformer, never()).transform(any());
    }

    @Test
    public void transformer_not_null_when_calculator_link_content_available_transformer_called() {
        /* prepare */
        GradleStringTransformer transformer = mock(GradleStringTransformer.class);
        calcToTest.setTransformer(transformer);

        /* execute */
        calcToTest.createFileLinkString("apply from: 'bla.gradle'", 0);

        /* test */
        verify(transformer).transform("bla.gradle");
    }

    @Test
    public void transformer_not_null_when_calculator_link_content_is_set_transformer_result_is_returned() {
        /* prepare */
        GradleStringTransformer transformer = mock(GradleStringTransformer.class);
        when(transformer.transform("bla.gradle")).thenReturn("blub.gradle");
        calcToTest.setTransformer(transformer);

        /* execute */
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'bla.gradle'", 0);

        /* test */
        assertNotNull(result);
        assertEquals("blub.gradle", result.linkContent);

    }

    @Test
    public void transformer_not_null_when_calculator_link_content_is_set_but_transformer_result_is_null_origin_text_is_returned() {
        /* prepare */
        GradleStringTransformer transformer = mock(GradleStringTransformer.class);
        when(transformer.transform("bla.gradle")).thenReturn(null);
        calcToTest.setTransformer(transformer);

        /* execute */
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'bla.gradle'", 0);

        /* test */
        assertNotNull(result);
        verify(transformer).transform("bla.gradle");
        assertEquals("bla.gradle", result.linkContent);

    }

    @Test
    public void link_content_is_string_examples_1_dot_gradle__for__apply_from_single_quote_string_examples_1_dot_gradle_single_quote__index_is_13() {
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'string-examples1.gradle'", 13);

        /* test */
        assertNotNull(result);
        assertEquals("string-examples1.gradle", result.linkContent);
    }

    @Test
    public void link_content_is_libraries_dot_gradle__for__apply_from_single_quote_libraries_dot_gradle_single_quote__index_is_13() {
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'libraries.gradle'", 13);

        /* test */
        assertNotNull(result);
        assertEquals("libraries.gradle", result.linkContent);
    }

    @Test
    public void link_content_is_libraries_dot_gradle__for__apply_from_single_quote_libraries_dot_gradle_single_quote__index_is_0() {
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'libraries.gradle'", 0);

        /* test */
        assertNotNull(result);
        assertEquals("libraries.gradle", result.linkContent);
    }

    @Test
    public void link_length_is_16__for__apply_from_single_quote_libraries_dot_gradle_single_quote__index_is_13() {
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'libraries.gradle'", 13);

        /* test */
        assertNotNull(result);
        assertEquals(16, result.linkLength);
    }

    @Test
    public void link_length_is_16__for__apply_from_single_quote_libraries_dot_gradle_single_quote__index_is_0() {
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'libraries.gradle'", 0);

        /* test */
        assertNotNull(result);
        assertEquals(16, result.linkLength);
    }

    @Test
    public void linkOffsetInLine_is_13__for__apply_from_single_quote_libraries_dot_gradle_single_quote__index_is_13() {
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'libraries.gradle'", 13);

        /* test */
        assertNotNull(result);
        assertEquals(13, result.linkOffsetInLine);
    }

    @Test
    public void linkOffsetInLine_is_13__for__apply_from_single_quote_libraries_dot_gradle_single_quote__index_is_0() {
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'libraries.gradle'", 0);

        /* test */
        assertNotNull(result);
        assertEquals(13, result.linkOffsetInLine);
    }

    @Test
    public void result_is_null_when_no_dot_gradle_insid_text() {
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'libraries.xradle'", 0);

        /* test */
        assertNull(result);
    }

    @Test
    public void result_is_null_when_gradle_inside_text_but_is_an_url() {
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'http://libraries.gradle'", 0);

        /* test */
        assertNull(result);
    }

    @Test
    public void result_is_null_when_gradle_inside_text_but_line_not_closed_with_a_quote() {
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'libraries.gradle", 0);

        /* test */
        assertNull(result);
    }

    @Test
    public void result_is_null_when_gradle_inside_text_but_next_char_is_not_a_quote() {
        GradleHyperLinkResult result = calcToTest.createFileLinkString("apply from: 'libraries.gradlex'", 0);

        /* test */
        assertNull(result);
    }

}
