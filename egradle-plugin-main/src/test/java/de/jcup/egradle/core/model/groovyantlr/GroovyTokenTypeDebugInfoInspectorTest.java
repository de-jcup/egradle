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
 package de.jcup.egradle.core.model.groovyantlr;

import static org.junit.Assert.*;

import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;
import org.junit.Test;

public class GroovyTokenTypeDebugInfoInspectorTest {

	@Test
	public void for_field_ANNOTATION_int_number_the_name_ANNOTATION_is_returned() {
		/* prepare */
		GroovyTokenTypeDebugInfoInspector inspector = new GroovyTokenTypeDebugInfoInspector();
		
		/* execute */
		String name = inspector.getGroovyTokenTypeName(GroovyTokenTypes.ANNOTATION);
		
		/* test  */
		assertEquals("ANNOTATION",name);
	
	}
	
	@Test
	public void for_unknown_field_int_number_the_name_with_three_question_operators_is_returned() {
		/* prepare */
		GroovyTokenTypeDebugInfoInspector inspector = new GroovyTokenTypeDebugInfoInspector();
		
		/* execute */
		String name = inspector.getGroovyTokenTypeName(Integer.MAX_VALUE);
		
		/* test  */
		assertEquals("???",name);
	
	}

}
