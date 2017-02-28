package de.jcup.egradle.sdk.builder.action.mapping;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;

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
