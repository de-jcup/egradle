package de.jcup.egradle.core;

import static org.apache.commons.lang3.Validate.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.jcup.egradle.core.util.FileUtil;
import de.jcup.egradle.core.util.LogAdapter;

/**
 * Support class which is able to copy files from given root folder to a path inside EGradle user home.
 * Is also able to check if the target folder already exists - so caller can decide to override or to ignore
 * @author Albert Tregnaghi
 *
 */
public class VersionedFolderToUserHomeCopySupport implements CopySupport {

	private File targetFolder;

	public VersionedFolderToUserHomeCopySupport(String path, VersionData versionData, LogAdapter logAdapter) {
		notNull(path, "'pathFromEGradleUserHome' may not be null");
		notNull(versionData, "'version' may not be null");
		
		String userHome = System.getProperty("user.home");
		File egradleRoot = new File(userHome,".egradle");
		File mainPath = new File(egradleRoot,path);
		
		targetFolder = new File(mainPath,versionData.getAsText());
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.CopySupport#copyFrom(de.jcup.egradle.template.RootFolderProvider)
	 */
	@Override
	public boolean copyFrom(RootFolderProvider rootFolderProvider) throws IOException {
		File internalFolder = rootFolderProvider.getRootFolder();
		if (internalFolder==null){
			/* has to be already logged by root folder provider*/
			return false;
		}
		if (!internalFolder.exists()){
			throw new FileNotFoundException("Did not find:"+internalFolder.toString());
		}
		FileUtil.copyDirectories(internalFolder,targetFolder);
		return true;
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.CopySupport#isTargetFolderExisting()
	 */
	@Override
	public boolean isTargetFolderExisting() {
		return targetFolder.exists();
	}

	/* (non-Javadoc)
	 * @see de.jcup.egradle.core.CopySupport#getTargetFolder()
	 */
	@Override
	public File getTargetFolder() {
		return targetFolder;
	}
	
	
}
