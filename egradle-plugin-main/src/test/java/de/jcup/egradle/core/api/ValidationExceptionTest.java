package de.jcup.egradle.core.api;

import static org.junit.Assert.*;

import org.junit.Test;

public class ValidationExceptionTest {

	@Test
	public void message1_equals_message1() {
		assertTrue(new ValidationException("message1").equals(new ValidationException("message1")));
	}
	
	@Test
	public void null_equals_null() {
		assertTrue(new ValidationException(null).equals(new ValidationException(null)));
	}
	
	@Test
	public void null_equals_NOT_message1() {
		assertFalse(new ValidationException(null).equals(new ValidationException("message1")));
	}
	
	@Test
	public void message1_equals_NOT_null() {
		assertFalse(new ValidationException(null).equals(new ValidationException("message1")));
	}
	
	@Test
	public void message1_equals_NOT_message2() {
		assertFalse(new ValidationException("message1").equals(new ValidationException("message2")));
	}
	
	@Test
	public void message2_equals_NOT_message1() {
		assertFalse(new ValidationException("message2").equals(new ValidationException("message1")));
	}

	@Test
	public void hashCodes_equals_for_message1_and_message1() {
		assertEquals(new ValidationException("message1").hashCode(),new ValidationException("message1").hashCode());
	}
	@Test
	public void hashCodes_equals_for_message2_and_message2() {
		assertEquals(new ValidationException("message2").hashCode(),new ValidationException("message2").hashCode());
	}
	
	@Test
	public void hashCodes_are_NOTt_equal_for_message1_and_message2() {
		assertFalse(new ValidationException("message1").hashCode()==new ValidationException("message2").hashCode());
	}
	
	@Test
	public void hashCodes_equals_for_null_and_null() {
		assertEquals(new ValidationException(null).hashCode(),new ValidationException(null).hashCode());
	}
	@Test
	public void hashCodes_NOT_equal_for_null_and_message1() {
		assertFalse(new ValidationException(null).hashCode() == new ValidationException("message1").hashCode());
	}
	
	@Test
	public void message1_detail1__equals__message1_detail1(){
		assertTrue(new ValidationException("message1","detail1").equals(new ValidationException("message1","detail1")));
	}
	
	public void message1_detail1__equalsNOT__message1_detail2(){
		assertFalse(new ValidationException("message1","detail1").equals(new ValidationException("message1","detail2")));
	}
}
