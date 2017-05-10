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

import org.junit.Test;

import de.jcup.egradle.core.util.History;

public class HistoryTest {

	@Test
	public void one_entry_in_history_1_is_empty_returns_false() {
		/* prepare */
		History<String> historyToTest = new History<>(1);
		historyToTest.add("content");
		
		/* execute + test */
		assertFalse(historyToTest.isEmpty());
	}

	@Test
	public void nothing_in_history_1_is_empty_returns_true() {
		/* prepare */
		History<String> historyToTest = new History<>(1);
		
		/* execute + test */
		assertTrue(historyToTest.isEmpty());
	}
	
	@Test
	public void nothing_in_history_1_current_returns_null() {
		/* prepare */
		History<String> historyToTest = new History<>(1);
		
		/* execute + test */
		assertEquals(null, historyToTest.current());
	}
	
	@Test
	public void one_entry_in_history_1_getCount_returns_1() {
		/* prepare */
		History<String> historyToTest = new History<>(1);
		historyToTest.add("content");
		
		/* execute + test */
		assertEquals(1, historyToTest.getCount());
	}
	
	@Test
	public void one_entry_in_history_1_current_returns_value_size_keeps_1() {
		/* prepare */
		History<String> historyToTest = new History<>(1);
		historyToTest.add("content");
		
		/* execute + test */
		assertEquals(1, historyToTest.getCount());
		assertEquals("content", historyToTest.current());
		assertEquals(1, historyToTest.getCount());
	}
	
	@Test
	public void nothing_in_history_1_getCount_returns_0() {
		/* prepare */
		History<String> historyToTest = new History<>(1);
		
		/* execute + test */
		assertEquals(0, historyToTest.getCount());
	}
	
	@Test
	public void nothing_in_history_1_max_returns_1() {
		/* prepare */
		History<String> historyToTest = new History<>(1);
		
		/* execute + test */
		assertEquals(1, historyToTest.getMax());
	}
	
	@Test
	public void nothing_in_history_0_go_back_returns_null() {
		/* prepare */
		History<String> historyToTest = new History<>(0);
		
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
