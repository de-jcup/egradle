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

import de.jcup.egradle.core.util.TextUtil;

public class TextUtilTest {

	@Test
	public void alpha123_index0__returns_alpha123() {
		String text="alpha123";
		assertEquals("alpha123", TextUtil.getLettersOrDigitsAt(0, text));
	}
	
	@Test
	public void alpha123_indexn1__returns_empty_string() {
		String text="alpha123";
		assertEquals("", TextUtil.getLettersOrDigitsAt(-1, text));
	}
	
	@Test
	public void alpha123_index100__returns_empty_string() {
		String text="alpha123";
		assertEquals("", TextUtil.getLettersOrDigitsAt(100, text));
	}
	
	@Test
	public void null_index0__returns_empty_string() {
		String text=null;
		assertEquals("", TextUtil.getLettersOrDigitsAt(0, text));
	}
	
	@Test
	public void null_index100__returns_empty_string() {
		String text=null;
		assertEquals("", TextUtil.getLettersOrDigitsAt(100, text));
	}
	
	@Test
	public void alpha123BacketXXBracket_index0__returns_alpha123() {
		String text="alpha123(xx)";
		assertEquals("alpha123", TextUtil.getLettersOrDigitsAt(0, text));
	}
	
	@Test
	public void alpha123dotabc_index0__returns_alpha123() {
		String text="alpha123.abc";
		assertEquals("alpha123", TextUtil.getLettersOrDigitsAt(0, text));
	}
}
