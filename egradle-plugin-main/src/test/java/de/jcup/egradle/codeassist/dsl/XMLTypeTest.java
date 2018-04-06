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
package de.jcup.egradle.codeassist.dsl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class XMLTypeTest {

	private XMLType typeToTest;

	@Before
	public void before() {
		typeToTest = new XMLType();
	}

	@Test
	public void type_b_inherits_type_a__after_mixin_is_done__getmethods_contains_mixin_method() {
		/* prepare */
		Type superType = mock(Type.class);
		Method method1 = mock(Method.class, "method1");
		Set<Method> methodSet = new LinkedHashSet<>();
		methodSet.add(method1);
		when(superType.getMethods()).thenReturn(methodSet);

		Type mixinType = mock(Type.class);
		Method method2 = mock(Method.class, "method2");
		Set<Method> methodSet2 = new LinkedHashSet<>();
		methodSet2.add(method2);
		when(mixinType.getMethods()).thenReturn(methodSet2);

		/* mockito workaround for tree set */
		when(method1.compareTo(method2)).thenReturn(1);
		when(method2.compareTo(method1)).thenReturn(-1);

		/* check preconditions */
		assertTrue(typeToTest.getMethods().isEmpty());

		/* execute */
		typeToTest.mixin(mixinType, null);
		typeToTest.extendFrom(superType);

		/* test */
		assertFalse(typeToTest.getMethods().isEmpty());
		assertTrue(typeToTest.getMethods().contains(method1));
		assertTrue(typeToTest.getMethods().contains(method2));
	}

	@Test
	public void type_b_inherits_type_a__before_mixin_is_done__getmethods_contains_mixin_method() {
		/* prepare */
		Type superType = mock(Type.class);
		Method method1 = mock(Method.class, "method1");
		Set<Method> methodSet = new LinkedHashSet<>();
		methodSet.add(method1);
		when(superType.getMethods()).thenReturn(methodSet);

		Type mixinType = mock(Type.class);
		Method method2 = mock(Method.class, "method2");
		Set<Method> methodSet2 = new LinkedHashSet<>();
		methodSet2.add(method2);
		when(mixinType.getMethods()).thenReturn(methodSet2);

		/* mockito workaround for tree set */
		when(method1.compareTo(method2)).thenReturn(1);
		when(method2.compareTo(method1)).thenReturn(-1);

		/* check preconditions */
		assertTrue(typeToTest.getMethods().isEmpty());

		/* execute */
		typeToTest.extendFrom(superType);
		typeToTest.mixin(mixinType, null);

		/* test */
		assertFalse(typeToTest.getMethods().isEmpty());
		assertTrue(typeToTest.getMethods().contains(method1));
		assertTrue(typeToTest.getMethods().contains(method2));
	}

	@Test
	public void is_interface_of_one_of_its_interfaces_returns_true() {
		/* prepare */
		Type interface2 = mock(Type.class, "interface2");
		when(interface2.isInterface()).thenReturn(true);
		when(interface2.getName()).thenReturn("de.jcup.test.Interface2");

		Type interface3 = mock(Type.class, "interface3");
		when(interface3.isInterface()).thenReturn(true);
		when(interface3.getName()).thenReturn("de.jcup.test.Interface3");

		/* execute */
		typeToTest.extendFrom(interface2);
		typeToTest.extendFrom(interface3);

		/* test */
		assertTrue(typeToTest.isImplementingInterface("de.jcup.test.Interface2"));
		assertTrue(typeToTest.isImplementingInterface("de.jcup.test.Interface3"));
		assertFalse(typeToTest.isImplementingInterface("de.jcup.test.somewhere.else.Interface2"));
	}

	@Test
	public void is_interface_of_one_of_its_interfaces_which_is_in_deper_hierarchy__returns_true() {
		/* prepare */
		Type superType1 = mock(Type.class);
		Type interfaceType = mock(Type.class);
		when(interfaceType.getName()).thenReturn("interfacename");

		TypeReference interfaceTypeRef = mock(TypeReference.class);
		when(interfaceTypeRef.getType()).thenReturn(interfaceType);

		when(superType1.getInterfaces()).thenReturn(Collections.singleton(interfaceTypeRef));

		/* execute */
		typeToTest.extendFrom(superType1);

		/* test */
		assertTrue(typeToTest.isImplementingInterface("interfacename"));
	}

	@Test
	public void extend_from_interface__merges_methods() {
		/* prepare */
		Type interface2 = mock(Type.class, "interface2");
		Type interface3 = mock(Type.class, "interface3");

		/* interface2 with method2 */
		Set<Method> methods2 = new LinkedHashSet<>();
		Method method2 = mock(Method.class, "method2");
		methods2.add(method2);

		when(interface2.getMethods()).thenReturn(methods2);

		/* interface3 with method3 */
		Set<Method> methods3 = new LinkedHashSet<>();
		Method method3 = mock(Method.class, "method3");
		methods3.add(method3);

		when(interface3.getMethods()).thenReturn(methods3);
		/*
		 * because using tree the compare method must be overriden, otherwise
		 * only one method will be in tree set because tree set uses compareTo
		 */
		when(method2.compareTo(method3)).thenReturn(1);
		when(method3.compareTo(method2)).thenReturn(-1);

		/* execute */
		typeToTest.extendFrom(interface2);
		typeToTest.extendFrom(interface3);

		/* test */
		Set<Method> methods = typeToTest.getMethods();
		assertEquals(2, methods.size());

	}

	@Test
	public void type1_extends_type2__which_has_no_interfaces_so_type1_has_no_interfaces() {
		/* prepare */
		Type superType1 = mock(Type.class);

		when(superType1.getInterfaces()).thenReturn(Collections.emptySet());

		/* execute */
		typeToTest.extendFrom(superType1);

		/* test */
		assertTrue(typeToTest.getInterfaces().isEmpty());
	}

	@Test
	public void type1_has_interface_type_3_and_extends_type2__which_has_no_interfaces_so_type1_has_only_interface_type3() {
		/* prepare */
		Type superType1 = mock(Type.class);
		Type superType3 = mock(Type.class);
		TypeReference ref = mock(TypeReference.class);

		when(superType1.getInterfaces()).thenReturn(Collections.emptySet());

		typeToTest.interfaces.add(ref);

		/* execute */
		typeToTest.extendFrom(superType1);

		/* test */
		assertTrue(typeToTest.getInterfaces().contains(superType3));
	}

	@Test
	public void type1_extends_type2__which_has_interface_type3_so_type1_has_now_also_type3_as_interface() {
		/* prepare */
		Type superType1 = mock(Type.class);
		Type superType3 = mock(Type.class);

		TypeReference referenceType3 = mock(TypeReference.class);
		when(referenceType3.getType()).thenReturn(superType3);

		when(superType1.getInterfaces()).thenReturn(Collections.singleton(referenceType3));

		/* execute */
		typeToTest.extendFrom(superType1);

		/* test */
		assertTrue(typeToTest.getInterfaces().contains(superType3));
	}

	@Test
	public void type1_extends_type2__which_has_interface_type3_and_interface_type3_extends_interface_type4_and_5_so_type1_has_now_also_all_three_interfaces() {
		/* prepare */
		Type superType1 = mock(Type.class);
		Type superType3 = mock(Type.class);
		Type superType4 = mock(Type.class);
		Type superType5 = mock(Type.class);

		TypeReference referenceType3 = mock(TypeReference.class);
		when(referenceType3.getType()).thenReturn(superType3);

		TypeReference referenceType4 = mock(TypeReference.class);
		when(referenceType4.getType()).thenReturn(superType4);

		TypeReference referenceType5 = mock(TypeReference.class);
		when(referenceType5.getType()).thenReturn(superType5);

		when(superType1.getInterfaces()).thenReturn(Collections.singleton(referenceType3));
		when(superType3.getInterfaces()).thenReturn(new HashSet<>(Arrays.asList(referenceType4, referenceType5)));

		/* execute */
		typeToTest.extendFrom(superType1);

		/* test */
		Set<TypeReference> interfaces = typeToTest.getInterfaces();
		assertTrue(interfaces.contains(referenceType3));
		assertTrue(interfaces.contains(referenceType4));
		assertTrue(interfaces.contains(referenceType5));
	}

	@Test
	public void a_type_which_is_an_interface_returns_also_its_superinterface_when_get_interfaces_is_called() {
		/* prepare */
		Type superType1 = mock(Type.class);
		when(superType1.getName()).thenReturn("test.Super1");
		when(superType1.isInterface()).thenReturn(true);

		/* execute */
		typeToTest.isInterface = true;
		typeToTest.extendFrom(superType1);

		/* test */
		Set<TypeReference> interfaces = typeToTest.getInterfaces();
		assertNotNull(interfaces);
		assertEquals(1, interfaces.size());

		TypeReference ref = interfaces.iterator().next();
		assertEquals("test.Super1", ref.getTypeAsString());

	}

	@Test
	public void type1_extends_type2_so_type1_is_descendant_of_type2() {
		/* prepare */
		Type superType1 = mock(Type.class);
		when(superType1.getName()).thenReturn("test.Super1");
		typeToTest.extendFrom(superType1);

		/* test */
		assertTrue(typeToTest.isDescendantOf("test.Super1"));
	}

	@Test
	public void type1_extends_type2_so_type1_is_NOT_descendant_of_type3() {
		/* prepare */
		Type superType1 = mock(Type.class);
		when(superType1.getName()).thenReturn("test.Super1");
		typeToTest.extendFrom(superType1);

		/* test */
		assertFalse(typeToTest.isDescendantOf("test.SuperX"));
	}

	@Test
	public void type1_extends_type2_extends_typ3__so_type1_is_descendant_of_type2_and_also_of_type3() {
		/* prepare */
		Type superType1 = mock(Type.class);
		when(superType1.getName()).thenReturn("test.Super1");

		Type superType2 = mock(Type.class);
		when(superType2.getName()).thenReturn("test.Super2");
		when(superType1.isDescendantOf("test.Super2")).thenReturn(true);

		typeToTest.extendFrom(superType1);

		/* test */
		assertTrue(typeToTest.isDescendantOf("test.Super1"));
		assertTrue(typeToTest.isDescendantOf("test.Super2"));
	}

	@Test
	public void extendBySuperType_method1_from_super_type1_returned_in_getMethods() {
		/* prepare */
		Type superType = mock(Type.class);
		Method method1 = mock(Method.class);
		Set<Method> methodSet = new LinkedHashSet<>();
		methodSet.add(method1);
		when(superType.getMethods()).thenReturn(methodSet);

		/* check preconditions */
		assertTrue(typeToTest.getMethods().isEmpty());

		/* execute */
		typeToTest.extendFrom(superType);

		/* test */
		assertFalse(typeToTest.getMethods().isEmpty());
		assertTrue(typeToTest.getMethods().contains(method1));

		Reason reason1 = typeToTest.getReasonFor(method1);
		assertNotNull(reason1);
		assertEquals(superType, reason1.getSuperType());
	}

	@Test
	public void extendBySuperType_property1_from_super_type1_returned_in_getProperties() {
		/* prepare */
		Type superType = mock(Type.class);
		Property property1 = mock(Property.class);
		Set<Property> methodSet = new LinkedHashSet<>();
		methodSet.add(property1);
		when(superType.getProperties()).thenReturn(methodSet);

		/* check preconditions */
		assertTrue(typeToTest.getMethods().isEmpty());

		/* execute */
		typeToTest.extendFrom(superType);

		/* test */
		assertFalse(typeToTest.getProperties().isEmpty());
		assertTrue(typeToTest.getProperties().contains(property1));

		Reason reason1 = typeToTest.getReasonFor(property1);
		assertNotNull(reason1);
		assertEquals(superType, reason1.getSuperType());
	}

	@Test
	public void extendBySuperType_own_method0_and_method1_from_super_type1_returned_in_getMethods() {
		/* prepare */
		Method method0 = mock(Method.class);
		typeToTest.getMethods().add(method0);

		Type superType = mock(Type.class);
		Method method1 = mock(Method.class);
		Set<Method> superMethodSet = new LinkedHashSet<>();
		superMethodSet.add(method1);
		when(superType.getMethods()).thenReturn(superMethodSet);

		/* tree set does not add when compare==0 */
		when(method0.compareTo(method1)).thenReturn(-1);
		when(method1.compareTo(method0)).thenReturn(1);

		/* check preconditions */
		assertEquals(1, typeToTest.getMethods().size());

		/* execute */
		typeToTest.extendFrom(superType);

		/* test */
		assertEquals(2, typeToTest.getMethods().size());
		assertTrue(typeToTest.getMethods().contains(method1));
		assertTrue(typeToTest.getMethods().contains(method0));

		Reason reason0 = typeToTest.getReasonFor(method0);
		assertNull(reason0);

		Reason reason1 = typeToTest.getReasonFor(method1);
		assertNotNull(reason1);
		assertEquals(superType, reason1.getSuperType());
	}

	@Test
	public void extendBySuperType_own_property0_and_property1_from_super_type1_returned_in_getProperties() {
		/* prepare */
		Property property0 = mock(Property.class);
		typeToTest.getProperties().add(property0);

		Type superType = mock(Type.class);
		Property property1 = mock(Property.class);
		Set<Property> propertySet = new TreeSet<>();
		propertySet.add(property1);
		when(superType.getProperties()).thenReturn(propertySet);
		/* tree set does not add when compare==0 */
		when(property0.compareTo(property1)).thenReturn(-1);
		when(property1.compareTo(property0)).thenReturn(1);

		/* check preconditions */
		assertEquals(1, typeToTest.getProperties().size());

		/* execute */
		typeToTest.extendFrom(superType);

		/* test */
		assertEquals(2, typeToTest.getProperties().size());
		assertTrue(typeToTest.getProperties().contains(property1));
		assertTrue(typeToTest.getProperties().contains(property0));

		Reason reason0 = typeToTest.getReasonFor(property0);
		assertNull(reason0);

		Reason reason1 = typeToTest.getReasonFor(property1);
		assertNotNull(reason1);
		assertEquals(superType, reason1.getSuperType());
	}

	@Test
	public void extendByNull_accepted_getmethods_returns_only_own_methods() {
		/* prepare */
		Type superType = mock(Type.class);
		Method method1 = mock(Method.class);
		Method method0 = mock(Method.class);
		Set<Method> methodSet = new LinkedHashSet<>();
		methodSet.add(method1);
		when(superType.getMethods()).thenReturn(methodSet);

		/* check preconditions */
		Set<Method> methods = typeToTest.getMethods();
		typeToTest.getMethods().add(method0);

		/* execute */
		typeToTest.extendFrom(null);

		/* test */
		methods = typeToTest.getMethods();
		assertFalse(methods.isEmpty());
		assertTrue(methods.contains(method0));
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
	public void mixin_a_type_with_property1_reason1_returns__property1__for_mixin_id() {
		/* prepare */
		Type mixinType = mock(Type.class);
		Property property1 = mock(Property.class);
		Set<Property> propertySet = new LinkedHashSet<>();
		propertySet.add(property1);
		when(mixinType.getProperties()).thenReturn(propertySet);

		XMLType type = new XMLType();

		/* check preconditions */
		assertTrue(type.getProperties().isEmpty());
		Reason reason1 = mock(Reason.class);
		when(reason1.getMixinId()).thenReturn("braveMixinId");

		/* execute */
		type.mixin(mixinType, reason1);

		/* test */
		assertEquals(property1, type.getElementForMixin("braveMixinId"));
	}

	@Test
	public void mixin_a_type_with_method1_reason1_returns__method1__for_mixin_id() {
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
		when(reason1.getMixinId()).thenReturn("braveMixinId");

		/* execute */
		type.mixin(mixinType, reason1);

		/* test */
		assertEquals(method1, type.getElementForMixin("braveMixinId"));
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

	@Test
	public void type_with_nameX_compare_to_other_type_with_same_name_is_0_both_ways() {
		/* prepare */
		XMLType type1 = new XMLType();
		type1.name = "nameX";

		XMLType type2 = new XMLType();
		type2.name = "nameX";

		/* test + execute */
		assertEquals(0, type2.compareTo(type1));
		assertEquals(0, type1.compareTo(type2));
	}

	@Test
	public void type_with_name1_compare_to_other_type_with_name2_is_lower_0() {
		/* prepare */
		XMLType type1 = new XMLType();
		type1.name = "name1";

		XMLType type2 = new XMLType();
		type2.name = "name2";

		/* test + execute */
		assertTrue(type1.compareTo(type2) < 0);
	}

	@Test
	public void type_with_name2_compare_to_other_type_with_name1_is_higher_0() {
		/* prepare */
		XMLType type1 = new XMLType();
		type1.name = "name1";

		XMLType type2 = new XMLType();
		type2.name = "name2";

		/* test + execute */
		assertTrue(type2.compareTo(type1) > 0);
	}
}
