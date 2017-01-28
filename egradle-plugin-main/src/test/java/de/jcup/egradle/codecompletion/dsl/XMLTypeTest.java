package de.jcup.egradle.codecompletion.dsl;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.mockito.Mockito.*;

import java.util.LinkedHashSet;
import java.util.Set;

public class XMLTypeTest {
	
	@Test
	public void mixin_a_type_with_method1_adds_method1_to_targettype() {
		/* prepare */
		Type mixinType = mock(Type.class);
		Method method1 = mock(Method.class);
		Set<Method> methodSet = new LinkedHashSet<>();
		methodSet.add(method1);
		when(mixinType.getMethods()).thenReturn(methodSet);
		
		XMLType type = new XMLType();
		
		/* check preconditions*/
		assertTrue(type.getMethods().isEmpty());
		
		/* execute */
		type.mixin(mixinType);
		
		/* test */
		assertFalse(type.getMethods().isEmpty());
		assertTrue(type.getMethods().contains(method1));
	
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
		
		/* check preconditions*/
		assertTrue(type.getMethods().isEmpty());
		
		/* execute */
		type.mixin(mixinType);
		
		/* test */
		assertFalse(type.getMethods().isEmpty());
		assertTrue(type.getMethods().contains(method1));
		assertTrue(type.getMethods().contains(method2));
	}

}
