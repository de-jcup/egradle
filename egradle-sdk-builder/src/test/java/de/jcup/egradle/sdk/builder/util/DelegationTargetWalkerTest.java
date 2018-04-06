/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package de.jcup.egradle.sdk.builder.util;

import static java.util.Collections.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.ModifiableMethod;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeReference;

public class DelegationTargetWalkerTest {

	private DelegationTargetWalker walkerToTest;
	private Type type;
	private Type superClass;
	private Type superClassInterface1;
	private Type superClassInterface1exendedInterface0;
	private Type typeImplementedInterface2;
	private ModifiableMethod typeMethod;
	private ModifiableMethod superClassMethod;
	private ModifiableMethod superClassInterface1Method;
	private ModifiableMethod superClassInterface1exendedInterface0Method;
	private ModifiableMethod typeImplementedInterface2Method;

	@Test
	public void when_visitor_returns_always_null_walker_inspects_superclass_superclass_interface1_child_interface2() {
		/* prepare */
		DelegationTargetMethodVisitor visitor = mock(DelegationTargetMethodVisitor.class);
		when(visitor.getDelegationTargetAsString(any(Method.class))).thenReturn(null);

		/* execute */
		walkerToTest.visitAllMethodInHierarchy(type, visitor, null, false);

		/* test */
		verify(visitor).getDelegationTargetAsString(typeMethod);
		verify(visitor).getDelegationTargetAsString(superClassMethod);
		verify(visitor).getDelegationTargetAsString(superClassInterface1Method);
		verify(visitor).getDelegationTargetAsString(superClassInterface1exendedInterface0Method);
		verify(visitor).getDelegationTargetAsString(typeImplementedInterface2Method);

		verify(typeMethod, never()).setDelegationTargetAsString(any(String.class));
	}

	@Test
	public void when_visitor_for_typeImplementedInterface2Method_returns_value_value_is_returned_but_all_other_parts_are_visited() {
		/* prepare */
		DelegationTargetMethodVisitor visitor = mock(DelegationTargetMethodVisitor.class);
		when(visitor.getDelegationTargetAsString(any(Method.class))).thenReturn(null);
		when(visitor.getDelegationTargetAsString(typeImplementedInterface2Method)).thenReturn("done");

		/* execute */
		walkerToTest.visitAllMethodInHierarchy(type, visitor, null, false);

		/* test */
		verify(visitor).getDelegationTargetAsString(typeMethod);
		verify(visitor).getDelegationTargetAsString(superClassMethod);
		verify(visitor).getDelegationTargetAsString(superClassInterface1Method);
		verify(visitor).getDelegationTargetAsString(superClassInterface1exendedInterface0Method);
		verify(visitor).getDelegationTargetAsString(typeImplementedInterface2Method);

		verify(typeMethod).setDelegationTargetAsString("done");
	}

	@Test
	public void when_visitor_for_superclassMethod_returns_value_other_are_no_more_visited() {
		/* prepare */
		DelegationTargetMethodVisitor visitor = mock(DelegationTargetMethodVisitor.class);
		when(visitor.getDelegationTargetAsString(any(Method.class))).thenReturn(null);
		when(visitor.getDelegationTargetAsString(superClassMethod)).thenReturn("done");

		/* execute */
		walkerToTest.visitAllMethodInHierarchy(type, visitor, null, false);

		/* test */
		verify(visitor).getDelegationTargetAsString(typeMethod);
		verify(visitor).getDelegationTargetAsString(superClassMethod);
		verify(visitor, never()).getDelegationTargetAsString(superClassInterface1Method);
		verify(visitor, never()).getDelegationTargetAsString(superClassInterface1exendedInterface0Method);
		verify(visitor, never()).getDelegationTargetAsString(typeImplementedInterface2Method);

		verify(typeMethod).setDelegationTargetAsString("done");
	}

	@Before
	public void before() {
		walkerToTest = new DelegationTargetWalker();

		type = mock(Type.class, "type");
		superClass = mock(Type.class, "superclass");
		superClassInterface1 = mock(Type.class, "superClassInterface1");
		superClassInterface1exendedInterface0 = mock(Type.class, "superClassInterface1exendedInterface0");
		typeImplementedInterface2 = mock(Type.class, "typeImplementedInterface2");

		typeMethod = mock(ModifiableMethod.class, "typeMethod");
		superClassMethod = mock(ModifiableMethod.class, "superClassMethod");
		superClassInterface1Method = mock(ModifiableMethod.class, "superClassInterface1Method");
		superClassInterface1exendedInterface0Method = mock(ModifiableMethod.class,
				"superClassInterface1exendedInterface0Method");
		typeImplementedInterface2Method = mock(ModifiableMethod.class, "typeImplementedInterface2Method");

		when(type.getDefinedMethods()).thenReturn(singleton(typeMethod));
		when(superClass.getDefinedMethods()).thenReturn(singleton(superClassMethod));
		when(superClassInterface1.getDefinedMethods()).thenReturn(singleton(superClassInterface1Method));
		when(superClassInterface1exendedInterface0.getDefinedMethods())
				.thenReturn(singleton(superClassInterface1exendedInterface0Method));
		when(typeImplementedInterface2.getDefinedMethods()).thenReturn(singleton(typeImplementedInterface2Method));

		/* type extends superclass */
		when(type.getSuperType()).thenReturn(superClass);

		/* super class implements interface1 */
		TypeReference superClassInterface1Ref = mock(TypeReference.class);
		when(superClassInterface1Ref.getType()).thenReturn(superClassInterface1);
		when(superClass.getInterfaces()).thenReturn(singleton(superClassInterface1Ref));

		/* interface1 extends/implements interface0 */
		TypeReference interface1extendsInterface0Ref = mock(TypeReference.class);
		when(interface1extendsInterface0Ref.getType()).thenReturn(superClassInterface1exendedInterface0);
		when(superClassInterface1.getInterfaces()).thenReturn(singleton(interface1extendsInterface0Ref));

		/* type implements interface2 */
		TypeReference typeInterface2Ref = mock(TypeReference.class);
		when(typeInterface2Ref.getType()).thenReturn(typeImplementedInterface2);
		when(type.getInterfaces()).thenReturn(singleton(typeInterface2Ref));
	}
}
