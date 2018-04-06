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
package de.jcup.egradle.sdk.builder.action.type;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SaveTypesToSDKTargetFolderTest {

	private SaveTypesToSDKTargetFolder actionToTest;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before() {
		actionToTest = new SaveTypesToSDKTargetFolder();
	}

	@Test
	public void subpath_is_reduced_without_parent__when_parent_in_subpath() throws Exception {
		/* prepare */
		File parent = new File("parent");
		File file = new File("parent/child1/child2/child3/Test.xml");

		/* execute */
		String subPath = actionToTest.extractSubPathFromFile(file, parent);

		/* test */
		assertEquals("child1/child2/child3/Test.xml", subPath);

	}

	@Test
	public void when_parent_is_not_parent_of_file__illegal_argument_exception_is_thrown() throws Exception {
		/* prepare */
		File parent = new File("wrongParent");
		File file = new File("parent/child1/child2/child3/Test.xml");

		/* test on next execute */
		expectedException.expect(IllegalArgumentException.class);

		/* execute */
		actionToTest.extractSubPathFromFile(file, parent);

	}

}
