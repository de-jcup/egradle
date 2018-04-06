/*
 * Copyright 2017 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.eclipse.ide;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;

import de.jcup.egradle.core.util.DirectoryCopySupport;
import de.jcup.egradle.core.util.FileSupport;
import de.jcup.egradle.eclipse.util.EclipseDevelopmentSettings;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class ProjectMetaDataCacheSupport {
	private FileSupport fileSupport = new FileSupport();

	public ProjectCacheData buildMetaDataCache(List<IProject> projects) throws IOException {
		ProjectCacheData data = new ProjectCacheData();

		for (IProject project : projects) {
			data.cache(project);
		}
		return data;
	}

	/**
	 * Restores meta data from cache
	 * 
	 * @param cache
	 * @param projects
	 * @throws IOException
	 */
	public void restoreMetaData(ProjectCacheData cache, List<IProject> projects) throws IOException {
		if (cache == null) {
			return;
		}
		for (IProject project : projects) {
			cache.restore(project);
		}
	}

	private File resolvePathToProjectCoreResourceMetaData() {
		File rootFolder = fetchWorkspaceRootFolder();
		if (rootFolder == null) {
			return null;
		}
		File projectsMetaData = new File(rootFolder, ".metadata/.plugins/org.eclipse.core.resources/.projects");
		if (!projectsMetaData.exists()) {
			return null;
		}
		return projectsMetaData;
	}

	private File resolveProjectCoreResourceMetaDataFolder(File projectCoreResourceMetaDataFolder, IProject project) {
		if (project == null) {
			return null;
		}
		if (projectCoreResourceMetaDataFolder == null) {
			return null;
		}
		if (!projectCoreResourceMetaDataFolder.exists()) {
			return null;
		}
		String projectName = project.getName();
		File expectedSourceDir = new File(projectCoreResourceMetaDataFolder, projectName);
		return expectedSourceDir;
	}

	private File fetchWorkspaceRootFolder() {
		IWorkspace workspace = EclipseUtil.getWorkspace();
		IPath rootlocation = workspace.getRoot().getLocation();
		if (rootlocation == null) {
			return null;
		}
		File rootFolder = rootlocation.toFile();
		return rootFolder;
	}

	public class ProjectCacheData {

		private class CacheInfo {

			public File originProjectSubFolder;
			public File cacheProjectSubFolder;

		}

		private Map<IPath, CacheInfo> map = new HashMap<>();
		private File cacheFolder;
		private File projectCoreResourceMetaDataFolder;
		private DirectoryCopySupport dirCopySupport = new DirectoryCopySupport();

		public ProjectCacheData() {
			projectCoreResourceMetaDataFolder = resolvePathToProjectCoreResourceMetaData();
		}

		public void drop() {
			if (cacheFolder != null && cacheFolder.exists()) {
				try {
					fileSupport.delete(cacheFolder);
				} catch (IOException e) {
					IDEUtil.logWarning("Was not able to delete cache folder:" + cacheFolder);
				}
			}
		}

		/**
		 * Restores meta data from cache. But will NOT override existing files
		 * in target!
		 * 
		 * @param project
		 * @throws IOException
		 */
		public void restore(IProject project) throws IOException {
			if (project == null) {
				return;
			}
			IPath path = project.getFullPath();
			CacheInfo info = map.get(path);
			if (info == null) {
				return;
			}
			File from = info.cacheProjectSubFolder;
			File to = info.originProjectSubFolder;
			dirCopySupport.copyDirectories(from, to, false);
			if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_LOGGING) {
				IDEUtil.logInfo("Copied back from cache:" + from + " to " + to);
			}
		}

		public void cache(IProject project) throws IOException {
			File originProjectSubFolder = resolveProjectCoreResourceMetaDataFolder(projectCoreResourceMetaDataFolder,
					project);

			if (originProjectSubFolder == null) {
				return;
			}
			if (!originProjectSubFolder.exists()) {
				return;
			}
			IPath path = project.getFullPath();

			CacheInfo info = new CacheInfo();
			info.originProjectSubFolder = originProjectSubFolder;
			info.cacheProjectSubFolder = resolveProjectCoreResourceMetaDataFolder(getCacheFolder(), project);

			File from = info.originProjectSubFolder;
			File to = info.cacheProjectSubFolder;
			dirCopySupport.copyDirectories(from, to, false);
			if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_LOGGING) {
				IDEUtil.logInfo("Copied for cache from " + from + " to " + to);
			}
			map.put(path, info);
		}

		private File getCacheFolder() throws IOException {
			if (cacheFolder == null) {
				Path tempPath = Files.createTempDirectory("egradle_project_metacache");
				cacheFolder = tempPath.toFile();
				if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_LOGGING) {
					IDEUtil.logInfo("Created cache folder:" + cacheFolder);
				}
			}
			return cacheFolder;
		}
	}
}
