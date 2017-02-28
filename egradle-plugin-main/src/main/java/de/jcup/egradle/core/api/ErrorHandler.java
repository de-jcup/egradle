package de.jcup.egradle.core.api;

public interface ErrorHandler {
	
	public static final ErrorHandler IGNORE_ERRORS = new IgnoreErrorHandler();

	public void handleError(Throwable t);
	
	public void handleError(String message);
	
	public void handleError(String message, Throwable t);

	public static class IgnoreErrorHandler implements ErrorHandler{
		
		private IgnoreErrorHandler(){
			
		}

		@Override
		public void handleError(Throwable t) {
			
		}

		@Override
		public void handleError(String message) {
			
		}

		@Override
		public void handleError(String message, Throwable t) {
			
		}
	}
}
