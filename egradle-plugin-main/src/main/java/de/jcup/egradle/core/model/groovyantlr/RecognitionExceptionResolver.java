package de.jcup.egradle.core.model.groovyantlr;

import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamRecognitionException;

public class RecognitionExceptionResolver {
	
	private static final RecognitionExceptionResolver SHARED_INSTANCE = new RecognitionExceptionResolver();

	public static RecognitionExceptionResolver getSharedInstance() {
		return SHARED_INSTANCE;
	}
	
	public RecognitionException resolveRecognitionException(Throwable t) {
		if (t==null){
			return null;
		}
		if (t instanceof RecognitionException){
			return (RecognitionException) t;
		}
		if (t instanceof TokenStreamRecognitionException){
			TokenStreamRecognitionException rex =(TokenStreamRecognitionException) t;
			return rex.recog;
			
		}
		Throwable cause = t.getCause();
		return resolveRecognitionException(cause);
	}
}
