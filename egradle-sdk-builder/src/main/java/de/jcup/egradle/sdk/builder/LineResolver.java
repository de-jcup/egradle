package de.jcup.egradle.sdk.builder;

import java.io.IOException;

public interface  LineResolver{
	String getNextLine() throws IOException;
}