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
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import antlr.RecognitionException;
import antlr.TokenStreamRecognitionException;


public class RecognitionExceptionResolverTest {
	RecognitionExceptionResolver resolverToTest;

	@Before
	public void before(){
		resolverToTest=new RecognitionExceptionResolver();
	}
	
	@Test
	public void call_with_null__returns_null() {
		assertNull(resolverToTest.resolveRecognitionException(null));
	}
	

	@Test
	public void call_with_recogntionException__returns_it_again() {
		RecognitionException reco = mock(RecognitionException.class);
		assertEquals(reco, resolverToTest.resolveRecognitionException(reco));
	}
	
	@Test
	public void call_with_tokenstream_with_recognitonException__returns_contained_recogition() {
		RecognitionException reco = mock(RecognitionException.class);
		TokenStreamRecognitionException ex = new TokenStreamRecognitionException(reco);
		assertEquals(reco, resolverToTest.resolveRecognitionException(ex));
	}
	

}
