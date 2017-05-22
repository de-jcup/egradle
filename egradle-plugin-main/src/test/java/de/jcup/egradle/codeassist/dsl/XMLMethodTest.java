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
import static org.mockito.Mockito.*;

import org.junit.Test;

public class XMLMethodTest {

	@Test
	public void compare_method_with_name1_is_lower_than_method_with_name2() {
		XMLMethod method1 = new XMLMethod();
		method1.name="name1";
		
		XMLMethod method2 = new XMLMethod();
		method2.name="name2";
		
		assertTrue(method1.compareTo(method2)<0);
	}
	
	@Test
	public void compare_method_with_name2_is_higher_than_method_with_name1() {
		XMLMethod method1 = new XMLMethod();
		method1.name="name1";
		
		XMLMethod method2 = new XMLMethod();
		method2.name="name2";
		
		assertTrue(method2.compareTo(method1)>0);
	}
	
	@Test
	public void compare_method_with_nameX_to_method_with_same_name_is_0() {
		XMLMethod method1 = new XMLMethod();
		method1.name="name";
		
		XMLMethod method2 = new XMLMethod();
		method2.name="name";
		
		assertTrue(method1.compareTo(method2)==0);
	}
	
	@Test
	public void compare_method_with_nameX_to_method_with_same_name_but_method1_has_parameters() {
		XMLMethod method1 = new XMLMethod();
		method1.name="nameX";
		Parameter param = mock(Parameter.class);
		when(param.getTypeAsString()).thenReturn("String");
		when(param.getName()).thenReturn("testParam");
		method1.parameters.add(param);
		
		XMLMethod method2 = new XMLMethod();
		method2.name="nameX";
		
		assertTrue(method1.compareTo(method2)>0);
	}
	
	@Test
	public void compare_method_with_nameX_to_method_with_same_name_and_parameters_are_same_type_but_different_names_is_0() {
		XMLMethod method1 = new XMLMethod();
		method1.name="nameX";
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("testParam1");
		method1.parameters.add(param1);
		
		XMLMethod method2 = new XMLMethod();
		method2.name="nameX";
		
		Parameter param2 = mock(Parameter.class);
		when(param2.getTypeAsString()).thenReturn("String");
		when(param2.getName()).thenReturn("testParam2");
		method2.parameters.add(param2);
		
		when(param1.compareTo(param2)).thenReturn(0);
		when(param2.compareTo(param1)).thenReturn(0);
		
		assertTrue(method1.compareTo(method2)==0);
	}
	
	
	/* ---------- */
	@Test
	public void equals_method_with_name1_to_method_with_name2__is_false() {
		XMLMethod method1 = new XMLMethod();
		method1.name="name1";
		
		XMLMethod method2 = new XMLMethod();
		method2.name="name2";
		
		assertFalse(method1.equals(method2));
	}
	
	@Test
	public void equals_method_with_name2_to_method_with_name1__is_false() {
		XMLMethod method1 = new XMLMethod();
		method1.name="name1";
		
		XMLMethod method2 = new XMLMethod();
		method2.name="name2";
		
		assertFalse(method1.equals(method2));
	}
	
	@Test
	public void equals_method_with_nameX_to_method_with_same_name_is_true__in_both_ways() {
		XMLMethod method1 = new XMLMethod();
		method1.name="name";
		
		XMLMethod method2 = new XMLMethod();
		method2.name="name";
		
		assertTrue(method1.equals(method2));
		assertTrue(method2.equals(method1));
	}
	
	@Test
	public void equals_method_with_nameX_to_method_with_same_name_but_method1_has_parameters() {
		XMLMethod method1 = new XMLMethod();
		method1.name="nameX";
		Parameter param = mock(Parameter.class);
		when(param.getTypeAsString()).thenReturn("String");
		when(param.getName()).thenReturn("testParam");
		method1.parameters.add(param);
		
		XMLMethod method2 = new XMLMethod();
		method2.name="nameX";
		
		assertFalse(method1.equals(method2));
		assertFalse(method2.equals(method1));
	}
	
	@Test
	public void equals_method_with_nameX_to_method_with_same_name_and_parameters_are_same_type_but_different_names_is_0() {
		XMLMethod method1 = new XMLMethod();
		method1.name="nameX";
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("testParam1");
		method1.parameters.add(param1);
		
		XMLMethod method2 = new XMLMethod();
		method2.name="nameX";
		
		Parameter param2 = param1; // equals true
		when(param2.getTypeAsString()).thenReturn("String");
		when(param2.getName()).thenReturn("testParam2");
		method2.parameters.add(param2);
		
		assertTrue(method1.equals(method2));
		assertTrue(method2.equals(method1));
	}

}
