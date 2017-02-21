package de.jcup.egradle.sdk.builder;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Copies API mappings from gradle source folders to res/main
 * @author Albert Tregnaghi
 *
 */
public class CopyApiMappingsAction implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		/* copy origin mapping file to target directory */
		File targetPathDirectory = context.targetPathDirectory;
		FileUtils.copyFile(context.gradleOriginMappingFile,
				new File(targetPathDirectory, context.gradleOriginMappingFile.getName()));
	}

}
