package de.jcup.egradle.eclipse.plugin.sdk;

import java.io.File;
import java.io.IOException;

public interface SDK {

	String getVersion();
	
	String getDescription();

	boolean isInstalled();

	void install() throws IOException;

	/**
	 * Returns dsl folder or <code>null</code>
	 * @return dsl folder or <code>null</code>
	 */
	File getSDKInstallationFolder();

}