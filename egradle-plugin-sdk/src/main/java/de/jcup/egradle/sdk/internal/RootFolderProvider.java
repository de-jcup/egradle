package de.jcup.egradle.sdk.internal;

import java.io.File;
import java.io.IOException;

public interface RootFolderProvider{

	/**
	 * Resolves root folder
	 * @return root folder, <code>null</code> when root folder not found 
	 * @throws IOException
	 */
	public File getRootFolder() throws IOException;
}
