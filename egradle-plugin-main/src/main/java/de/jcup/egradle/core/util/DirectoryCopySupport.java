/*
 * Copyright 2016 Albert Tregnaghi
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DirectoryCopySupport {

	/**
	 * Copy given src folder itself into dest folder<br>
	 * e.g. copy "testfolder" into "targetfolder", resuls in
	 * "targetfolder/testfolder/..."
	 * 
	 * @param sourceFolder
	 * @param destinationFolder
	 * @param overwriteExistingFiles
	 *            - when <code>true</code> existing files will be replaced by
	 *            new ones (but additional file will NOT be deleted!), if
	 *            <code>false</code> the origin file will be kept
	 * @throws IOException
	 */
	public void copyDirectories(File sourceFolder, File destinationFolder, boolean overwriteExistingFiles)
			throws IOException {
		copyDirectories(sourceFolder, destinationFolder, null, overwriteExistingFiles);
	}

	/**
	 * Copy given src folder itself into dest folder<br>
	 * e.g. copy "testfolder" into "targetfolder", resuls in
	 * "targetfolder/testfolder/..."
	 * 
	 * @param sourceFolder
	 * @param destinationFolder
	 * @param targetFileNameTransformer
	 * @param overwriteExistingFiles
	 *            - when <code>true</code> existing files will be replaced by
	 *            new ones (but additional file will NOT be deleted!), if
	 *            <code>false</code> the origin file will be kept
	 * @throws IOException
	 */
	public void copyDirectories(File sourceFolder, File destinationFolder,
			Transformer<String> targetFileNameTransformer, boolean overwriteExistingFiles) throws IOException {
		if (sourceFolder == null) {
			throw new IllegalArgumentException("src may not be null!");
		}
		if (!sourceFolder.exists()) {
			throw new FileNotFoundException("source folder does not exist:" + sourceFolder);
		}
		if (destinationFolder == null) {
			throw new IllegalArgumentException("dest may not be null!");
		}
		copyRecursive(sourceFolder, destinationFolder, targetFileNameTransformer, overwriteExistingFiles);
	}

	private void copyRecursive(File src, File dest, Transformer<String> targetFileNameTransformer,
			boolean overwriteExistingFiles) throws IOException {

		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdirs();
			}
			File sourceDirectory = src;
			File srcDirChildren[] = sourceDirectory.listFiles();

			for (File srcFile : srcDirChildren) {
				String name = srcFile.getName();
				if (targetFileNameTransformer != null) {
					String transformed = targetFileNameTransformer.transform(name);
					if (transformed != null) {
						name=transformed;
					}
				}
				File destFile = new File(dest, name);

				copyRecursive(srcFile, destFile, targetFileNameTransformer, overwriteExistingFiles);
			}
		} else {
			copyFile(src, dest, overwriteExistingFiles);
		}
	}

	private void copyFile(File src, File dest, boolean overwriteExistingFiles) throws IOException {
		if (dest.exists()) {
			if (!overwriteExistingFiles) {
				return;
			}
			if (!dest.delete()) {
				throw new IOException("Was not able to delete existing file:" + dest);
			}
		}
		try (InputStream in = new FileInputStream(src); OutputStream out = new FileOutputStream(dest)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
		}
	}
}
