/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.egradle.sdk.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import de.jcup.egradle.core.VersionData;
import de.jcup.egradle.sdk.SDK;
import de.jcup.egradle.sdk.SDKInfo;

public abstract class AbstractSDK implements SDK{

	protected VersionData version;
	private SDKInfo info;
	private Object monitor = new Object();

	public AbstractSDK(VersionData sdkVersion) {
		if (sdkVersion==null){
			sdkVersion=VersionData.UNKNOWN;
		}
		this.version=sdkVersion;
	}
	
	@Override
	public final VersionData getVersion() {
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