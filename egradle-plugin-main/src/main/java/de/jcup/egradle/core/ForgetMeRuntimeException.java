package de.jcup.egradle.core;

/**
 * Special exception. Can be used to throw so caller is informed about problem but the caller. But the problem is already logged or shown to user in UI. So caller has nothing more to do -just to forget
 * @author Albert Tregnaghi
 *
 */
public class ForgetMeRuntimeException extends RuntimeException implements ForgetMe{

	private static final long serialVersionUID = 1L;

	public ForgetMeRuntimeException(String message, Throwable alreadyHandledException) {
		super(message, alreadyHandledException);
	}
	public ForgetMeRuntimeException(String message) {
		super(message);
	}
	
	

	
}
