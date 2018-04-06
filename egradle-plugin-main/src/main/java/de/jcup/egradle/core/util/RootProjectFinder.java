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
package de.jcup.egradle.core.util;

import java.io.File;
import java.io.FileFilter;

public class RootProjectFinder {

	private static SettingsFilter FILE_FILTER = new SettingsFilter();

	public File findRootProjectFolder(File file) {
		if (file == null) {
			return null;
		}
		if (!file.isDirectory()) {
			return scan(file.getParentFile());
		}
		return scan(file);
	}

	private File scan(File parentFolder) {
		if (parentFolder == null) {
			return null;
		}
		File[] result = parentFolder.listFiles(FILE_FILTER);
		if (result != null && result.length == 1) {
			return parentFolder;
		}
		return scan(parentFolder.getParentFile());
	}

	private static class SettingsFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			if (file == null) {
				return false;
			}
			if ("settings.gradle".equals(file.getName())) {
				return true;
			}
			return false;
		}

	}

}
