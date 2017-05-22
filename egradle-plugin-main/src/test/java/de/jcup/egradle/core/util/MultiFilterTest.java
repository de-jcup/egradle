/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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

public class MultiFilterTest {

	private MultiFilter filterToTest;

	@Before
	public void before(){
		filterToTest = new MultiFilter();
	}
	
	@Test
	public void no_filter_added__returns_false_for_object() {
		assertFalse(filterToTest.isFiltered("test"));
	}
	
	@Test
	public void no_filter_added__returns_true_for_null() {
		assertTrue(filterToTest.isFiltered(null));
	}
	
	@Test
	public void one_filter_added__returns_false_for_object_when_added_filter_returns_false() {
		/* prepare */
		Filter otherFilter = mock(Filter.class);
		when(otherFilter.isFiltered(any())).thenReturn(false);

		filterToTest.add(otherFilter);
		
		/* execute + test */
		assertFalse(filterToTest.isFiltered("test"));
	}
	
	@Test
	public void one_filter_added__returns_true_for_object_when_added_filter_returns_true() {
		/* prepare */
		Filter otherFilter = mock(Filter.class);
		when(otherFilter.isFiltered(any())).thenReturn(true);

		filterToTest.add(otherFilter);
		
		/* execute + test */
		assertTrue(filterToTest.isFiltered("test"));
	}
	
	@Test
	public void one_filter_added__returns_true_for_null_even_when_other_filter_would_return_true() {
		/* prepare */
		Filter otherFilter = mock(Filter.class);
		when(otherFilter.isFiltered(any())).thenReturn(true);

		filterToTest.add(otherFilter);
		
		/* execute + test */
		assertTrue(filterToTest.isFiltered(null));
	}
}
