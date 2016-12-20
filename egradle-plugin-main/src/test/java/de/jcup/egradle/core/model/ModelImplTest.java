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
