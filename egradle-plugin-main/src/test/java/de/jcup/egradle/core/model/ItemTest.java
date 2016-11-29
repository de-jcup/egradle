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

import org.junit.Test;

public class ItemTest {

	@Test
	public void item_tree_works() {
		Item item1 = new Item();
		Item item2 = new Item();
		Item item3 = new Item();
		Item item4 = new Item();
		Item item5 = new Item();
		
		/* execute */
		item1.add(item2);
		item1.add(item3);
		item3.add(item4);
		item3.add(item5);
		
		/* test */
		assertEquals(2,item1.getChildren().length);
		assertEquals(item2, item1.getChildren()[0]);
		assertEquals(item3, item1.getChildren()[1]);
		
		assertEquals(2,item3.getChildren().length);
	}
	
}
