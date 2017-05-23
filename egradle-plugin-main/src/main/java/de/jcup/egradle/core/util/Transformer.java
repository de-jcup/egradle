package de.jcup.egradle.core.util;

public interface Transformer<T> {

	/**
	 * Returns transformed source or source again
	 * @param source
	 * @return transformation result
	 */
	public T transform(T source);
}
