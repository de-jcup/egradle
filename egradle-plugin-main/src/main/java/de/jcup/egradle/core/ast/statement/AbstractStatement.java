package de.jcup.egradle.core.ast.statement;

public abstract class AbstractStatement implements Statement {

	private boolean correctBuild;

	public boolean isCorrectBuild(){
		return correctBuild;
	}
	
	public void setCorrectBuild(boolean correctBuild) {
		this.correctBuild = correctBuild;
	}
	
}
