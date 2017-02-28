package de.jcup.egradle.eclipse.gradleeditor.codeassist;


import de.jcup.egradle.codeassist.Proposal;

public class LazyCursorMovement {
	
	private Proposal proposal;

	public LazyCursorMovement(Proposal p){
		this.proposal=p;
	}

	public int getCursorMove() {
		int cursorMovement = -1;
		int proposedCursorPostion = proposal.getCursorPos();
		if (proposedCursorPostion == -1) {
			int length = proposal.getCode().length();
			cursorMovement = length;
		} else {
			cursorMovement = proposedCursorPostion;
		}
		return cursorMovement;
	}
}
