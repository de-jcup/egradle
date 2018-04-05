package de.jcup.egradle.eclipse.ide;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

public class ProjectContext {

	private List<IProject> projects = new ArrayList<>();

	/**
	 * @return a list of projects - never <code>null</code>
	 */
	public List<IProject> getProjects() {
		return projects;
	}


}