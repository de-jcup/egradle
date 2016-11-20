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
 package de.jcup.egradle.core.token.parser;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ParseContextTest {

	private ParseContext contextToTest;

	@Before
	public void before() {
		contextToTest = new ParseContext();
	}
	
	@Test
	public void appendAllRemainingTextOfLineAndIncPos__text_with_length_10_pos_is_1_after_execution_pos_is_10() {
		/* prepare */
		String text = "1234567890";
		contextToTest.setLineChars(text.toCharArray());
		contextToTest.incPosAndOffset();
		assertEquals(1,contextToTest.getPos());

		/* execute */
		contextToTest.appendAllRemainingTextOfLineAndIncPos();
		
		/* test */
		String currentText = contextToTest.getCurrentTextString();
		assertEquals("Position must be icnremented to last position of text", 10,contextToTest.getPos());
		assertEquals("234567890",currentText);
	}
	
	@Test
	public void appendAllRemainingTextOfLineAndIncPos__text_with_length_10_pos_is_1_returns_current_text_for_pos2_to_10() {
		/* prepare */
		String text = "1234567890";
		contextToTest.setLineChars(text.toCharArray());
		contextToTest.incPosAndOffset();
		assertEquals(1,contextToTest.getPos());
		
		/* execute */
		contextToTest.appendAllRemainingTextOfLineAndIncPos();
		
		/* test */
		String currentText = contextToTest.getCurrentTextString();
		assertEquals("Position must be icnremented to last position of text", 10,contextToTest.getPos());
		assertEquals("234567890",currentText);
	}
	
	@Test
	public void appendAllRemainingTextOfLineAndIncPos__text_with_length_10_pos_is_0_returns_current_text_for_pos1_to_10() {
		/* prepare */
		String text = "1234567890";
		contextToTest.setLineChars(text.toCharArray());
		assertEquals(0,contextToTest.getPos());

		/* execute */
		contextToTest.appendAllRemainingTextOfLineAndIncPos();
		
		/* test */
		String currentText = contextToTest.getCurrentTextString();
		assertEquals("1234567890",currentText);
	}
	
	@Test
	public void a_text_with_single_quote_contains_singlequote_in_current_text() {
		/* prepare */
		String text = "\"'test\"";

		contextToTest.setLineChars(text.toCharArray());
		contextToTest.appendAllRemainingTextOfLineAndIncPos();

		/* execute */
		String currentText = contextToTest.getCurrentTextString();
		
		/* test */
		assertEquals("\"'test\"",currentText);
	}
	
	@Test
	public void text_currentPos_at_2__appendAllRemainingTextOfLineAndIncPos__remains_on_last_position() {
		/* prepare */
		String text = "12345";
		int expectedOffsetAfterAllAppended = text.length();
		
		contextToTest.setLineChars(text.toCharArray());
		contextToTest.incPosAndOffset();
		contextToTest.incPosAndOffset();
		
		assertEquals(2, contextToTest.getOffset());

		/* execute */
		contextToTest.appendAllRemainingTextOfLineAndIncPos();

		/* test */
		assertEquals(expectedOffsetAfterAllAppended, contextToTest.getOffset());
	}

}
