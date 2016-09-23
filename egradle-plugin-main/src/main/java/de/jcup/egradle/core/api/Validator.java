package de.jcup.egradle.core.api;

public interface Validator<T> {

	/**
	 * Validate given object
	 * @param object
	 * @throws ValidationException when not valid
	 */
	public void validate(T object) throws ValidationException;
	
}
