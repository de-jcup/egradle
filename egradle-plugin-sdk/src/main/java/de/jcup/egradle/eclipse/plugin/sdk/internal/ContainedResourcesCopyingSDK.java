package de.jcup.egradle.eclipse.plugin.sdk.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.jcup.egradle.eclipse.plugin.sdk.internal.util.FileUtil;

public class ContainedResourcesCopyingSDK extends AbstractSDK {

	private File targetFolder;
	private File internalFolder;

	/**
	 * Creates internal file based sdk
	 * @param sdkVersion - if <code>null</code> or blank then version "unknown" will be used instead
	 * @param internalFolder - may not be <code>null</code>
	 * @throws IllegalArgumentException when internal folder is <code>null</code>
	 */
	public ContainedResourcesCopyingSDK(String sdkVersion, File internalFolder) {
		super(sdkVersion);
		if (internalFolder==null){
			throw new IllegalArgumentException("internal folder may not be null!");
		}
		this.internalFolder=internalFolder;
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
		if (!internalFolder.exists()){
			throw new FileNotFoundException("Did not find:"+internalFolder.toString());
		}
		FileUtil.copyDirectories(internalFolder,targetFolder);
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
