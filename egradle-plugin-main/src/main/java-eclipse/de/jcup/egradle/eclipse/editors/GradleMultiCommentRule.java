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
package de.jcup.egradle.eclipse.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

public class GradleMultiCommentRule extends MultiLineRule {

	public GradleMultiCommentRule(IToken token) {
		super("/*", "*/", token);
	}

	@Override
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		scanner.read();
		if (sequence[0] == '/') {
			if (sequence.length == 1) {
				scanner.unread();
				return false;
			}
			if (sequence[1] != '*') {
				scanner.unread();
				return false;
			}
		} else if (sequence[0] == '*') {
			if (sequence.length == 1) {
				scanner.unread();
			}
			if (sequence[1] != '/') {
				scanner.unread();
			}
		}
		return super.sequenceDetected(scanner, sequence, eofAllowed);
	}
}
