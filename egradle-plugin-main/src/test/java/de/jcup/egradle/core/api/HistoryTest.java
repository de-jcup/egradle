package de.jcup.egradle.core.api;

import static org.junit.Assert.*;

import org.junit.Test;

public class HistoryTest {

	@Test
	public void nothing_in_history_0_go_back_returns_null() {
		/* prepare */
		History<String> historyToTest = new History<>(1);
		
		/* execute + test */
		assertEquals(null, historyToTest.goBack());
	}
	
	@Test
	public void add_test1_to_history_0_go_back_returns_null() {
		/* prepare */
		History<String> historyToTest = new History<>(0);
		historyToTest.add("test1");
		
		/* execute + test */
		assertEquals(null, historyToTest.goBack());
	}
	
	@Test
	public void nothing_in_history_1_go_back_returns_null() {
		/* prepare */
		History<String> historyToTest = new History<>(1);
		
		/* execute + test */
		assertEquals(null, historyToTest.goBack());
	}
	
	@Test
	public void add_test1_in_history1_go_back_returns_test1_another_go_back_returns_null() {
		/* prepare */
		History<String> historyToTest = new History<>(1);
		historyToTest.add("test1");
		
		/* execute + test */
		assertEquals("test1", historyToTest.goBack());
		assertEquals(null, historyToTest.goBack());
	}
	
	@Test
	public void add_test1_add_null_in_history2_go_back_returns_test1_another_go_back_returns_null() {
		/* prepare */
		History<String> historyToTest = new History<>(1);
		historyToTest.add("test1");
		historyToTest.add(null);
		
		/* execute + test */
		assertEquals("test1", historyToTest.goBack());
		assertEquals(null, historyToTest.goBack());
	}
	
	@Test
	public void add_test1_and_test2_in_history1_go_back_returns_test2_another_go_back_returns_null() {
		/* prepare */
		History<String> historyToTest = new History<>(1);
		historyToTest.add("test1");
		historyToTest.add("test2");
		
		/* execute + test */
		assertEquals("test2", historyToTest.goBack());
		assertEquals(null, historyToTest.goBack());
	}
	
	@Test
	public void add_test1_and_test2_in_history2_go_back_returns_test2_another_go_back_returns_test1_another_one_null() {
		/* prepare */
		History<String> historyToTest = new History<>(2);
		historyToTest.add("test1");
		historyToTest.add("test2");
		
		/* execute + test */
		assertEquals("test2", historyToTest.goBack());
		assertEquals("test1", historyToTest.goBack());
		assertEquals(null, historyToTest.goBack());
	}
	
	@Test
	public void add_test1_and_test2_in_history2_clear_go_back_returns_null() {
		/* prepare */
		History<String> historyToTest = new History<>(2);
		historyToTest.add("test1");
		historyToTest.add("test2");
		historyToTest.clear();
		/* execute + test */
		assertEquals(null, historyToTest.goBack());
	}
}
