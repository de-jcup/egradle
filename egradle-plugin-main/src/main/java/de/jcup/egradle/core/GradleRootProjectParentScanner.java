package de.jcup.egradle.core;

import java.io.File;
import java.io.FilenameFilter;

public class GradleRootProjectParentScanner {

	private static final FilenameFilter GRADLE_BUILD_FILE_FILTER = new GradleBuildFileFilter();

	/**
	 * Scans for parent root project - means last folder from given start file where a "build.gradle" file is reachable.
	 * @param start folder or file to start from
	 * @param deepness when <code>-1</code> is given this means infinite search
	 * @return root project folder or <code>null</code>
	 */
	public File scanForParentRootProject(File start, int deepness) {
		if (start == null) {
			return null;
		}
		if (!start.exists()) {
			return null;
		}
		File currentDir = null;
		if (start.isDirectory()) {
			currentDir = start;
		} else {
			currentDir = start.getParentFile();
		}

		int dive = 0;
		File result = scanForLastParentFolderContainingABuildGradleFile(currentDir, deepness, dive);
		return result;
	}

	private File scanForLastParentFolderContainingABuildGradleFile(File directory, int deepness, int dive) {
		if (directory == null) {
			return null;
		}
		if (deepness > -1 && dive > deepness) {
			return null;
		}
		File lastPotentialRootProject = null;
		File[] buildFiles = directory.listFiles(GRADLE_BUILD_FILE_FILTER);
		if (buildFiles != null && buildFiles.length == 1) {
			/* mark this directory as last potential root project */
			lastPotentialRootProject = directory;
		}
		File result = scanForLastParentFolderContainingABuildGradleFile(directory.getParentFile(), deepness, dive+1);
		if (result == null) {
			return lastPotentialRootProject;
		}
		return result;
	}

	private static final class GradleBuildFileFilter implements FilenameFilter {

		private static final String BUILD_GRADLE = "build.gradle";

		@Override
		public boolean accept(File dir, String name) {
			if (BUILD_GRADLE.equals(name)) {
				return true;
			}
			return false;
		}
	}

}
