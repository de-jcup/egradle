package de.jcup.egradle.eclipse.execution.validation;

public interface ValidationObserver{
	public void handleValidationRunning(boolean running);

	public void handleValidationResult(boolean b);
	
}