/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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
 package de.jcup.egradle.codeassist.dsl;

import static org.junit.Assert.*;

import org.junit.Test;

public class XMLParameterTest {

	@Test
	public void compare_property_with_type1_is_lower_than_prop_with_type2() {
		XMLParameter param1 = new XMLParameter();
		param1.typeAsString="type1";
		
		XMLParameter param2 = new XMLParameter();
		param2.typeAsString="type2";
		
		assertTrue(param1.compareTo(param2)<0);
	}
	
	@Test
	public void compare_property_with_type2_is_higher_than_prop_with_type1() {
		XMLParameter param1 = new XMLParameter();
		param1.typeAsString="type1";
		
		XMLParameter param2 = new XMLParameter();
		param2.typeAsString="type2";
		
		assertTrue(param2.compareTo(param1)>0);
	}
	
	@Test
	public void compare_property_with_typeX_to_prop_with_same_type_is_0() {
		XMLParameter param1 = new XMLParameter();
		param1.typeAsString="typeX";
		
		XMLParameter param2 = new XMLParameter();
		param2.typeAsString="typeX";
		
		assertTrue(param1.compareTo(param2)==0);
	}
	
	/* ------------- */
	
	@Test
	public void equals_property_with_type1_to_prop_with_type2_is_false() {
		XMLParameter param1 = new XMLParameter();
		param1.typeAsString="type1";
		
		XMLParameter param2 = new XMLParameter();
		param2.typeAsString="type2";
		
		assertFalse(param1.equals(param2));
	}
	
	@Test
	public void equals_property_with_type2_to_prop_with_type1__is_false() {
		XMLParameter param1 = new XMLParameter();
		param1.typeAsString="type1";
		
		XMLParameter param2 = new XMLParameter();
		param2.typeAsString="type2";
		
		assertFalse(param1.equals(param2));
	}
	
	@Test
	public void equals_property_with_typeX_to_prop_with_same_type_is_true_both_ways() {
		XMLParameter param1 = new XMLParameter();
		param1.typeAsString="typeX";
		
		XMLParameter param2 = new XMLParameter();
		param2.typeAsString="typeX";
		
		assertTrue(param1.equals(param2));
		assertTrue(param2.equals(param1));
	}

}
