package de.jcup.egradle.library.access;

public class EGradleBuildScriptException extends Exception {

	public EGradleBuildScriptException(String message, Throwable cause) {
		super(message, cause);
	}

	public EGradleBuildScriptException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}