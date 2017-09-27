package de.jcup.egradle.core.util;

public interface ILogSupport {

	void logInfo(String info);

	void logWarning(String warning);

	void logError(String error, Throwable t);

}