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
package de.jcup.egradle.sdk.builder.action.delegationtarget;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeReference;
import de.jcup.egradle.codeassist.dsl.XMLMethod;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;

public class EstimateDelegationTargetsByJavadocActionTest {

    private EstimateDelegationTargetsByJavadocAction actionToTest;
    private SDKBuilderContext context;

    @Before
    public void before() {
        actionToTest = new EstimateDelegationTargetsByJavadocAction();
        context = Mockito.mock(SDKBuilderContext.class);
    }

    @Test
    public void estimateDelegationTarget_byJavaDoc__for_type_set_delegate_from_javadoc_of_method_into_method__simple_name() {
        /* prepare */
        Type type = mock(Type.class);
        Set<Method> methods = new LinkedHashSet<>();
        XMLMethod m1 = mock(XMLMethod.class);
        when(m1.getDelegationTarget()).thenReturn(null);
        StringBuilder description1 = new StringBuilder();
        description1.append("bla\n<br> bla\n<br> bla <a href='type://Type1'>TypeX</a> and so on ...");
        when(m1.getDescription()).thenReturn(description1.toString());
        methods.add(m1);
        when(type.getDefinedMethods()).thenReturn(methods);

        /* execute */
        actionToTest.estimateDelegateTargets_by_javdoc(type, context, false);

        /* test */
        verify(m1).setDelegationTargetAsString("Type1");
    }

    @Test
    public void estimateDelegationTarget_byJavaDoc__for_type_set_delegate_from_javadoc_of_interface_into_method_when_method_has_no_data() {
        /* prepare */

        /* method 1 - in type, but with inherited doc */
        Type type = mock(Type.class);
        Set<Method> methods1 = new LinkedHashSet<>();
        XMLMethod method1 = mock(XMLMethod.class);
        when(method1.getDelegationTarget()).thenReturn(null);

        StringBuilder description1 = new StringBuilder();
        description1.append("@inheritDoc");
        when(method1.getDescription()).thenReturn(description1.toString());

        methods1.add(method1);
        when(type.getDefinedMethods()).thenReturn(methods1);

        /* method 2 - in interface */
        Type interfaceType = mock(Type.class);
        Set<Method> interfaceMethods = new LinkedHashSet<>();
        XMLMethod interfaceMethod = mock(XMLMethod.class);
        when(interfaceMethod.getDelegationTarget()).thenReturn(null);

        StringBuilder interfaceMethodDescription = new StringBuilder();
        interfaceMethodDescription.append("bla\n<br> bla\n<br> bla <a href='type://org.destination.Type1'>TypeX</a> and so on ...");
        when(interfaceMethod.getDescription()).thenReturn(interfaceMethodDescription.toString());

        interfaceMethods.add(interfaceMethod);
        when(interfaceType.getDefinedMethods()).thenReturn(interfaceMethods);

        /* interface references */
        Set<TypeReference> interfaceReferences = new LinkedHashSet<>();
        TypeReference interfaceReference = mock(TypeReference.class);
        when(interfaceReference.getType()).thenReturn(interfaceType);
        interfaceReferences.add(interfaceReference);

        when(type.getInterfaces()).thenReturn(interfaceReferences);

        /* execute */
        actionToTest.estimateDelegateTargets_by_javdoc(type, context, false);

        /* test */
        verify(method1).setDelegationTargetAsString("org.destination.Type1");
    }

    @Test
    public void estimateDelegationTarget_byJavaDoc__for_type_set_delegate_from_javadoc_of_interfaces_first_where_hyperlink_found() {
        /* prepare */

        /* method 1 - in type, but with inherited doc */
        Type type = mock(Type.class);
        Set<Method> methods1 = new LinkedHashSet<>();
        XMLMethod method1 = mock(XMLMethod.class);
        when(method1.getDelegationTarget()).thenReturn(null);

        StringBuilder description1 = new StringBuilder();
        description1.append("@inheritDoc");
        when(method1.getDescription()).thenReturn(description1.toString());

        methods1.add(method1);
        when(type.getDefinedMethods()).thenReturn(methods1);

        /* in interface */
        Type interfaceType1 = mock(Type.class);
        Set<Method> interfaceMethods1 = new LinkedHashSet<>();
        XMLMethod interfaceMethod1 = mock(XMLMethod.class);
        interfaceMethods1.add(interfaceMethod1);
        when(interfaceMethod1.getDescription()).thenReturn("description, but no link");
        when(interfaceType1.getDefinedMethods()).thenReturn(interfaceMethods1);

        Type interfaceType2 = mock(Type.class);
        Set<Method> interfaceMethods2 = new LinkedHashSet<>();
        XMLMethod interfaceMethod2 = mock(XMLMethod.class);
        interfaceMethods2.add(interfaceMethod2);

        StringBuilder interfaceMethod2Description = new StringBuilder();
        interfaceMethod2Description.append("bla\n<br> bla\n<br> bla <a href='type://org.destination.Type1'>TypeX</a> and so on ...");
        when(interfaceMethod2.getDescription()).thenReturn(interfaceMethod2Description.toString());

        when(interfaceType2.getDefinedMethods()).thenReturn(interfaceMethods2);

        /* interface references */
        TypeReference interfaceReference2 = mock(TypeReference.class);
        when(interfaceReference2.getType()).thenReturn(interfaceType2);

        TypeReference interfaceReference1 = mock(TypeReference.class);
        when(interfaceReference1.getType()).thenReturn(interfaceType1);

        Set<TypeReference> interfaceReferences = new LinkedHashSet<>();
        interfaceReferences.add(interfaceReference1); /* first is withing link */
        interfaceReferences.add(interfaceReference2);

        when(type.getInterfaces()).thenReturn(interfaceReferences);

        /* execute */
        actionToTest.estimateDelegateTargets_by_javdoc(type, context, false);

        /* test */
        verify(method1).setDelegationTargetAsString("org.destination.Type1");
    }

    @Test
    public void estimateDelegationTarget_byJavaDoc__for_type_set_delegate_from_javadoc_of_method_into_method_fullpath_name() {
        /* prepare */
        Type type = mock(Type.class);
        Set<Method> methods = new LinkedHashSet<>();
        XMLMethod m1 = mock(XMLMethod.class);
        when(m1.getDelegationTarget()).thenReturn(null);
        StringBuilder description1 = new StringBuilder();
        description1.append("bla\n<br> bla\n<br> bla <a href='type://org.destination.Type1'>TypeX</a> and so on ...");
        when(m1.getDescription()).thenReturn(description1.toString());
        methods.add(m1);
        when(type.getDefinedMethods()).thenReturn(methods);

        /* execute */
        actionToTest.estimateDelegateTargets_by_javdoc(type, context, false);

        /* test */
        verify(m1).setDelegationTargetAsString("org.destination.Type1");
    }

    @Test
    public void estimateDelegationTarget_byJavaDoc__for_type_set_no_delegate_from_javadoc_when_no_link_with_type_protocoll() {
        /* prepare */
        Type type = mock(Type.class);
        Set<Method> methods = new LinkedHashSet<>();
        XMLMethod m1 = mock(XMLMethod.class);
        when(m1.getDelegationTarget()).thenReturn(null);
        StringBuilder description1 = new StringBuilder();
        description1.append("bla\n<br> bla\n<br> bla <a href='http://www.eclipse.org'>TypeX</a> and so on ...");
        when(m1.getDescription()).thenReturn(description1.toString());
        methods.add(m1);
        when(type.getDefinedMethods()).thenReturn(methods);

        /* execute */
        actionToTest.estimateDelegateTargets_by_javdoc(type, context);

        /* test */
        verify(m1, never()).setDelegationTargetAsString(any());
    }

    @Test
    public void estimateDelegationTarget_byJavaDoc__for_type_set_delegate_from_javadoc_of_methods1_2_into_methods() {
        /* prepare */
        Type type = mock(Type.class, "type");
        Set<Method> methods = new LinkedHashSet<>();

        XMLMethod m1 = mock(XMLMethod.class, "m1");
        when(m1.getName()).thenReturn("m1");
        when(m1.getDelegationTarget()).thenReturn(null);
        StringBuilder description1 = new StringBuilder();
        description1.append("bla\n<br> bla\n<br> bla <a href='type://Type1'>TypeX</a> and so on ...");
        when(m1.getDescription()).thenReturn(description1.toString());
        methods.add(m1);

        XMLMethod m2 = mock(XMLMethod.class, "m2");
        when(m2.getName()).thenReturn("m2");
        when(m2.getDelegationTarget()).thenReturn(null);
        StringBuilder description2 = new StringBuilder();
        description2.append("bla\n<br> bla\n<br> bla <a href='type://Type2'>TypeX</a> and so on ...");
        when(m2.getDescription()).thenReturn(description2.toString());
        methods.add(m2);

        // type has two methods: m1 and m2, both have descriptions with
        // delegation info inside
        when(type.getDefinedMethods()).thenReturn(methods);

        /* execute */
        actionToTest.estimateDelegateTargets_by_javdoc(type, context, false);

        /* test */
        verify(m1).setDelegationTargetAsString("Type1");
        verify(m2).setDelegationTargetAsString("Type2");
    }

    @Test
    public void estimateDelegationTarget_byJavaDoc__for_type_set_NO_delegate_from_javadoc_of_method_into_method__when_containing_hash() {
        /* prepare */
        Type type = mock(Type.class);
        Set<Method> methods = new LinkedHashSet<>();
        XMLMethod m1 = mock(XMLMethod.class);
        when(m1.getDelegationTarget()).thenReturn(null);
        StringBuilder description1 = new StringBuilder();
        description1.append("bla\n<br> bla\n<br> bla <a href='type://Type1#blax'>TypeX</a> and so on ...");
        when(m1.getDescription()).thenReturn(description1.toString());
        methods.add(m1);
        when(type.getDefinedMethods()).thenReturn(methods);

        /* execute */
        actionToTest.estimateDelegateTargets_by_javdoc(type, context, false);

        /* test */
        verify(m1, never()).setDelegationTargetAsString(any());
    }

    @Test
    public void estimateDelegationTarget_byJavaDoc__for_type_set_type2_when_type1_is_with_hash() {
        /* prepare */
        Type type = mock(Type.class);
        Set<Method> methods = new LinkedHashSet<>();
        XMLMethod m1 = mock(XMLMethod.class);
        when(m1.getDelegationTarget()).thenReturn(null);
        StringBuilder description1 = new StringBuilder();
        description1.append("bla\n<br> bla\n<br> bla <a href='type://Type1#blax'>TypeX</a> and so <a href='type://Type2'>TypeX</a> on ...");
        when(m1.getDescription()).thenReturn(description1.toString());
        methods.add(m1);
        when(type.getDefinedMethods()).thenReturn(methods);

        /* execute */
        actionToTest.estimateDelegateTargets_by_javdoc(type, context, false);

        /* test */
        verify(m1).setDelegationTargetAsString("Type2");
    }

}
