/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.egradle.core.text;

import static de.jcup.egradle.core.text.AssertTextLines.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CommentBlockTogglerTest {

	private CommentBlockToggler togglerToTest;

	@Before
	public void before() {
		togglerToTest = new CommentBlockToggler();
	}

	@Test
	public void sameBlock__a_b_c__is_true() {
		List<TextLine> list = new ArrayList<>();
		list.add(new TextLine(-1, "a"));
		list.add(new TextLine(-1, "b"));
		list.add(new TextLine(-1, "c"));

		/* execute */
		assertTrue(togglerToTest.isSameCommentBlockStart(list));
	}

	@Test
	public void sameBlock__a_comment_b_c__is_false() {
		List<TextLine> list = new ArrayList<>();
		list.add(new TextLine(-1, "a"));
		list.add(new TextLine(-1, "//b"));
		list.add(new TextLine(-1, "c"));

		/* execute */
		assertFalse(togglerToTest.isSameCommentBlockStart(list));
	}

	/**
	 * <pre>
	 * 0123        =>    012345 
	 * //a\n             ////a\n
	 * 456               678910
	 * xy\n              //xy\n
	 * 78                11 12 13 14
	 * z\n               /  /  z  \n
	 * </pre>
	 */
	@Test
	public void comment_a_xy_z__converted_correct() {
		List<TextLine> list = new ArrayList<>();
		list.add(new TextLine(0, "//a\n"));
		list.add(new TextLine(4, "xy\n"));
		list.add(new TextLine(7, "z\n"));
		/* execute */
		List<TextLine> lines = togglerToTest.toggle(list);

		/* test */
		assertLines(lines).containsOnly(new TextLine(0, "////a\n"), new TextLine(6, "//xy\n"),
				new TextLine(11, "//z\n"));
	}

	/**
	 * <pre>
	 * 0123        =>    012345 
	 * abc\n             //abc\n
	 * 456               678910
	 * xy\n              //xy\n
	 * 78                11 12 13 14
	 * z\n               /  /  z  \n
	 * </pre>
	 */
	@Test
	public void abc_xy_z__converted_correct() {
		List<TextLine> list = new ArrayList<>();
		list.add(new TextLine(0, "abc\n"));
		list.add(new TextLine(4, "xy\n"));
		list.add(new TextLine(7, "z\n"));
		/* execute */
		List<TextLine> lines = togglerToTest.toggle(list);

		/* test */
		assertLines(lines).containsOnly(new TextLine(0, "//abc\n"), new TextLine(6, "//xy\n"),
				new TextLine(11, "//z\n"));
	}

	/**
	 * <pre>
	 * 012345           0123            
	 * //abc\n    =>    abc\n             
	 * 6789104          56               
	 * //xy\nx          y\n              
	 * 11 12 1          3 1478                
	 * /  /  z            \nz\n
	 * </pre>
	 */
	@Test
	public void commented_abc_xy_z__converted_correct() {
		List<TextLine> list = new ArrayList<>();
		list.add(new TextLine(0, "//abc\n"));
		list.add(new TextLine(6, "//xy\n"));
		list.add(new TextLine(11, "//z\n"));
		/* execute */
		List<TextLine> lines = togglerToTest.toggle(list);

		/* test */
		assertLines(lines).containsOnly(new TextLine(0, "abc\n"), new TextLine(4, "xy\n"), new TextLine(7, "z\n"));
	}

	@Test
	public void line1_and_line2__is_converted_to_comment_line1_and_comment_line2__and_offset_is_increased_by_2() {
		List<TextLine> list = new ArrayList<>();
		list.add(new TextLine(0, "line1"));
		list.add(new TextLine(20, "line2"));
		/* execute */
		List<TextLine> lines = togglerToTest.toggle(list);

		/* test */
		assertLines(lines).containsOnly(new TextLine(0, "//line1"), new TextLine(22, "//line2"));
	}

	@Test
	public void comment_line1_and_comment_line2__is_converted_to_line1_and_line2__and_offset_is_decreased_by_2() {
		List<TextLine> list = new ArrayList<>();
		list.add(new TextLine(0, "//line1"));
		list.add(new TextLine(22, "//line2"));
		/* execute */
		List<TextLine> lines = togglerToTest.toggle(list);

		/* test */
		assertLines(lines).containsOnly(new TextLine(0, "line1"), new TextLine(20, "line2"));
	}

	@Test
	public void a_line_is_converted_to_comment_a_line() {
		/* execute */
		List<TextLine> lines = togglerToTest.toggle(singletonList(new TextLine(0, "a line")));

		/* test */
		assertLines(lines).containsOnly(new TextLine(0, "//a line"));
	}

	@Test
	public void comment_a_line_is_converted_to_a_line() {

		/* execute */
		List<TextLine> lines = togglerToTest.toggle(singletonList(new TextLine(0, "//a line")));

		/* test */
		assertLines(lines).containsOnly(new TextLine(0, "a line"));
	}

}
