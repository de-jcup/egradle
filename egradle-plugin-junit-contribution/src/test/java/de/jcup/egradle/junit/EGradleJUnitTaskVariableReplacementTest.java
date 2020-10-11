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
package de.jcup.egradle.junit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EGradleJUnitTaskVariableReplacementTest {

    private EGradleJUnitTaskVariableReplacement supportToTest;

    @Before
    public void before() {
        supportToTest = new EGradleJUnitTaskVariableReplacement();
    }

    @Test
    public void tasks_xxx_without_variable_are_kept_as_is() {
        /* prepare */
        String tasksWithOutVariable = "xxx";

        /* execute */
        String tasks = supportToTest.replace(tasksWithOutVariable, "REPLACEMENT");

        /* test */
        assertEquals(tasksWithOutVariable, tasks);
    }

    @Test
    public void tasks_with_clean_test__means_old_default__will_be_replaced() {
        /* prepare */
        String tasksWithOutVariable = "clean test";

        /* execute */
        String tasks = supportToTest.replace(tasksWithOutVariable, "REPLACEMENT");

        /* test */
        assertEquals("REPLACEMENT", tasks);
    }

    @Test
    public void tasks_null__are_kept_as_null() {
        /* prepare */
        String tasksWithVariable = null;

        /* execute */
        String tasks = supportToTest.replace(tasksWithVariable, "REPLACEMENT");

        /* test */
        assertEquals(tasksWithVariable, tasks);
    }

    @Test
    public void tasks_with_variable_are_replaced() {
        /* prepare */
        String tasksWithVariable = EGradleJUnitTaskVariableReplacement.TASKS_VARIABLE;

        /* execute */
        String tasks = supportToTest.replace(tasksWithVariable, "REPLACEMENT");

        /* test */
        assertEquals("REPLACEMENT", tasks);
    }

    @Test
    public void when_replacement_is_null__tasks_with_variable_is_kept_as_is() {
        /* prepare */
        String tasksWithVariable = EGradleJUnitTaskVariableReplacement.TASKS_VARIABLE;

        /* execute */
        String tasks = supportToTest.replace(tasksWithVariable, null);

        /* test */
        assertEquals(tasksWithVariable, tasks);
    }

    @Test
    public void tasks_with_variable_and_followed_arguments_are_replaced_but_argument_still_exists() {
        /* prepare */
        String tasksWithVariable = EGradleJUnitTaskVariableReplacement.TASKS_VARIABLE + " --arguments";

        /* execute */
        String tasks = supportToTest.replace(tasksWithVariable, "REPLACEMENT");

        /* test */
        assertEquals("REPLACEMENT --arguments", tasks);
    }

    @Test
    public void tasks_with_prefix_variable_and_followed_arguments_are_replaced_but_argument_and_prefix_still_exist() {
        /* prepare */
        String tasksWithVariable = "xxx " + EGradleJUnitTaskVariableReplacement.TASKS_VARIABLE + " --test xyz";

        /* execute */
        String tasks = supportToTest.replace(tasksWithVariable, "REPLACEMENT");

        /* test */
        assertEquals("xxx REPLACEMENT --test xyz", tasks);
    }

}
