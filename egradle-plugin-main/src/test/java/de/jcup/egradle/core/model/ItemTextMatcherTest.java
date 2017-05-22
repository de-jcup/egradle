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
 package de.jcup.egradle.core.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ItemTextMatcherTest {

	private ItemTextMatcher matcherToTest;
	private Item item;

	@Before
	public void before() {
		matcherToTest = new ItemTextMatcher();
		item = new Item();
	}
	
	@Test
	public void a_method_getName_with_no_return_type_is_found_by_simple_get_asterisk__when_item_has_type(){
		item.setName("getName");
		item.setType(null); // not necessary, only to explain
		matcherToTest.setFilterText("get*");

		/* execute + test */
		assertTrue(matcherToTest.matches(item));	
	}

	@Test
	public void a_method_getName_with_return_type_String_is_found_by_simple_get_asterisk__when_item_has_type(){
		item.setName("getName");
		item.setType("String");
		matcherToTest.setFilterText("get*");
		

		/* execute + test */
		assertTrue(matcherToTest.matches(item));	
	}
	
	@Test
	public void item_with_name_abc_is_matched_by_unset_filter_text() {
		/* prepare */
		item.setName("abc");

		/* execute + test */
		assertTrue(matcherToTest.matches(item));
	}

	@Test
	public void item_with_name_abc_is_matched_by_filter_text_null() {
		/* prepare */
		item.setName("abc");
		matcherToTest.setFilterText(null);

		/* execute + test */
		assertTrue(matcherToTest.matches(item));
	}

	@Test
	public void item_with_name_abc_is_matched_by_filter_text_with_two_spaces() {
		/* prepare */
		item.setName("abc");
		matcherToTest.setFilterText("  ");

		/* execute + test */
		assertTrue(matcherToTest.matches(item));
	}

	@Test
	public void item_with_name_abc_is_matched_by_a() {
		/* prepare */
		item.setName("abc");
		matcherToTest.setFilterText("a");

		/* execute + test */
		assertTrue(matcherToTest.matches(item));
	}

	@Test
	public void item_with_name_abc_is_not_matched_by_x() {
		/* prepare */
		item.setName("abc");
		matcherToTest.setFilterText("x");

		/* execute + test */
		assertFalse(matcherToTest.matches(item));
	}
	
	@Test
	public void item_with_name_abc_is_not_matched_by_b() {
		/* prepare */
		item.setName("abc");
		matcherToTest.setFilterText("b");

		/* execute + test */
		assertFalse(matcherToTest.matches(item));
	}
	
	@Test
	public void item_with_name_abc_is_matched_by_asterisk_and_b() {
		/* prepare */
		item.setName("abc");
		matcherToTest.setFilterText("*b");

		/* execute + test */
		assertTrue(matcherToTest.matches(item));
	}

	@Test
	public void item_with_name_having_brackets_and_doDump_is_matched_by_asterisk_and_do() {
		/* prepare */
		item.setName("jacocoRemoteAction(doDump)");
		matcherToTest.setFilterText("*do");

		/* execute + test */
		assertTrue(matcherToTest.matches(item));
	}

	@Test
	public void item_with_name_having_brackets_and_doDump_doReset_doAppend_is_matched_by_asterisk_and_do() {
		/* prepare */
		item.setName("jacocoRemoteAction(doDump, doReset, doAppend)");
		matcherToTest.setFilterText("*do");

		/* execute + test */
		assertTrue(matcherToTest.matches(item));
	}
	
	@Test
	public void item_with_parameters_doDump_doReset_doAppend_is_matched_by_asterisk_and_do() {
		/* prepare */
		item.setParameters("doDump, doReset, doAppend");
		matcherToTest.setFilterText("*do");

		/* execute + test */
		assertTrue(matcherToTest.matches(item));
	}
	
	@Test
	public void item_with_name_having_brackets_and_doDump_doReset_doAppend_is_matched_by_asterisk_and_opening_bracket() {
		/* prepare */
		item.setName("jacocoRemoteAction(doDump, doReset, doAppend)");
		matcherToTest.setFilterText("*(");

		/* execute + test */
		assertTrue(matcherToTest.matches(item));
	}
	
	@Test
	public void item_with_configuration_testCompile_is_matched_by_asterisk_and_test() {
		/* prepare */
		item.setConfiguration("testCompile");
		matcherToTest.setFilterText("*test");

		/* execute + test */
		assertTrue(matcherToTest.matches(item));
	}
	
	@Test
	public void item_with_info_someThing_is_matched_by_asterisk_and_thing() {
		/* prepare */
		item.setInfo("someThing");
		matcherToTest.setFilterText("*thing");

		/* execute + test */
		assertTrue(matcherToTest.matches(item));
	}
}
