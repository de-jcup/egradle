package de.jcup.egradle.eclipse.ide;

import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.eclipse.preferences.EGradleCallType;

public interface EGradleProjectPropertyPageDataSupport {

	public EGradleCallType getCallType();

	public String getRootProjectPath();

	public String getJavaHomePath();

	public String getGradleCallCommand();

	public String getGradleBinInstallFolder();

	public EGradleShellType getShellType();
}
