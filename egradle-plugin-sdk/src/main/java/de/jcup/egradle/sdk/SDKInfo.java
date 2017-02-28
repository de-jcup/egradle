package de.jcup.egradle.sdk;

import java.util.Date;

public interface SDKInfo {
	
	public static final String FILENAME="sdk.xml";

	public String getGradleVersion();

	public String getSdkVersion();

	/**
	 * Return local installation date of SDK 
	 * @return date or <code>null</code>
	 */
	public Date getInstallationDate();

	/**
	 * Return creation date of SDK 
	 * @return date or <code>null</code>
	 */
	public Date getCreationDate();
	
	
}
