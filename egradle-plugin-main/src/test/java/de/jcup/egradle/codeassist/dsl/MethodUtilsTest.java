package de.jcup.egradle.codeassist.dsl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MethodUtilsTest {

	private Method method;
	private List<Parameter> parameters;

	@Before
	public void before() {
		method = mock(Method.class);
		parameters = new ArrayList<>();
		when(method.getParameters()).thenReturn(parameters);
	}

	@Test
	public void createSignature_method_myMethod_with_no_param__methodSignatureCorrect() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");

		/* execute + test */
		assertEquals("myMethod()", MethodUtils.createSignature(method));
	}

	@Test
	public void createSignature_method_myMethod_with_param_String_name_has_no_commata() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);

		/* execute + test */
		assertEquals("myMethod(String name)", MethodUtils.createSignature(method));
	}

	@Test
	public void createSignature_method_myMethod_with_param1_String_name_param2_Object_data() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);

		Parameter param2 = mock(Parameter.class);
		when(param2.getTypeAsString()).thenReturn("Object");
		when(param2.getName()).thenReturn("data");
		parameters.add(param2);

		/* execute + test */
		assertEquals("myMethod(String name, Object data)", MethodUtils.createSignature(method));
	}

	@Test
	public void createSignature_method_myMethod_with_param1_String_name_param2_Object_data_param3_int_size() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);

		Parameter param2 = mock(Parameter.class);
		when(param2.getTypeAsString()).thenReturn("Object");
		when(param2.getName()).thenReturn("data");
		parameters.add(param2);

		Parameter param3 = mock(Parameter.class);
		when(param3.getTypeAsString()).thenReturn("int");
		when(param3.getName()).thenReturn("size");
		parameters.add(param3);

		/* execute + test */
		assertEquals("myMethod(String name, Object data, int size)", MethodUtils.createSignature(method));
	}

	/* ----------------------- */

	@Test
	public void isMethodIdentified_method_myMethod_with_no_param() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");

		/* execute + test */
		assertTrue(MethodUtils.isMethodIdentified(method,"myMethod"));
	}
	
	@Test
	public void isMethodIdentified_method_myMethod_with_no_param__NOT_when_name_differs() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");

		/* execute + test */
		assertFalse(MethodUtils.isMethodIdentified(method,"otherMethod"));
	}

	@Test
	public void isMethodIdentified_method_myMethod_with_param_String_name_for_same_name_and_paramType_but_param_name_other() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);

		/* execute + test */
		assertTrue("myMethod(String name)", MethodUtils.isMethodIdentified(method, "myMethod", "String"));
	}
	
	@Test
	public void isMethodIdentified_method_myMethod_with_param_String_name_NOT_for_same_name_but_missing_param() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);

		/* execute + test */
		assertFalse("myMethod(String name)", MethodUtils.isMethodIdentified(method, "myMethod"));
	}
	
	@Test
	public void isMethodIdentified_method_myMethod_with_param_String_name_NOT_for_same_name_but_other_param_type() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);

		/* execute + test */
		assertFalse("myMethod(String name)", MethodUtils.isMethodIdentified(method, "myMethod", "Object"));
	}
	
	@Test
	public void isMethodIdentified_method_myMethod_with_param_Object_colon_name_is_accepted_for_Object() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("Object");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);

		/* execute + test */
		assertTrue("myMethod(String name)", MethodUtils.isMethodIdentified(method, "myMethod", "Object:text"));
	}

	@Test
	public void isMethodIdentified_method_myMethod_with_param1_String_name_param2_Object_data() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);

		Parameter param2 = mock(Parameter.class);
		when(param2.getTypeAsString()).thenReturn("Object");
		when(param2.getName()).thenReturn("data");
		parameters.add(param2);

		/* execute + test */
		assertTrue("myMethod(String name)", MethodUtils.isMethodIdentified(method, "myMethod", "String", "Object"));
	}

	@Test
	public void isMethodIdentified_method_myMethod_with_param1_String_name_param2_Object_data_param3_int_size() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);

		Parameter param2 = mock(Parameter.class);
		when(param2.getTypeAsString()).thenReturn("Object");
		when(param2.getName()).thenReturn("data");
		parameters.add(param2);

		Parameter param3 = mock(Parameter.class);
		when(param3.getTypeAsString()).thenReturn("int");
		when(param3.getName()).thenReturn("size");
		parameters.add(param3);

		/* execute + test */
		assertTrue("myMethod(String name)", MethodUtils.isMethodIdentified(method, "myMethod", "String", "Object","int"));
	}
	
	@Test
	public void isMethodIdentified_method_myMethod_with_param1_String_name_param2_Object_data_param3_int_size__NOT_when_arguments_other_ordered() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);

		Parameter param2 = mock(Parameter.class);
		when(param2.getTypeAsString()).thenReturn("Object");
		when(param2.getName()).thenReturn("data");
		parameters.add(param2);

		Parameter param3 = mock(Parameter.class);
		when(param3.getTypeAsString()).thenReturn("int");
		when(param3.getName()).thenReturn("size");
		parameters.add(param3);

		/* execute + test */
		assertFalse("myMethod(String name)", MethodUtils.isMethodIdentified(method, "myMethod", "String", "int","Object"));
		assertFalse("myMethod(String name)", MethodUtils.isMethodIdentified(method, "myMethod", "Object", "String", "Object"));
	}
	
	@Test
	public void isMethodIdentified_method_myMethod_with_param1_String_name_param2_Object_data_param3_int_size__NOT_when_another_argument_given() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);

		Parameter param2 = mock(Parameter.class);
		when(param2.getTypeAsString()).thenReturn("Object");
		when(param2.getName()).thenReturn("data");
		parameters.add(param2);

		Parameter param3 = mock(Parameter.class);
		when(param3.getTypeAsString()).thenReturn("int");
		when(param3.getName()).thenReturn("size");
		parameters.add(param3);

		/* execute + test */
		assertFalse("myMethod(String name)", MethodUtils.isMethodIdentified(method, "myMethod", "String", "Object", "int", "String"));
	}
	
	@Test
	public void hasClosureParam_method_no_arguments_at_all__returns_false(){
		assertFalse(MethodUtils.hasGroovyClosureAsParameter(method));
	}
	
	@Test
	public void hasClosureParam_method_argument_1_is_string__returns_false(){
		/* prepare */
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		/* execute + test */
		assertFalse(MethodUtils.hasGroovyClosureAsParameter(method));
	}
	
	@Test
	public void hasClosureParam_method_argument_1_is_closure__returns_true(){
		/* prepare */
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("groovy.lang.Closure");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		/* execute + test */
		assertTrue(MethodUtils.hasGroovyClosureAsParameter(method));
	}
	
	@Test
	public void hasClosureParam_method_argument_1_is_string_method_argument2_is_closure__returns_true(){
		/* prepare */
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		Parameter param2 = mock(Parameter.class);
		when(param2.getTypeAsString()).thenReturn("groovy.lang.Closure");
		when(param2.getName()).thenReturn("name");
		parameters.add(param2);
		
		/* execute + test */
		assertTrue(MethodUtils.hasGroovyClosureAsParameter(method));
	}
}
