package de.jcup.egradle.sdk.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import de.jcup.egradle.sdk.SDK;
import de.jcup.egradle.sdk.SDKInfo;

public abstract class AbstractSDK implements SDK{

	protected String version;
	private SDKInfo info;
	private Object monitor = new Object();

	public AbstractSDK(String sdkVersion) {
		if (sdkVersion==null || sdkVersion.trim().length()==0){
			sdkVersion="unknown";
		}
		this.version=sdkVersion;
	}
	
	@Override
	public final String getVersion() {
		return version;
	}
	
	@Override
	public SDKInfo getInfo() {
		synchronized (monitor) {
			if (info==null){
				info=loadInfo();
			}
		}
		return info;
	}

	private SDKInfo loadInfo() {
		File sdkInstallationFolder = getSDKInstallationFolder();
		if (sdkInstallationFolder==null || !sdkInstallationFolder.exists()){
			return NoSDKInfo.INSTANCE;
		}
		XMLSDKInfoImporter importer = new XMLSDKInfoImporter();
		
		File file = new File(sdkInstallationFolder,SDKInfo.FILENAME);
		try(FileInputStream stream = new FileInputStream(file)){
			XMLSDKInfo loaded = importer.importSDKInfo(stream);
			return loaded;
		}catch(IOException e){
			return NoSDKInfo.INSTANCE;
		}
	}

}