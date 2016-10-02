package de.jcup.egradle.eclipse.execution;

public class GradleExecutionException extends Exception{
	private static final long serialVersionUID = 1L;

	public GradleExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public GradleExecutionException(String message) {
		super(message);
	}


}
