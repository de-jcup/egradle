package de.jcup.egradle.core.api;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.api.Filter;
import de.jcup.egradle.core.api.MultiFilter;

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
