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
		assertNull(typeToTest.getReasonForMethod(method1));
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
		assertEquals(reason1, type.getReasonForMethod(method1));
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
