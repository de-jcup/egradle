package de.jcup.egradle.eclipse.ide;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.core.util.GradleInfoSupport;
import de.jcup.egradle.eclipse.preferences.EGradleCallType;
import de.jcup.egradle.eclipse.util.EclipseResourceHelper;

public class PersistedEGradleProjectPropertyPageDataSupport implements EGradleProjectPropertyPageDataSupport {

	private GradleInfoSupport gradleInfoSupport;
	private EclipseResourceHelper resourceSupport;
	private String rootProjectPath;

	public PersistedEGradleProjectPropertyPageDataSupport(){
		this.gradleInfoSupport = GradleInfoSupport.DEFAULT;
		this.resourceSupport = EclipseResourceHelper.DEFAULT;
	}
	
	@Override
	public EGradleCallType getCallType() {
		/* FIXME ATR, 26.06.2017:  implement! */
		return null;
	}

	@Override
	public String getRootProjectPath() {
		return rootProjectPath;
	}

	@Override
	public String getJavaHomePath() {
		/* FIXME ATR, 26.06.2017:  implement! */
		return null;
	}

	@Override
	public String getGradleCallCommand() {
		/* FIXME ATR, 26.06.2017:  implement! */
		return null;
	}

	@Override
	public String getGradleBinInstallFolder() {
		/* FIXME ATR, 26.06.2017:  implement! */
		return null;
	}

	@Override
	public EGradleShellType getShellType() {
		/* FIXME ATR, 26.06.2017:  implement! */
		return null;
	}

	public void setProject(IProject project) {
		/* reset */
		rootProjectPath=null;
		
		if (project==null){
			return;
		}
		File folder;
		try {
			folder = resourceSupport.toFile(project);
			File rootProjectFile = gradleInfoSupport.resolveGradleRootProjectFolder(folder);
			rootProjectPath=rootProjectFile.getAbsolutePath();
		} catch (CoreException e) {
			IDEUtil.logError("was not able to resolve project:"+project, e);
		}
		
	}


}
