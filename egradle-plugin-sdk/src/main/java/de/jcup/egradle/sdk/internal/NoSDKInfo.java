package de.jcup.egradle.sdk.internal;

import java.util.Date;

import de.jcup.egradle.sdk.SDKInfo;

public class NoSDKInfo implements SDKInfo{

	public static final NoSDKInfo INSTANCE = new NoSDKInfo();
	
	private NoSDKInfo() {
	}
	
	@Override
	public String getGradleVersion() {
		return "none";
	}

	@Override
	public String getSdkVersion() {
		return "Not installed";
	}

	@Override
	public Date getInstallationDate() {
		return null;
	}

	@Override
	public Date getCreationDate() {
		return null;
	}

}
