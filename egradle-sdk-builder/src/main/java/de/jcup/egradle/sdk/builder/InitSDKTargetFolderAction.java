package de.jcup.egradle.sdk.builder;

import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class InitSDKTargetFolderAction implements SDKBuilderAction{

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		/* delete old sdk */
		if (context.targetPathDirectory.exists()) {
			System.out.println(
					"Target directory exists - will be deleted before:" + context.targetPathDirectory.getCanonicalPath());
			FileUtils.deleteDirectory(context.targetPathDirectory);
		}
		context.targetPathDirectory.mkdirs();
		
	}

}
