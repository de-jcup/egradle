package de.jcup.egradle.core;

public class ExceptionUtils {

	public static Throwable getRootCause(Throwable t) {
		if (t == null) {
			return null;
		}
		Throwable rootCause = t;
		while (t.getCause() != null) {
			rootCause = t.getCause();
		}
		return rootCause;
	}
}
