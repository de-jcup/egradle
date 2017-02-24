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
	public void has_signature_method_with_methodName_no_params__methodName_null__true__with_shortening(){
		/* prepare */
		when(method.getName()).thenReturn("methodName");
		
		/* execute + test */
		assertTrue(MethodUtils.hasSignature(method, "methodName", null,true));
	}
	
	@Test
	public void has_signature_method_with_methodName_no_params__methodName_null__true(){
		/* prepare */
		when(method.getName()).thenReturn("methodName");
		
		/* execute + test */
		assertTrue(MethodUtils.hasSignature(method, "methodName", null,false));
	}
	
	@Test
	public void has_signature_method_with_methodName_no_params__otherMethodName_null__false(){
		/* prepare */
		when(method.getName()).thenReturn("methodName");
		
		/* execute + test */
		assertFalse(MethodUtils.hasSignature(method, "otherMethodName", null,false));
	}
	
	@Test
	public void has_signature_method_with_methodName_string_param__methodName_String__true(){
		/* prepare */
		when(method.getName()).thenReturn("methodName");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		/* execute + test */
		assertTrue(MethodUtils.hasSignature(method, "methodName", new String[]{"String"},false));
	}
	
	@Test
	public void has_signature_method_with_methodName_string_param__methodName_String__true__with_shortening(){
		/* prepare */
		when(method.getName()).thenReturn("methodName");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		/* execute + test */
		assertTrue(MethodUtils.hasSignature(method, "methodName", new String[]{"String"},true));
	}
	
	@Test
	public void has_signature_method_with_methodName_java_util_string_param__methodName_String__true__with_shortening(){
		/* prepare */
		when(method.getName()).thenReturn("methodName");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("java.util.String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		/* execute + test */
		assertTrue(MethodUtils.hasSignature(method, "methodName", new String[]{"String"},true));
	}
	
	@Test
	public void has_signature_method_with_methodName_java_util_string_param__methodName_String__false__without_shortening(){
		/* prepare */
		when(method.getName()).thenReturn("methodName");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("java.util.String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		/* execute + test */
		assertFalse(MethodUtils.hasSignature(method, "methodName", new String[]{"String"},false));
	}
	
	@Test
	public void has_signature_method_with_methodName_string_param__methodName_Integer__false(){
		/* prepare */
		when(method.getName()).thenReturn("methodName");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		/* execute + test */
		assertFalse(MethodUtils.hasSignature(method, "methodName", new String[]{"Integer"},false));
	}
	
	@Test
	public void has_signature_method_with_methodName_string_param__otherMethodName_String_false(){
		/* prepare */
		when(method.getName()).thenReturn("methodName");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		/* execute + test */
		assertFalse(MethodUtils.hasSignature(method, "otherMethodName", new String[]{"String"},false));
	}
	
	@Test
	public void has_signature_method_with_methodName_string_param__methodName_String_String_false(){
		/* prepare */
		when(method.getName()).thenReturn("methodName");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		/* execute + test */
		assertFalse(MethodUtils.hasSignature(method, "methodName", new String[]{"String","String"},false));
	}
	
	
	@Test
	public void has_signature_method_with_methodName_string_string_param__methodName_String_String_true(){
		/* prepare */
		when(method.getName()).thenReturn("methodName");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		Parameter param2 = mock(Parameter.class);
		when(param2.getTypeAsString()).thenReturn("String");
		when(param2.getName()).thenReturn("other");
		parameters.add(param2);
		
		/* execute + test */
		assertTrue(MethodUtils.hasSignature(method, "methodName", new String[]{"String","String"},false));
	}
	

	@Test
	public void has_signature_method_with_methodName_string_string_param__methodName_String_false(){
		/* prepare */
		when(method.getName()).thenReturn("methodName");
		Parameter param1 = mock(Parameter.class);
		when(param1.getTypeAsString()).thenReturn("String");
		when(param1.getName()).thenReturn("name");
		parameters.add(param1);
		
		Parameter param2 = mock(Parameter.class);
		when(param2.getTypeAsString()).thenReturn("String");
		when(param2.getName()).thenReturn("other");
		parameters.add(param2);
		
		/* execute + test */
		assertFalse(MethodUtils.hasSignature(method, "methodName", new String[]{"String"},false));
	}
	
	@Test
	public void has_same_signature__true__for_same() {
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
		
		Method method2b = mock(Method.class);
		List<Parameter> parameters2b = new ArrayList<>();
		when(method2b.getParameters()).thenReturn(parameters2b);
		
		when(method2b.getName()).thenReturn("myMethod");
		Parameter param1b = mock(Parameter.class);
		when(param1b.getTypeAsString()).thenReturn("String");
		when(param1b.getName()).thenReturn("name");
		parameters2b.add(param1b);

		Parameter param2b = mock(Parameter.class);
		when(param2b.getTypeAsString()).thenReturn("Object");
		when(param2b.getName()).thenReturn("data2b");
		parameters2b.add(param2b);

		Parameter param3b = mock(Parameter.class);
		when(param3b.getTypeAsString()).thenReturn("int");
		when(param3b.getName()).thenReturn("size");
		parameters2b.add(param3b);

		/* execute + test */
		assertEquals(true,MethodUtils.haveSameSignatures(method, method2b));
	}
	
	@Test
	public void has_same_signature__true__for_same__except_one_param_has_other_type() {
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
		
		Method method2b = mock(Method.class);
		List<Parameter> parameters2b = new ArrayList<>();
		when(method2b.getParameters()).thenReturn(parameters2b);
		
		when(method2b.getName()).thenReturn("myMethod");
		Parameter param1b = mock(Parameter.class);
		when(param1b.getTypeAsString()).thenReturn("String");
		when(param1b.getName()).thenReturn("name");
		parameters2b.add(param1b);

		Parameter param2b = mock(Parameter.class);
		when(param2b.getTypeAsString()).thenReturn("String"); // other type
		when(param2b.getName()).thenReturn("data");
		parameters2b.add(param2b);

		Parameter param3b = mock(Parameter.class);
		when(param3b.getTypeAsString()).thenReturn("int");
		when(param3b.getName()).thenReturn("size");
		parameters2b.add(param3b);

		/* execute + test */
		assertEquals(false,MethodUtils.haveSameSignatures(method, method2b));
	}
	
	@Test
	public void has_same_signature__false__for_same_expect_name() {
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
		
		Method method2b = mock(Method.class);
		List<Parameter> parameters2b = new ArrayList<>();
		when(method2b.getParameters()).thenReturn(parameters2b);
		
		when(method2b.getName()).thenReturn("myMethodNot");
		Parameter param1b = mock(Parameter.class);
		when(param1b.getTypeAsString()).thenReturn("String");
		when(param1b.getName()).thenReturn("name");
		parameters2b.add(param1b);

		Parameter param2b = mock(Parameter.class);
		when(param2b.getTypeAsString()).thenReturn("Object");
		when(param2b.getName()).thenReturn("data");
		parameters2b.add(param2b);

		Parameter param3b = mock(Parameter.class);
		when(param3b.getTypeAsString()).thenReturn("int");
		when(param3b.getName()).thenReturn("size");
		parameters2b.add(param3b);

		/* execute + test */
		assertEquals(false,MethodUtils.haveSameSignatures(method, method2b));
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
		assert100Percent(MethodUtils.calculateMethodIdentificationPercentage(method,"myMethod"));
	}
	
	@Test
	public void isMethodIdentified_method_myMethod_with_no_param__NOT_when_name_differs() {
		/* prepare */
		when(method.getName()).thenReturn("myMethod");

		/* execute + test */
		assertNoPercent(MethodUtils.calculateMethodIdentificationPercentage(method,"otherMethod"));
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
		assert100Percent(MethodUtils.calculateMethodIdentificationPercentage(method, "myMethod", "String"));
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
		assert50Percent(MethodUtils.calculateMethodIdentificationPercentage(method, "myMethod"));
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
		assert50Percent(MethodUtils.calculateMethodIdentificationPercentage(method, "myMethod", "Object"));
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
		assert100Percent(MethodUtils.calculateMethodIdentificationPercentage(method, "myMethod", "Object:text"));
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
		assert100Percent(MethodUtils.calculateMethodIdentificationPercentage(method, "myMethod", "String", "Object"));
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
		assert100Percent(MethodUtils.calculateMethodIdentificationPercentage(method, "myMethod", "String", "Object","int"));
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
		assertPercent(66, MethodUtils.calculateMethodIdentificationPercentage(method, "myMethod", "String", "int","Object"));
		assert50Percent(MethodUtils.calculateMethodIdentificationPercentage(method, "myMethod", "Object", "String", "Object"));
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
		assert50Percent(MethodUtils.calculateMethodIdentificationPercentage(method, "myMethod", "String", "Object", "int", "String"));
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
	
	private void assertPercent(int percent,int calculatedPercentage) {
		assertEquals("Percentage differs", percent, calculatedPercentage);
	}

	private void assert50Percent(int calculatedPercentage) {
		assertPercent(50, calculatedPercentage);
	}
	
	private void assert100Percent(int calculatedPercentage) {
		assertPercent(100, calculatedPercentage);
	}

	private void assertNoPercent(int calculatedPercentage) {
		assertPercent(0, calculatedPercentage);
	}
}
