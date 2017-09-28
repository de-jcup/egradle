/*
 * Copyright 2017 Albert Tregnaghi
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
 package de.jcup.egradle.core.text;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.text.DocumentIdentifier.IdentifierStringIdBuilder;

public class IdentifierStringIdBuilderTest {

	private IdentifierStringIdBuilder builderToTest;

	@Before
	public void before(){
		builderToTest = DocumentIdentifier.createStringIdBuilder();
	}
	
	@Test
	public void test_hallo_welt_created_as_array() {
		/* execute */
		String[] created = builderToTest.add("hallo").add("welt").build();
		
		/* test */
		assertArrayEquals(new String[]{"hallo","welt"}, created);
	}
	
	private enum DocumentTestidentifiers1 implements DocumentIdentifier{
		ALPHA,
		
		BETA,
		;

		@Override
		public String getId() {
			return name();
		}
		
	}
	
	private enum DocumentTestidentifiers2 implements DocumentIdentifier{
		GAMMA,
		
		DELTA,
		;

		@Override
		public String getId() {
			return name();
		}
		
	}
	
	@Test
	public void test_document_identifier1() {
		/* execute */
		String[] created = builderToTest.addAll(DocumentTestidentifiers1.values()).build();
		
		/* test */
		assertArrayEquals(new String[]{"ALPHA","BETA"}, created);
	}
	
	@Test
	public void test_document_identifier1_and_2() {
		/* execute */
		String[] created = builderToTest.addAll(DocumentTestidentifiers1.values()).addAll(DocumentTestidentifiers2.values()).build();
		
		/* test */
		assertArrayEquals(new String[]{"ALPHA","BETA","GAMMA","DELTA"}, created);
	}
	
	@Test
	public void test_default_and_document_identifier1_and_2() {
		/* execute */
		String[] created = builderToTest.add("default").addAll(DocumentTestidentifiers1.values()).addAll(DocumentTestidentifiers2.values()).build();
		
		/* test */
		assertArrayEquals(new String[]{"default","ALPHA","BETA","GAMMA","DELTA"}, created);
	}

}
