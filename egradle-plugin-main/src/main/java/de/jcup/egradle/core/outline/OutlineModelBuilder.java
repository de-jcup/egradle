package de.jcup.egradle.core.outline;


public interface OutlineModelBuilder {

	OutlineModel build() throws OutlineModelBuilderException;
	
	
	public class OutlineModelBuilderException extends Exception{

		public OutlineModelBuilderException(String message, Throwable cause) {
			super(message, cause);
		}

		private static final long serialVersionUID = 1L;
		
	}
}
