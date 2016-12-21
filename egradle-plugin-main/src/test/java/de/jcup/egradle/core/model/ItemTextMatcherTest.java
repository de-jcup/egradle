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

}
