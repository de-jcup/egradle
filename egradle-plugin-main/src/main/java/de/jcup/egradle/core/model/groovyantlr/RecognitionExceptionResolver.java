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

import antlr.RecognitionException;
import antlr.TokenStreamRecognitionException;

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
