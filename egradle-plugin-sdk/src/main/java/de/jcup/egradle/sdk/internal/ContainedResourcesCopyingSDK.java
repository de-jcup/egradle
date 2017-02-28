package de.jcup.egradle.sdk.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class ContainedResourcesCopyingSDK extends AbstractSDK {

	private File targetFolder;
	private RootFolderProvider rootFolderProvider;
	private LogAdapter logAdapter;

	/**
	 * Creates internal file based sdk
	 * @param sdkVersion - if <code>null</code> or blank then version "unknown" will be used instead
	 * @param rootFolderProvider - may not be <code>null</code>
	 * @param logAdapter can be <code>null</code>
	 * @throws IllegalArgumentException when rootFolderProvider is <code>null</code>
	 */
	public ContainedResourcesCopyingSDK(String sdkVersion, RootFolderProvider rootFolderProvider, LogAdapter logAdapter) {
		super(sdkVersion);
		if (rootFolderProvider==null){
			throw new IllegalArgumentException("root folder provider may not be null!");
		}
		this.rootFolderProvider=rootFolderProvider;
		this.logAdapter=logAdapter;
		
		String userHome = System.getProperty("user.home");
		File egradleRoot = new File(userHome,".egradle");
		targetFolder=new File(egradleRoot,"sdk/"+sdkVersion);
	}

	@Override
	public boolean isInstalled() {
		return targetFolder.exists();
	}
	

	@Override
	public void install() throws IOException {
		File internalFolder = rootFolderProvider.getRootFolder();
		if (internalFolder==null){
			/* has to be already logged by root folder provider*/
			return;
		}
		if (!internalFolder.exists()){
			throw new FileNotFoundException("Did not find:"+internalFolder.toString());
		}
		FileUtil.copyDirectories(internalFolder,targetFolder);
		
		File sdkInfoFile = new File(targetFolder,"sdk.xml");
		XMLSDKInfo sdkInfo = null;
		if (sdkInfoFile.exists()){
			XMLSDKInfoImporter importer = new XMLSDKInfoImporter();
			try(FileInputStream fis = new FileInputStream(sdkInfoFile)){
				sdkInfo = importer.importSDKInfo(fis);
			}
		}else{
			sdkInfo=new XMLSDKInfo();
			sdkInfoFile.createNewFile();
		}
		sdkInfo.setInstallationDate(new Date());
		sdkInfo.setSdkVersion(version);
		try(OutputStream out = new FileOutputStream(sdkInfoFile)){
			XMLSDKInfoExporter exporter = new XMLSDKInfoExporter();
			exporter.exportSDKInfo(sdkInfo, out);
		}
		if (logAdapter!=null){
			logAdapter.logInfo("Successfully installed SDK "+getVersion());
		}
				
	}

	@Override
	public File getSDKInstallationFolder() {
		return targetFolder;
	}

	@Override
	public String toString() {
		return "ContainedResourcesCopyingSDK [version=" + version + "]";
	}

}
