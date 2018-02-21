package de.jcup.egradle.core.domain;

public class GradleProjectException extends Exception{

	private static final long serialVersionUID = -2755660376043277214L;


	public GradleProjectException(String message, Throwable cause) {
		super(message, cause);
	}

	public GradleProjectException(String message) {
		super(message);
	}

}
