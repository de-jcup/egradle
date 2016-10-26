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
 package de.jcup.egradle.eclipse.gradleeditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordPatternRule;

public class ExactWordPatternRule extends WordPatternRule{

	public ExactWordPatternRule(IWordDetector detector, String exactWord, IToken token) {
		super(detector, exactWord, null, token);
	}
	
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		/* sequence is not the word found by word detector but the start sequence!!!!! (in this case always the exact word)*/
		
		// -------------------------------------------------
		// example: exactWord='test' 
		// 
		// subjects: atest,test,testa
		//                   ^----------------only result!
		
		int column=scanner.getColumn();
		boolean wordHasPrefix;
		if (column==1){
			wordHasPrefix=false;
		}else{
			scanner.unread();
			scanner.unread();
			char charBefore =(char) scanner.read();
			scanner.read();
			wordHasPrefix =fDetector.isWordPart(charBefore);
		}
		if (wordHasPrefix){
			scanner.read(); // move one forward
			return false;
		}
		for (int i= 1; i < sequence.length; i++) {
			int c= scanner.read();
			if (c == ICharacterScanner.EOF){
				if (eofAllowed) {
					return true;
				}else{
					//return false;
				}
			} else if (c != sequence[i]) {
				// Non-matching character detected, rewind the scanner back to the start.
				// Do not unread the first character.
				scanner.unread();
				for (int j= i-1; j > 0; j--){
					scanner.unread();
				}
				return false;
			}
		}
		char charAfter = (char)scanner.read();
		scanner.unread();
		
		if (fDetector.isWordPart(charAfter)){
			/* the word is more than the exact one - e.g. instead of 'test' 'testx' ... so not correct*/
			return false;
		}
		return true;
	}

	
}
