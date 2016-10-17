package de.jcup.egradle.eclipse.api;

import org.eclipse.core.resources.IProjectDescription;

public interface ProjectDescriptionCreator {

	IProjectDescription createNewProjectDescription(String projectName);

}
