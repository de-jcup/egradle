package de.jcup.egradle.core;

import java.io.File;
import java.io.IOException;

public interface RootFolderCopySupport {

	/**
	 * Copies defined folder with defined version to given 
	 * @param rootFolderProvider provides root folder where source data exists
	 * @return <code>true</code> when copying was possible
	 * @throws IOException
	 */
	boolean copyFrom(RootFolderProvider rootFolderProvider) throws IOException;

	boolean isTargetFolderExisting();

	File getTargetFolder();

}