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
 package de.jcup.egradle.core.token.filter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.core.token.Token;
public class MultiTokenFilterTest {
	private MultiTokenFilter filterToTest;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void before() {
		filterToTest= new MultiTokenFilter();
	}
	
	@Test
	public void add_null_as_filter_throws_illegal_argument(){
		expectedException.expect(IllegalArgumentException.class);
		
		filterToTest.add(null);
	}
	
	@Test
	public void no_filters_added_null_is_not_filtered() {
		assertFalse(filterToTest.isFiltered(null));
	}
	
	@Test
	public void no_filters_added_new_token_is_not_filtered() {
		assertFalse(filterToTest.isFiltered(mock(Token.class)));
	}
	
	@Test
	public void filter_added__added_filter_filters_null__returns__true_for_null() {
		/* prepare */
		TokenFilter filter = mock(TokenFilter.class);
		when(filter.isFiltered(null)).thenReturn(true);
		filterToTest.add(filter);
		
		/* execute + test */
		assertTrue(filterToTest.isFiltered(null));
	}
	
	@Test
	public void filter_added__added_filter_filters_NOT_null__returns__false_for_null() {
		/* prepare */
		TokenFilter filter = mock(TokenFilter.class);
		when(filter.isFiltered(null)).thenReturn(false);
		filterToTest.add(filter);
		
		/* execute + test */
		assertFalse(filterToTest.isFiltered(null));
	}

}
