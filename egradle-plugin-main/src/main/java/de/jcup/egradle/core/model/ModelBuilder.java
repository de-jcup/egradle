package de.jcup.egradle.core.model;


public interface ModelBuilder {

	/**
	 * Build outline model
	 * @param context - can be <code>null</code>
	 * @return outline model, never <code>null</code>
	 * @throws ModelBuilderException
	 */
	Model build(BuildContext context) throws ModelBuilderException;
	
	
	public class ModelBuilderException extends Exception{

		private static final long serialVersionUID = 1L;

		public ModelBuilderException(String message, Throwable cause) {
			super(message, cause);
		}


	}
}
