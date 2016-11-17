package de.jcup.egradle.core.model;


public interface ModelBuilder {

	/**
	 * Build outline model
	 * @param context - can be <code>null</code>
	 * @return outline model, never <code>null</code>
	 * @throws OutlineModelBuilderException
	 */
	Model build(BuildContext context) throws OutlineModelBuilderException;
	
	
	public class OutlineModelBuilderException extends Exception{

		private static final long serialVersionUID = 1L;

		public OutlineModelBuilderException(String message, Throwable cause) {
			super(message, cause);
		}


	}
}
