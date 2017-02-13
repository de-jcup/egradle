package de.jcup.egradle.codeassist.dsl;

import static org.junit.Assert.*;

import org.junit.Test;

public class XMLPropertyTest {

	@Test
	public void compare_property_with_name1_is_lower_than_prop_with_name2() {
		XMLProperty prop1 = new XMLProperty();
		prop1.name="name1";
		
		XMLProperty prop2 = new XMLProperty();
		prop2.name="name2";
		
		assertTrue(prop1.compareTo(prop2)<0);
	}
	
	@Test
	public void compare_property_with_name2_is_higher_than_prop_with_name1() {
		XMLProperty prop1 = new XMLProperty();
		prop1.name="name1";
		
		XMLProperty prop2 = new XMLProperty();
		prop2.name="name2";
		
		assertTrue(prop2.compareTo(prop1)>0);
	}
	
	@Test
	public void compare_property_with_nameX_to_prop_with_same_name_is_0() {
		XMLProperty prop1 = new XMLProperty();
		prop1.name="nameX";
		
		XMLProperty prop2 = new XMLProperty();
		prop2.name="nameX";
		
		assertTrue(prop1.compareTo(prop2)==0);
	}
	
	/* ------------- */
	
	@Test
	public void equals_property_with_name1_to_prop_with_name2_is_false() {
		XMLProperty prop1 = new XMLProperty();
		prop1.name="name1";
		
		XMLProperty prop2 = new XMLProperty();
		prop2.name="name2";
		
		assertFalse(prop1.equals(prop2));
	}
	
	@Test
	public void equals_property_with_name2_to_prop_with_name1__is_false() {
		XMLProperty prop1 = new XMLProperty();
		prop1.name="name1";
		
		XMLProperty prop2 = new XMLProperty();
		prop2.name="name2";
		
		assertFalse(prop1.equals(prop2));
	}
	
	@Test
	public void equals_property_with_nameX_to_prop_with_same_name_is_true_both_ways() {
		XMLProperty prop1 = new XMLProperty();
		prop1.name="nameX";
		
		XMLProperty prop2 = new XMLProperty();
		prop2.name="nameX";
		
		assertTrue(prop1.equals(prop2));
		assertTrue(prop2.equals(prop1));
	}

}
