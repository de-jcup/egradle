package de.jcup.egradle.core.api;

import static org.junit.Assert.*;

import org.junit.Test;

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
