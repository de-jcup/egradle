package de.jcup.egradle.core.virtualroot;

import java.io.File;

public interface RootProjectVisitor {
	public Object createOrRecreateProject(String projectName) throws VirtualRootProjectException;

	public void createLink(Object targetParentFolder, File file) throws VirtualRootProjectException;

	public boolean needsFolderToBeCreated(Object targetParentFolder, File file) throws VirtualRootProjectException;
	
}