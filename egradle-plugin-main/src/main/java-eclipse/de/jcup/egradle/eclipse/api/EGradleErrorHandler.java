package de.jcup.egradle.eclipse.api;

import de.jcup.egradle.core.api.ErrorHandler;

public class EGradleErrorHandler implements ErrorHandler{

	public static EGradleErrorHandler INSTANCE = new EGradleErrorHandler();
	
	EGradleErrorHandler(){
		
	}
	
	@Override
	public void handleError(Throwable t) {
		EGradleUtil.log(t);
	}

	@Override
	public void handleError(String message, Throwable t) {
		EGradleUtil.log(message, t);
	}

	@Override
	public void handleError(String message) {
		EGradleUtil.log(message, null);		
	}

}
