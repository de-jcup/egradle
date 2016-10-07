package de.jcup.egradle.eclipse.virtualroot;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * Nature for virtual root projects
 * @author Albert Tregnaghi
 *
 */
public class VirtualRootProjectNature implements IProjectNature {

	/**
	 * ID of this project nature
	 */
	public static final String NATURE_ID = "de.jcup.egradle.eclipse.virtualRootProjectNature";

	private IProject project;

	@Override
	public void configure() throws CoreException {
		// IProjectDescription desc = project.getDescription();
		// customize...
		// project.setDescription(desc, null);
	}

	@Override
	public void deconfigure() throws CoreException {
		// remove customization...
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

}
