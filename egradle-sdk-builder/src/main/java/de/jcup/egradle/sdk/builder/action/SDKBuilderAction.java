package de.jcup.egradle.sdk.builder.action;

import java.io.IOException;

import de.jcup.egradle.sdk.builder.SDKBuilderContext;

public interface SDKBuilderAction {

	public void execute(SDKBuilderContext context) throws IOException;
}
