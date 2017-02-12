package de.jcup.egradle.codeassist.dsl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Reason;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLType;

public class XMLTypeTest {

	private XMLType typeToTest;

	@Before
	public void before() {
		typeToTest = new XMLType();
	}
	
	@Test
	public void extendBySuperType_method1_from_super_type1_returned_in_getMethods(){
		/* prepare */
		Type superType = mock(Type.class);
		Method method1 = mock(Method.class);
		Set<Method> methodSet = new LinkedHashSet<>();
		methodSet.add(method1);
		when(superType.getMethods()).thenReturn(methodSet);

		/* check preconditions */
		assertTrue(typeToTest.getMethods().isEmpty());

		/* execute */
		typeToTest.inheritFrom(superType);

		/* test */
		assertFalse(typeToTest.getMethods().isEmpty());
		assertTrue(typeToTest.getMethods().contains(method1));
		
		Reason reason1 = typeToTest.getReasonFor(method1);
		assertNotNull(reason1);
		assertEquals(superType,reason1.getSuperType());
	}
	
	@Test
	public void extendBySuperType_property1_from_super_type1_returned_in_getProperties(){
		/* prepare */
		Type superType = mock(Type.class);
		Property property1 = mock(Property.class);
		Set<Property> methodSet = new LinkedHashSet<>();
		methodSet.add(property1);
		when(superType.getProperties()).thenReturn(methodSet);

		/* check preconditions */
		assertTrue(typeToTest.getMethods().isEmpty());

		/* execute */
		typeToTest.inheritFrom(superType);

		/* test */
		assertFalse(typeToTest.getProperties().isEmpty());
		assertTrue(typeToTest.getProperties().contains(property1));
		
		Reason reason1 = typeToTest.getReasonFor(property1);
		assertNotNull(reason1);
		assertEquals(superType,reason1.getSuperType());
	}
	
	@Test
	public void extendBySuperType_own_method0_and_method1_from_super_type1_returned_in_getMethods(){
		/* prepare */
		Method method0 = mock(Method.class);
		typeToTest.methods.add(method0);

		Type superType = mock(Type.class);
		Method method1 = mock(Method.class);
		Set<Method> superMethodSet = new LinkedHashSet<>();
		superMethodSet.add(method1);
		when(superType.getMethods()).thenReturn(superMethodSet);
		
		/* check preconditions */
		assertEquals(1, typeToTest.getMethods().size());

		/* execute */
		typeToTest.inheritFrom(superType);

		/* test */
		assertEquals(2, typeToTest.getMethods().size());
		assertTrue(typeToTest.getMethods().contains(method1));
		assertTrue(typeToTest.getMethods().contains(method0));
		
		Reason reason0 = typeToTest.getReasonFor(method0);
		assertNull(reason0);
		
		Reason reason1 = typeToTest.getReasonFor(method1);
		assertNotNull(reason1);
		assertEquals(superType,reason1.getSuperType());
	}
	
	@Test
	public void extendBySuperType_own_property0_and_property1_from_super_type1_returned_in_getProperties(){
		/* prepare */
		Property property0 = mock(Property.class);
		typeToTest.properties.add(property0);

		Type superType = mock(Type.class);
		Property proeprty1 = mock(Property.class);
		Set<Property> propertySet = new LinkedHashSet<>();
		propertySet.add(proeprty1);
		when(superType.getProperties()).thenReturn(propertySet);
		
		/* check preconditions */
		assertEquals(1, typeToTest.getProperties().size());

		/* execute */
		typeToTest.inheritFrom(superType);

		/* test */
		assertEquals(2, typeToTest.getProperties().size());
		assertTrue(typeToTest.getProperties().contains(proeprty1));
		assertTrue(typeToTest.getProperties().contains(property0));
		
		Reason reason0 = typeToTest.getReasonFor(property0);
		assertNull(reason0);
		
		Reason reason1 = typeToTest.getReasonFor(proeprty1);
		assertNotNull(reason1);
		assertEquals(superType,reason1.getSuperType());
	}

	@Test
	public void extendByNull_accepted_getmethods_returns_own_methods(){
		/* prepare */
		Type superType = mock(Type.class);
		Method method1 = mock(Method.class);
		Set<Method> methodSet = new LinkedHashSet<>();
		methodSet.add(method1);
		when(superType.getMethods()).thenReturn(methodSet);

		/* check preconditions */
		Set<Method> methods = typeToTest.getMethods();
		assertTrue(methods.isEmpty());

		/* execute */
		typeToTest.inheritFrom(superType);
		methods = typeToTest.getMethods();
		assertFalse(methods.isEmpty());
		
		/* now reset */
		typeToTest.inheritFrom(null);

		/* test */
		methods = typeToTest.getMethods();
		assertTrue(methods.isEmpty());
	}
	
	
	
	
	@Test
	public void addExtension_without_reson_adds_extension_without_reason() {
		/* prepare */
		Type extensionType = mock(Type.class);

		/* execute */
		typeToTest.addExtension("id1", extensionType, null);

		/* test */
		Type extensionTypeFound = typeToTest.getExtensions().get("id1");
		assertEquals(extensionType, extensionTypeFound);
		assertNull(typeToTest.getReasonForExtension("id1"));
	}

	@Test
	public void addExtension_with_reson_adds_extension_with_reason() {
		/* prepare */
		Type extensionType = mock(Type.class);
		Reason mockedReason = mock(Reason.class);
		
		/* execute */
		typeToTest.addExtension("id1", extensionType, mockedReason);

		/* test */
		Type extensionTypeFound = typeToTest.getExtensions().get("id1");
		assertEquals(extensionType, extensionTypeFound);
		assertEquals(mockedReason, typeToTest.getReasonForExtension("id1"));
	}

	@Test
	public void mixin_a_type_with_method1_reason_null_adds_method1_to_targettype_without_reason() {
		/* prepare */
		Type mixinType = mock(Type.class);
		Method method1 = mock(Method.class);
		Set<Method> methodSet = new LinkedHashSet<>();
		methodSet.add(method1);
		when(mixinType.getMethods()).thenReturn(methodSet);

		/* check preconditions */
		assertTrue(typeToTest.getMethods().isEmpty());

		/* execute */
		typeToTest.mixin(mixinType, null);

		/* test */
		assertFalse(typeToTest.getMethods().isEmpty());
		assertTrue(typeToTest.getMethods().contains(method1));
		assertNull(typeToTest.getReasonFor(method1));
	}

	@Test
	public void mixin_a_type_with_method1_reason1_adds_method1_to_targettype_with_reason1() {
		/* prepare */
		Type mixinType = mock(Type.class);
		Method method1 = mock(Method.class);
		Set<Method> methodSet = new LinkedHashSet<>();
		methodSet.add(method1);
		when(mixinType.getMethods()).thenReturn(methodSet);

		XMLType type = new XMLType();

		/* check preconditions */
		assertTrue(type.getMethods().isEmpty());
		Reason reason1 = mock(Reason.class);

		/* execute */
		type.mixin(mixinType, reason1);

		/* test */
		assertFalse(type.getMethods().isEmpty());
		assertTrue(type.getMethods().contains(method1));
		assertEquals(reason1, type.getReasonFor(method1));
	}

	@Test
	public void mixin_a_type_with_method1_and_method2_adds_method1_and_method2_to_targettype() {
		/* prepare */
		Type mixinType = mock(Type.class);
		Method method1 = mock(Method.class);
		Method method2 = mock(Method.class);
		Set<Method> methodSet = new LinkedHashSet<>();
		methodSet.add(method1);
		methodSet.add(method2);
		when(mixinType.getMethods()).thenReturn(methodSet);

		XMLType type = new XMLType();

		/* check preconditions */
		assertTrue(type.getMethods().isEmpty());

		/* execute */
		type.mixin(mixinType, null);

		/* test */
		assertFalse(type.getMethods().isEmpty());
		assertTrue(type.getMethods().contains(method1));
		assertTrue(type.getMethods().contains(method2));
	}

}
