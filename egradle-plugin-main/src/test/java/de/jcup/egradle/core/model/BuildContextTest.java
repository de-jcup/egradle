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
import org.junit.Test;

import de.jcup.egradle.core.model.BuildContext;
import de.jcup.egradle.core.model.Error;

public class BuildContextTest {

	private BuildContext contextToTest;
	
	@Before
	public void before(){
		contextToTest = new BuildContext();
	}
	
	@Test
	public void test_nothing_added__has_errors_returns_false() {
		assertFalse(contextToTest.hasErrors());
	}

	@Test
	public void test_one_error_added__has_errors_returns_true() {
		contextToTest.add(new Error());
		assertTrue(contextToTest.hasErrors());
	}
}
