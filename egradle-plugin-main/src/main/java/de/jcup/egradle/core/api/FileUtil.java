package de.jcup.egradle.core.api;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

public class FileUtil {

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
