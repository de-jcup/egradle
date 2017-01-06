package de.jcup.egradle.eclipse;

import de.jcup.egradle.core.api.ErrorHandler;
import de.jcup.egradle.eclipse.api.EGradleUtil;

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

}
