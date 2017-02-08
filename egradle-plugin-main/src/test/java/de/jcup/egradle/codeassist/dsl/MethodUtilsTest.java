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
	public void method_myMethod_with_no_param__methodSignatureCorrect() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		
		/* execute + test */
		assertEquals("myMethod()",MethodUtils.createSignature(method));
	}
	
	@Test
	public void method_myMethod_with_param_String_name_has_no_commata() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		/* execute + test */
		assertEquals("myMethod(String name)",MethodUtils.createSignature(method));
	}
	
	@Test
	public void method_myMethod_with_param1_String_name_param2_Object_data() {
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
		assertEquals("myMethod(String name, Object data)",MethodUtils.createSignature(method));
	}
	
	@Test
	public void method_myMethod_with_param1_String_name_param2_Object_data_param3_int_size() {
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
		assertEquals("myMethod(String name, Object data, int size)",MethodUtils.createSignature(method));
	}

}
