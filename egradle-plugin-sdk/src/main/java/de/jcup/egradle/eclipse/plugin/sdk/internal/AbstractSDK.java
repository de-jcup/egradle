package de.jcup.egradle.eclipse.plugin.sdk.internal;

import de.jcup.egradle.eclipse.plugin.sdk.SDK;

public abstract class AbstractSDK implements SDK{

	protected String version;
	private String description;

	public AbstractSDK(String sdkVersion) {
		if (sdkVersion==null || sdkVersion.trim().length()==0){
			sdkVersion="unknown";
		}
		this.version=sdkVersion;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public final String getVersion() {
		return version;
	}

}