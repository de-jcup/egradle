package de.jcup.egradle.sdk.builder;

import java.io.IOException;

public interface SDKBuilderAction {

	public void execute(SDKBuilderContext context) throws IOException;
}
