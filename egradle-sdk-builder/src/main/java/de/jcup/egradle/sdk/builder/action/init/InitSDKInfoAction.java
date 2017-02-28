package de.jcup.egradle.sdk.builder.action.init;

import java.io.FileOutputStream;
import java.io.IOException;

import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;
import de.jcup.egradle.sdk.internal.XMLSDKInfoExporter;

public class InitSDKInfoAction implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {

		try (FileOutputStream stream = new FileOutputStream(context.sdkInfoFile)) {

			XMLSDKInfoExporter exporter = new XMLSDKInfoExporter();
			exporter.exportSDKInfo(context.sdkInfo, stream);
			System.out.println("- written sdk info file:" + context.sdkInfoFile);
		}
	}

}
