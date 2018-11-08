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

import static de.jcup.egradle.integration.TypeAssert.*;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.sdk.SDKTestUtil;

public class DSLOriginXMLTypeImporterTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private File dslFolder;
	private XMLTypeImporter importerToTest;
	private File xmlFile;

	@Before
	public void before() {
		dslFolder = new File(SDKTestUtil.SRC_TEST_RES_FOLDER, "dsl-origin/");
		xmlFile = new File(dslFolder, "Ear.xml");
		importerToTest = new XMLTypeImporter();
	}

	@Test
	public void ear_xml_file_has_property_lib_and_method_lib_after_import() throws Exception {

		/* execute */
		Type type = importerToTest.importType(new FileInputStream(xmlFile));

		/* test */
		/* @formatter:off*/
		assertType(type).
			hasName("org.gradle.plugins.ear.Ear").
			hasProperty("lib").
			hasMethod("lib", "groovy.lang.Closure");
		/* @formatter:on*/
	}

}
