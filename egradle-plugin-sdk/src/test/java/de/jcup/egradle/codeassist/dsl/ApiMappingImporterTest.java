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
package de.jcup.egradle.codeassist.dsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.sdk.SDKTestUtil;

public class ApiMappingImporterTest {

	private ApiMappingImporter importerToTest;
	private File dslFolder;
	private File mappingsFile;

	@Before
	public void before() {
		importerToTest = new ApiMappingImporter();
		dslFolder = new File(SDKTestUtil.SRC_TEST_RES_FOLDER, "dsl/3.0");
		mappingsFile = new File(dslFolder, "api-mapping.txt");
	}

	@Test
	public void all_two_mappings_are_loaded() throws Exception {

		/* execute */
		Map<String, String> map = importerToTest.importMapping(new FileInputStream(mappingsFile));

		/* test */
		assertNotNull(map);

		Set<String> keys = map.keySet();
		assertEquals(2, keys.size());

		assertTrue(keys.contains("Delete"));
		assertTrue(keys.contains("ShortNameTestAction"));

		assertEquals("org.gradle.api.tasks.Delete", map.get("Delete"));
		assertEquals("org.gradle.api.TestAction", map.get("ShortNameTestAction"));
	}

}
