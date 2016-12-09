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
 package de.jcup.egradle.core.api;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class SimpleMapStringTransformerTest {
	
	@Test
	public void transform_rootproject_dir__is_transformed() {
		/* prepare*/
		Map<String, String> map = new HashMap<String, String>();
		map.put(GradleStringTransformer.ROOTPROJECT_PROJECTDIR, "iAmRoot");
		
		/* execute */
		SimpleMapStringTransformer transformerToTest = new SimpleMapStringTransformer(map);
		String result = transformerToTest.transform("${rootProject.projectDir}/libraries.gradle");
		
		/* test */
		assertEquals("iAmRoot/libraries.gradle",result);
	}

}
