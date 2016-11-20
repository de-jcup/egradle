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
 package de.jcup.egradle.core.token;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class MultiTokenTypeAnalyzerImplTest {

	private MultiTokenTypeAnalyzer analyzerToTest;

	@Before
	public void before() {
		analyzerToTest = new MultiTokenTypeAnalyzer();
	}

	@Test
	public void empty_multi_token_anylzer_returns_null_for_null() {
		assertNull(analyzerToTest.analyze(null));
	}

	@Test
	public void empty_multi_token_anylzer_returns_null_for_empty_token() {
		assertNull(analyzerToTest.analyze(new TokenImpl(1)));
	}

	@Test
	public void multi_token_anylzer_returns_result_of_first_added_analyzer_which_can_handle_empty_token_on_empty_token() {
		/* prepare*/
		TokenType expectedType = TokenType.TASK;
		TokenType unExpectedType = TokenType.COMMENT__MULTI_LINE;

		TokenTypeAnalyzer analyzer1 = mock(TokenTypeAnalyzer.class);
		TokenTypeAnalyzer analyzer2 = mock(TokenTypeAnalyzer.class);
		when(analyzer2.analyze(any())).thenReturn(expectedType);
		analyzerToTest.add(analyzer1);
		analyzerToTest.add(analyzer2);
		/* add multiple other analyzers*/
		for (int i = 0; i < 20; i++) {
			TokenTypeAnalyzer otherAnalyzer = mock(TokenTypeAnalyzer.class);
			when(otherAnalyzer.analyze(any())).thenReturn(unExpectedType);
			analyzerToTest.add(otherAnalyzer);
		}
		/* execute */
		TokenType resultType = analyzerToTest.analyze(new TokenImpl(1));
		
		/* test */
		assertNotNull(resultType);
		assertEquals(expectedType, resultType);
	}

}
