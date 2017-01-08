package de.jcup.egradle.core.api;

public interface ErrorHandler {

	public void handleError(Throwable t);
	
	public void handleError(String message);
	
	public void handleError(String message, Throwable t);
	
}
