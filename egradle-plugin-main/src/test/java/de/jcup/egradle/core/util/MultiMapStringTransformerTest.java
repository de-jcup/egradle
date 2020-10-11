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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class MultiMapStringTransformerTest {

    private MultiMapStringTransformer transformerToTest;
    private List<Map<String, String>> mockedMaps;

    @Before
    public void before() {
        mockedMaps = new ArrayList<>();
    }

    @Test
    public void test_null_throws_no_nullpointer_and_returns_origin_text_on_transform() {
        /* prepare */
        transformerToTest = new MultiMapStringTransformer(null);

        /* test */
        assertEquals("test", transformerToTest.transform("test"));
    }

    @Test
    public void test_empty_throws_no_exception_and_returns_origin_text_on_transform() {
        /* prepare */
        transformerToTest = new MultiMapStringTransformer(mockedMaps);

        /* test */
        assertEquals("test", transformerToTest.transform("test"));
    }

    @Test
    public void test_not_empty_map_builds_one_simple_map_transformers_into_transformers_list() {
        /* prepare */
        Map<String, String> map = new HashMap<>();
        mockedMaps.add(map);

        transformerToTest = new MultiMapStringTransformer(mockedMaps);

        /* test */
        assertEquals(1, transformerToTest.transformers.size());
        assertEquals(SimpleMapStringTransformer.class, transformerToTest.transformers.iterator().next().getClass());
    }

    public void test_transformation_calls_transformers_in_transformers_list() {
        /* prepare */
        SimpleMapStringTransformer mockedTransformer = mock(SimpleMapStringTransformer.class);

        transformerToTest = new MultiMapStringTransformer(mockedMaps);
        transformerToTest.transformers.add(mockedTransformer);

        /* execute */
        transformerToTest.transform("test");

        /* test */
        verify(mockedTransformer.transform("test"));
    }

}
