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
package de.jcup.egradle.codeassist;

import static org.junit.Assert.*;

import org.junit.Test;

public class AbstractProposalImplTest {

    @Test
    public void getCode_never_null() {
        TestProposalImpl1 impl = new TestProposalImpl1();
        assertNotNull(impl.getCode());
    }

    @Test
    public void getLabel_never_null() {
        TestProposalImpl1 impl = new TestProposalImpl1();
        assertNotNull(impl.getLabel());
    }

    @Test
    public void getDescription_can_be_null() {
        TestProposalImpl1 impl = new TestProposalImpl1();
        assertNull(impl.getDescription());
    }

    @Test
    public void getType_can_be_null() {
        TestProposalImpl1 impl = new TestProposalImpl1();
        assertNull(impl.getType());
    }

    @Test
    public void compare_two_different_implementations_with_same_name_returns_0() {
        TestProposalImpl1 impl1 = new TestProposalImpl1();
        impl1.setName("name1");

        TestProposalImpl2 impl2 = new TestProposalImpl2();
        impl2.setName("name1");

        assertEquals(0, impl1.compareTo(impl2));
        assertEquals(0, impl2.compareTo(impl1));
    }

    @Test
    public void equals_two_different_implementations_with_same_name_returns_false_because_not_same_class() {
        TestProposalImpl1 impl1 = new TestProposalImpl1();
        impl1.setName("name1");

        TestProposalImpl2 impl2 = new TestProposalImpl2();
        impl2.setName("name1");

        assertFalse(impl1.equals(impl2));
        assertFalse(impl2.equals(impl1));
    }

    @Test
    public void compare_two_different_implementations_with_impl1_has_albert_and_impl2_has_berta_returns_n1_for_impl1_compareTo_impl2() {
        TestProposalImpl1 impl1 = new TestProposalImpl1();
        impl1.setName("albert");

        TestProposalImpl2 impl2 = new TestProposalImpl2();
        impl2.setName("berta");

        assertEquals(-1, impl1.compareTo(impl2));
    }

    @Test
    public void compare_two_different_implementations_with_impl1_has_albert_and_impl2_has_berta_returns_1_for_impl2_compareTo_impl1() {
        TestProposalImpl1 impl1 = new TestProposalImpl1();
        impl1.setName("albert");

        TestProposalImpl2 impl2 = new TestProposalImpl2();
        impl2.setName("berta");

        assertEquals(1, impl2.compareTo(impl1));
    }

    private class TestProposalImpl1 extends AbstractProposalImpl {

    }

    private class TestProposalImpl2 extends AbstractProposalImpl {

    }
}
