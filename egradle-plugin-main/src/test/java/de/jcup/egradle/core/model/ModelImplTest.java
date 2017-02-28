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
 package de.jcup.egradle.core.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ModelImplTest {

	private ModelImpl modelToTest;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before() {
		modelToTest = new ModelImpl();
	}
	
	@Test
	public void root_is_always_a_possible_parent(){
		assertTrue(modelToTest.getRoot().isAPossibleParent());
	}
	
	@Test
	public void model_parent_child1_child2__between_child1_and_child2_returns_parent_item_as_parent(){
		Item parent= new Item();
		parent.setOffset(0);
		Item child1 = new Item();
		child1.setOffset(10);
		child1.setLength(4);
		// gap between child1(10-14) and child2(18-20)
		Item child2 = new Item();
		child2.setOffset(18);
		child2.setLength(2);
		
		parent.setLength(21);
		parent.add(child1);
		parent.add(child2);
		modelToTest.getRoot().add(parent);
		
		assertEquals(parent, modelToTest.getParentItemOf(15));
	}
	
	@Test
	public void model_parent_child1_child2__after_child2__returns_root_item_as_parent(){
		Item parent= new Item();
		parent.setOffset(0);
		Item child1 = new Item();
		child1.setOffset(10);
		child1.setLength(4);
		// gap between child1(10-14) and child2(18-20)
		Item child2 = new Item();
		child2.setOffset(18);
		child2.setLength(2);
		
		parent.setLength(21);
		parent.add(child1);
		parent.add(child2);
		modelToTest.getRoot().add(parent);
		
		assertEquals(modelToTest.getRoot(), modelToTest.getParentItemOf(22));
	}
	
	@Test
	public void model_parent1_child1_child2__parent2_child3_before_parent2__returns_root_item_as_parent(){
		Item parent1= new Item();
		parent1.setOffset(0);
		Item child1 = new Item();
		child1.setOffset(10);
		child1.setLength(4);
		// gap between child1(10-14) and child2(18-20)
		Item child2 = new Item();
		child2.setOffset(18);
		child2.setLength(2);
		
		parent1.setLength(21);
		parent1.add(child1);
		parent1.add(child2);
		
		Item parent2= new Item();
		parent2.setOffset(24);
		Item child3 = new Item();
		child3.setOffset(30);
		child3.setLength(5); // 30+5=35-24=11
		parent2.setLength(11);
		parent2.add(child3);
		modelToTest.getRoot().add(parent1);
		modelToTest.getRoot().add(parent2);
		
		assertEquals(modelToTest.getRoot(), modelToTest.getParentItemOf(22));
	}
	
	@Test
	public void model_parent1_child1_child2__parent2_child3_at_child3__returns_parent2_item_as_parent(){
		Item parent1= new Item();
		parent1.setOffset(0);
		Item child1 = new Item();
		child1.setOffset(10);
		child1.setLength(4);
		// gap between child1(10-14) and child2(18-20)
		Item child2 = new Item();
		child2.setOffset(18);
		child2.setLength(2);
		
		parent1.setLength(21);
		parent1.add(child1);
		parent1.add(child2);
		
		Item parent2= new Item();
		parent2.setName("parent2");
		parent2.setOffset(24);
		Item child3 = new Item();
		child3.setOffset(30);
		child3.setLength(5); // 30+5=35-24=11
		parent2.setLength(11);
		parent2.add(child3);
		modelToTest.getRoot().add(parent1);
		modelToTest.getRoot().add(parent2);
		
		assertEquals(parent2, modelToTest.getParentItemOf(30));
	}
	
	@Test
	public void model_parent1_child1_child2__parent2_child3_before_child3__returns_parent2_item_as_parent(){
		Item parent1= new Item();
		parent1.setOffset(0);
		Item child1 = new Item();
		child1.setOffset(10);
		child1.setLength(4);
		// gap between child1(10-14) and child2(18-20)
		Item child2 = new Item();
		child2.setOffset(18);
		child2.setLength(2);
		
		parent1.setLength(21);
		parent1.add(child1);
		parent1.add(child2);
		
		Item parent2= new Item();
		parent2.setName("parent2");
		parent2.setOffset(24);
		Item child3 = new Item();
		child3.setOffset(30);
		child3.setLength(5); // 30+5=35-24=11
		parent2.setLength(11);
		parent2.add(child3);
		
		modelToTest.getRoot().add(parent1);
		modelToTest.getRoot().add(parent2);
		
		assertEquals(parent2, modelToTest.getParentItemOf(29));
	}
	
	@Test
	public void empty_model_returns_not_null_on_getRoot() {
		assertNotNull(modelToTest.getRoot());
	}

	@Test
	public void empty_model_returns_not_null_getRoot_getChildrenUnmodifable() {
		assertNotNull(modelToTest.getRoot().getChildren());
	}

	@Test
	public void getItemAt_works_for_exact_positions() {
		Item child1 = new Item();
		child1.setOffset(10);
		Item child2 = new Item();
		child2.setOffset(20);
		modelToTest.getRoot().add(child1);
		modelToTest.getRoot().add(child2);
		
		assertEquals(child1, modelToTest.getItemAt(10));
		assertEquals(child2, modelToTest.getItemAt(20));
	}
	
	@Test
	public void getItemAt_pos_between_child1_and_child2__returns_child1() {
		Item child1 = new Item();
		child1.setOffset(10);
		Item child2 = new Item();
		child2.setOffset(20);
		modelToTest.getRoot().add(child1);
		modelToTest.getRoot().add(child2);
		
		assertEquals(child1, modelToTest.getItemAt(15));
	}
	

	@Test
	/**
	 * <pre>
	 * 		root (0-21)
	 *         child1 (10-14)
	 *         ->x (15) (no item)
	 *         child2 (18-20)
	 * </pre>
	 * Expected result for x is root
	 */
	public void getItemAt_pos_between_child1_and_child2__returns_root() {
		Item root= new Item();
		root.setOffset(0);
		Item child1 = new Item();
		child1.setOffset(10);
		child1.setLength(4);
		Item child2 = new Item();
		child2.setOffset(18);
		child2.setLength(2);
		root.setLength(21);
		root.add(child1);
		root.add(child2);
		modelToTest.getRoot().add(root);
		
		assertEquals(root, modelToTest.getItemAt(15));
	}
}
