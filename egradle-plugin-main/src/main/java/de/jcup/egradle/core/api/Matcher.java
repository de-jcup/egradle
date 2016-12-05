package de.jcup.egradle.core.api;

public interface Matcher<T> {

	/**
	 * Returns true when matcher matches
	 * @param toMatch
	 * @return <code>true</code> when matching
	 */
	public boolean matches(T toMatch) ;

}
