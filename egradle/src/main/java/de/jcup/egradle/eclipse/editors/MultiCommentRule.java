package de.jcup.egradle.eclipse.editors;

import org.eclipse.jface.text.rules.*;

public class MultiCommentRule extends MultiLineRule {

	public MultiCommentRule(IToken token) {
		super("/*", "*/", token);
	}
	
	@Override
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		int c = scanner.read();
		if (sequence[0] == '/') {
			if (sequence.length==1){
				scanner.unread();
				return false;
			}
			if(sequence[1]!='*'){
				scanner.unread();
				return false;
			}
//			if (c == '?') {
//				// processing instruction - abort
//				scanner.unread();
//				return false;
//			}
//			if (c == '!') {
//				scanner.unread();
//				// comment - abort
//				return false;
//			}
		} else if (sequence[0] == '*') {
			if (sequence.length==1){
				scanner.unread();
			}
			if(sequence[1]!='/'){
				scanner.unread();
			}
		}
		return super.sequenceDetected(scanner, sequence, eofAllowed);
	}
}
