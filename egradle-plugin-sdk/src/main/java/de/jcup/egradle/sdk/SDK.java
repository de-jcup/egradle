package de.jcup.egradle.sdk;

import java.io.File;
import java.io.IOException;

public interface SDK {

	String getVersion();
	
	boolean isInstalled();

	void install() throws IOException;

	/**
	 * Returns dsl folder or <code>null</code>
	 * @return dsl folder or <code>null</code>
	 */
	File getSDKInstallationFolder();
	
	/**
	 * Returns sdk info object , never <code>null</code>
	 * @return sdk info object , never <code>null</code>
	 */
	SDKInfo getInfo();

}