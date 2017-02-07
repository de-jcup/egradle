package de.jcup.egradle.core;

public interface TextProvider {

	public String getText();

	public String getText(int offset, int length) throws TextProviderException;

	public int getLineOffset(int offset) throws TextProviderException;
}
