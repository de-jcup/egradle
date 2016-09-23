package de.jcup.egradle.core.api;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.config.GradleConfiguration;

public class FileUtil {

	/**
	 * Creates a gradle command full path - if gradle binary folder is set
	 * @param config
	 * @return path
	 */
	public static final String createGradleCommandFullPath(GradleConfiguration config){
		return createCorrectFilePath(config.getGradleBinDirectory(), config.getGradleCommand());
	}
	public static final String createCorrectFilePath(String folderName, String filename){
		String fileTrim = filename.trim();
		if (StringUtils.isEmpty(folderName)){
			return fileTrim;
		}
		String folderTrim = folderName.trim();
		File asFile = new File(folderTrim,fileTrim);
		return asFile.getAbsolutePath();
	}
}
