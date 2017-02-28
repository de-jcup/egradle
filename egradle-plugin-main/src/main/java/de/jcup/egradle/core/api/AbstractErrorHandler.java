package de.jcup.egradle.core.api;

public abstract class AbstractErrorHandler implements ErrorHandler{

	@Override
	public void handleError(Throwable t) {
		handleError(null,t);
	}

	@Override
	public void handleError(String message) {
		handleError(message,null);
	}

}
