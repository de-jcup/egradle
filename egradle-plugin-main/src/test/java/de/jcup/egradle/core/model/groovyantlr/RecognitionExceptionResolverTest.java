package de.jcup.egradle.core.model.groovyantlr;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.model.groovyantlr.RecognitionExceptionResolver;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamRecognitionException;

import static org.mockito.Mockito.*;

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
