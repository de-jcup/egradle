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
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ModelInspectorTest {
	
	private ModelInspector modelInspectorToTest;
	private Model mockedModel;

	@Before
	public void before(){
		modelInspectorToTest = new ModelInspector();
		mockedModel = mock(Model.class);
	}

	@Test
	public void find_type_of_variable__in_model_with_3_root_children_having_2_variables_returns_2_items() {
		/* prepare */
		Item root = new Item();
		when(mockedModel.getRoot()).thenReturn(root);
		Item child1= new Item();
		child1.setName("test1");
		child1.setItemType(ItemType.VARIABLE);
		root.add(child1);
		
		Item child2= new Item();
		child2.setName("test2");
		child2.setItemType(ItemType.APPLY_PLUGIN);
		root.add(child2);
		
		Item child3= new Item();
		child3.setName("test3");
		child3.setItemType(ItemType.VARIABLE);
		root.add(child3);
		
		/* execute */
		List<Item> result = modelInspectorToTest.findAllItemsOfType(ItemType.VARIABLE, mockedModel);
	
		/* test */
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains(child1));
		assertFalse(result.contains(child2));
		assertTrue(result.contains(child3));
	
	}
	
	@Test
	public void find_type_of_variable__in_model_with_2_rootchildren_where_firstroot_child_has2_children_as_variables_returns_2_items() {
		/* prepare */
		Item root = new Item();
		when(mockedModel.getRoot()).thenReturn(root);
		Item parent1= new Item();
		parent1.setName("test1");
		parent1.setItemType(ItemType.ALL_PROJECTS);
		root.add(parent1);
		
		Item child2= new Item();
		child2.setName("test2");
		child2.setItemType(ItemType.APPLY_PLUGIN);
		root.add(child2);
		
		Item child3= new Item();
		child3.setName("test3");
		child3.setItemType(ItemType.APPLY_PLUGIN);
		root.add(child3);
		
		
		Item parent1Child1= new Item();
		parent1Child1.setName("test1");
		parent1Child1.setItemType(ItemType.VARIABLE);
		parent1.add(parent1Child1);
		
		Item parent1Child2= new Item();
		parent1Child2.setName("test2");
		parent1Child2.setItemType(ItemType.APPLY_PLUGIN);
		parent1.add(parent1Child2);
		
		Item parent1Child3= new Item();
		parent1Child3.setName("test3");
		parent1Child3.setItemType(ItemType.VARIABLE);
		parent1.add(parent1Child3);
		
		/* execute */
		List<Item> result = modelInspectorToTest.findAllItemsOfType(ItemType.VARIABLE, mockedModel);
	
		/* test */
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains(parent1Child1));
		assertFalse(result.contains(parent1Child2));
		assertTrue(result.contains(parent1Child3));
	
	}
	
	@Test
	public void find_type_of_variable__in_root_with_2_rootchildren_where_firstroot_child_has2_children_as_variables_returns_2_items() {
		/* prepare */
		Item root = new Item();
		when(mockedModel.getRoot()).thenReturn(root);
		Item parent1= new Item();
		parent1.setName("test1");
		parent1.setItemType(ItemType.ALL_PROJECTS);
		root.add(parent1);
		
		Item child2= new Item();
		child2.setName("test2");
		child2.setItemType(ItemType.APPLY_PLUGIN);
		root.add(child2);
		
		Item child3= new Item();
		child3.setName("test3");
		child3.setItemType(ItemType.APPLY_PLUGIN);
		root.add(child3);
		
		
		Item parent1Child1= new Item();
		parent1Child1.setName("test1");
		parent1Child1.setItemType(ItemType.VARIABLE);
		parent1.add(parent1Child1);
		
		Item parent1Child2= new Item();
		parent1Child2.setName("test2");
		parent1Child2.setItemType(ItemType.APPLY_PLUGIN);
		parent1.add(parent1Child2);
		
		Item parent1Child3= new Item();
		parent1Child3.setName("test3");
		parent1Child3.setItemType(ItemType.VARIABLE);
		parent1.add(parent1Child3);
		
		/* execute */
		List<Item> result = modelInspectorToTest.findAllItemsOfType(ItemType.VARIABLE, root);
	
		/* test */
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains(parent1Child1));
		assertFalse(result.contains(parent1Child2));
		assertTrue(result.contains(parent1Child3));
	
	}
	
	@Test
	public void find_type_of_variable__in_parent1_with_2_rootchildren_where_firstroot_child_has2_children_as_variables_returns_2_items() {
		/* prepare */
		Item root = new Item();
		when(mockedModel.getRoot()).thenReturn(root);
		Item parent1= new Item();
		parent1.setName("test1");
		parent1.setItemType(ItemType.ALL_PROJECTS);
		root.add(parent1);
		
		Item child2= new Item();
		child2.setName("test2");
		child2.setItemType(ItemType.APPLY_PLUGIN);
		root.add(child2);
		
		Item child3= new Item();
		child3.setName("test3");
		child3.setItemType(ItemType.APPLY_PLUGIN);
		root.add(child3);
		
		
		Item parent1Child1= new Item();
		parent1Child1.setName("test1");
		parent1Child1.setItemType(ItemType.VARIABLE);
		parent1.add(parent1Child1);
		
		Item parent1Child2= new Item();
		parent1Child2.setName("test2");
		parent1Child2.setItemType(ItemType.APPLY_PLUGIN);
		parent1.add(parent1Child2);
		
		Item parent1Child3= new Item();
		parent1Child3.setName("test3");
		parent1Child3.setItemType(ItemType.VARIABLE);
		parent1.add(parent1Child3);
		
		/* execute */
		List<Item> result = modelInspectorToTest.findAllItemsOfType(ItemType.VARIABLE, parent1);
	
		/* test */
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains(parent1Child1));
		assertFalse(result.contains(parent1Child2));
		assertTrue(result.contains(parent1Child3));
	
	}
	
	@Test
	public void find_type_of_variable__in_rootchild2_with_2_rootchildren_where_firstroot_child_has2_children_as_variables_returns_0_items() {
		/* prepare */
		Item root = new Item();
		when(mockedModel.getRoot()).thenReturn(root);
		Item parent1= new Item();
		parent1.setName("test1");
		parent1.setItemType(ItemType.ALL_PROJECTS);
		root.add(parent1);
		
		Item child2= new Item();
		child2.setName("test2");
		child2.setItemType(ItemType.APPLY_PLUGIN);
		root.add(child2);
		
		Item child3= new Item();
		child3.setName("test3");
		child3.setItemType(ItemType.APPLY_PLUGIN);
		root.add(child3);
		
		
		Item parent1Child1= new Item();
		parent1Child1.setName("test1");
		parent1Child1.setItemType(ItemType.VARIABLE);
		parent1.add(parent1Child1);
		
		Item parent1Child2= new Item();
		parent1Child2.setName("test2");
		parent1Child2.setItemType(ItemType.APPLY_PLUGIN);
		parent1.add(parent1Child2);
		
		Item parent1Child3= new Item();
		parent1Child3.setName("test3");
		parent1Child3.setItemType(ItemType.VARIABLE);
		parent1.add(parent1Child3);
		
		/* execute */
		List<Item> result = modelInspectorToTest.findAllItemsOfType(ItemType.VARIABLE, child2);
	
		/* test */
		assertNotNull(result);
		assertEquals(0, result.size());
	
	}

}
