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
package de.jcup.egradle.codeassist.dsl.gradle;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Property;

public class GradleDSLCodeBuilderTest {

    private GradleDSLCodeTemplateBuilder builderToTest;

    @Before
    public void before() {
        builderToTest = new GradleDSLCodeTemplateBuilder();
    }

    @Test
    public void property_with_name1__with_type_string() {
        /* prepare */
        Property property = mock(Property.class);
        when(property.getName()).thenReturn("name1");
        when(property.getTypeAsString()).thenReturn("java.lang.String");

        /* execute + test */
        assertEquals("name1 = '$cursor'", builderToTest.createPropertyAssignment(property));
    }

    @Test
    public void property_with_name1__with_type_java_io_file() {
        /* prepare */
        Property property = mock(Property.class);
        when(property.getName()).thenReturn("name1");
        when(property.getTypeAsString()).thenReturn("java.io.File");

        /* execute + test */
        assertEquals("name1 = file('$cursor')", builderToTest.createPropertyAssignment(property));
    }

    @Test
    public void property_with_name1__with_type_null() {
        /* prepare */
        Property property = mock(Property.class);
        when(property.getName()).thenReturn("name1");
        when(property.getTypeAsString()).thenReturn(null);

        /* execute + test */
        assertEquals("name1 = $cursor", builderToTest.createPropertyAssignment(property));
    }

    @Test
    public void method_with_name1__without_parameters_returns_name1_two_brackets_and_cursor_followed() {
        /* prepare */
        Method method = mock(Method.class);
        when(method.getName()).thenReturn("name1");
        when(method.getParameters()).thenReturn(Collections.emptyList());

        /* execute + test */
        assertEquals("name1()$cursor", builderToTest.createSmartMethodCall(method));
    }

    @Test
    public void method_with_name1__with2_parameters_but_no_names_returns_arg0_cursor_space_arg1() {
        /* prepare */
        Method method = mock(Method.class);
        when(method.getName()).thenReturn("name1");
        List<Parameter> paramList = new ArrayList<>();
        Parameter parameter1 = mock(Parameter.class);
        when(parameter1.getTypeAsString()).thenReturn("other1");
        Parameter parameter2 = mock(Parameter.class);
        when(parameter2.getTypeAsString()).thenReturn("other2");
        paramList.add(parameter1);
        paramList.add(parameter2);
        when(method.getParameters()).thenReturn(paramList);

        /* execute + test */
        assertEquals("name1 arg0$cursor arg1", builderToTest.createSmartMethodCall(method));
    }

    @Test
    public void method_with_name1__with2_parameters_with_p1_p2_as_names_returns_p1_cursor_space_p2() {
        /* prepare */
        Method method = mock(Method.class);
        when(method.getName()).thenReturn("name1");
        List<Parameter> paramList = new ArrayList<>();
        Parameter parameter1 = mock(Parameter.class);
        when(parameter1.getTypeAsString()).thenReturn("other1");
        when(parameter1.getName()).thenReturn("p1");
        Parameter parameter2 = mock(Parameter.class);
        when(parameter2.getTypeAsString()).thenReturn("other2");
        when(parameter2.getName()).thenReturn("p2");
        paramList.add(parameter1);
        paramList.add(parameter2);
        when(method.getParameters()).thenReturn(paramList);

        /* execute + test */
        assertEquals("name1 p1$cursor p2", builderToTest.createSmartMethodCall(method));
    }

    @Test
    public void method_with_name1__with_string_parameter1_returns_name1_space_empty_string_start_cursor_close_string() {
        /* prepare */
        Method method = mock(Method.class);
        when(method.getName()).thenReturn("name1");
        List<Parameter> paramList = new ArrayList<>();
        Parameter parameter = mock(Parameter.class);
        when(parameter.getTypeAsString()).thenReturn("java.lang.String");
        paramList.add(parameter);
        when(method.getParameters()).thenReturn(paramList);

        /* execute + test */
        assertEquals("name1 \"$cursor\"", builderToTest.createSmartMethodCall(method));
    }

    @Test
    public void method_with_name1__with_file_parameter1() {
        /* prepare */
        Method method = mock(Method.class);
        when(method.getName()).thenReturn("name1");
        List<Parameter> paramList = new ArrayList<>();
        Parameter parameter = mock(Parameter.class);
        when(parameter.getTypeAsString()).thenReturn("java.io.File");
        paramList.add(parameter);
        when(method.getParameters()).thenReturn(paramList);

        /* execute + test */
        assertEquals("name1 file('$cursor')", builderToTest.createSmartMethodCall(method));
    }

    @Test
    public void method_with_name1__with_closure_parameter1_returns_name1_curly_bracket_closure_content_with_cursor_and_closure_close() {
        /* prepare */
        Method method = mock(Method.class);
        when(method.getName()).thenReturn("name1");
        List<Parameter> paramList = new ArrayList<>();
        Parameter parameter1 = mock(Parameter.class);
        when(parameter1.getTypeAsString()).thenReturn("java.lang.String");
        Parameter parameter2 = mock(Parameter.class);
        when(parameter2.getTypeAsString()).thenReturn("groovy.lang.Closure");
        paramList.add(parameter1);
        paramList.add(parameter2);
        when(method.getParameters()).thenReturn(paramList);

        /* execute + test */
        assertEquals("name1 \"$cursor\" {\n" + "    \n" + "}", builderToTest.createSmartMethodCall(method));
    }

    @Test
    public void method_with_name1__with_string_and_closure_parameter1_returns_name1_curly_bracket_closure_content_with_cursor_and_closure_close() {
        /* prepare */
        Method method = mock(Method.class);
        when(method.getName()).thenReturn("name1");
        List<Parameter> paramList = new ArrayList<>();
        Parameter parameter = mock(Parameter.class);
        when(parameter.getTypeAsString()).thenReturn("groovy.lang.Closure");
        paramList.add(parameter);
        when(method.getParameters()).thenReturn(paramList);

        /* execute + test */
        assertEquals("name1 {\n" + "    $cursor\n" + "}", builderToTest.createSmartMethodCall(method));
    }
}
