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
 package de.jcup.egradle.eclipse.api;

import static org.apache.commons.lang3.Validate.notNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;

import de.jcup.egradle.core.virtualroot.VirtualRootProjectException;

public class FileHelper {

	public static FileHelper SHARED= new FileHelper();
	private final int MAX_RETRY = 5;
	private final IProgressMonitor NULL_MONITOR = new NullProgressMonitor();

	private byte[] buffer = new byte[8192];
	
	public void createTextFile(File parentFolder, String fileName, String content) throws VirtualRootProjectException {
		File gitIgnore = new File(parentFolder, fileName);
		try (FileOutputStream fileOutputStram = new FileOutputStream(gitIgnore);
				OutputStreamWriter w = new OutputStreamWriter(fileOutputStram, "UTF-8")) {
			gitIgnore.createNewFile();
			w.write(content);
		} catch (Exception e) {
			throw new VirtualRootProjectException("Cannot create virtual root content", e);
		}
	}

	/**
	 * Unzips the given zip file to the given destination directory extracting
	 * only those entries the pass through the given filter.
	 *
	 * @param zipFile
	 *            the zip file to unzip
	 * @param dstDir
	 *            the destination directory
	 * @throws IOException
	 *             in case of problem
	 */
	public void unzip(ZipFile zipFile, File dstDir) throws IOException {

		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		try {
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				String entryName = entry.getName();
				File file = new File(dstDir, changeSeparator(entryName, '/', File.separatorChar));
				file.getParentFile().mkdirs();
				InputStream src = null;
				OutputStream dst = null;
				try {
					src = zipFile.getInputStream(entry);
					dst = new FileOutputStream(file);
					transferData(src, dst);
				} finally {
					if (dst != null) {
						try {
							dst.close();
						} catch (IOException e) {
						}
					}
					if (src != null) {
						try {
							src.close();
						} catch (IOException e) {
						}
					}
				}
			}
		} finally {
			try {
				zipFile.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Returns the given file path with its separator character changed from the
	 * given old separator to the given new separator.
	 *
	 * @param path
	 *            a file path
	 * @param oldSeparator
	 *            a path separator character
	 * @param newSeparator
	 *            a path separator character
	 * @return the file path with its separator character changed from the given
	 *         old separator to the given new separator
	 */
	public String changeSeparator(String path, char oldSeparator, char newSeparator) {
		return path.replace(oldSeparator, newSeparator);
	}

	/**
	 * Copies all bytes in the given source file to the given destination file.
	 *
	 * @param source
	 *            the given source file
	 * @param destination
	 *            the given destination file
	 * @throws IOException
	 *             in case of error
	 */
	public void transferData(File source, File destination) throws IOException {
		destination.getParentFile().mkdirs();
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(destination);
			transferData(is, os);
		} finally {
			if (os != null)
				os.close();
			if (is != null)
				is.close();
		}
	}

	/**
	 * Copies all bytes in the given source stream to the given destination
	 * stream. Neither streams are closed.
	 *
	 * @param source
	 *            the given source stream
	 * @param destination
	 *            the given destination stream
	 * @throws IOException
	 *             in case of error
	 */
	public void transferData(InputStream source, OutputStream destination) throws IOException {
		int bytesRead = 0;
		while (bytesRead != -1) {
			bytesRead = source.read(buffer, 0, buffer.length);
			if (bytesRead != -1) {
				destination.write(buffer, 0, bytesRead);
			}
		}
	}

	/**
	 * Copies the given source file to the given destination file.
	 *
	 * @param src
	 *            the given source file
	 * @param dst
	 *            the given destination file
	 * @throws IOException
	 *             in case of error
	 */
	public void copy(File src, File dst) throws IOException {
		if (src.isDirectory()) {
			String[] srcChildren = src.list();
			for (int i = 0; i < srcChildren.length; ++i) {
				File srcChild = new File(src, srcChildren[i]);
				File dstChild = new File(dst, srcChildren[i]);
				copy(srcChild, dstChild);
			}
		} else
			transferData(src, dst);
	}

	public File getFileInPlugin(Plugin plugin, IPath path) throws CoreException {
		try {
			URL installURL = plugin.getBundle().getEntry(path.toString());
			URL localURL = FileLocator.toFileURL(installURL);
			return new File(localURL.getFile());
		} catch (IOException e) {
			throw new PathException(IStatus.ERROR, path, "cannot get file in plugin", e);
		}
	}

	public File createTempFileInPlugin(Plugin plugin, IPath path) {
		IPath stateLocation = plugin.getStateLocation();
		stateLocation = stateLocation.append(path);
		return stateLocation.toFile();
	}

	public StringBuffer read(String fileName) throws IOException {
		return read(new FileReader(fileName));
	}

	public StringBuffer read(Reader reader) throws IOException {
		StringBuffer s = new StringBuffer();
		try {
			char[] charBuffer = new char[8196];
			int chars = reader.read(charBuffer);
			while (chars != -1) {
				s.append(charBuffer, 0, chars);
				chars = reader.read(charBuffer);
			}
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		return s;
	}

	public void write(String fileName, StringBuffer content) throws IOException {
		Writer writer = new FileWriter(fileName);
		try {
			writer.write(content.toString());
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
	}

	public void delete(IPath path) throws CoreException {
		IFileStore fileStore = FileBuffers.getFileStoreAtLocation(path);

		File file = null;
		file = fileStore.toLocalFile(EFS.NONE, NULL_MONITOR);
		delete(file);
	}

	public File toFile(IPath path) throws CoreException {
		IFileStore fileStore = FileBuffers.getFileStoreAtLocation(path);

		File file = null;
		file = fileStore.toLocalFile(EFS.NONE, NULL_MONITOR);
		return file;
	}

	public void delete(File file) throws CoreException{
		if (file.exists()) {
			boolean deleted = false;
			for (int i = 0; i < MAX_RETRY; i++) {
				if (file.delete()){
					deleted=true;
					break;
				}
				else {
					try {
						Thread.sleep(1000); // sleep a second
					} catch (InterruptedException e) {
					}
				}
			}
			if (!deleted){
				EGradleUtil.throwCoreException("cannot delete file:"+file);
			}
		}
	}

	public IPath toPath(File tempFolder) {
		notNull(tempFolder, "'tempFolder' may not be null");
		IPath path = Path.fromOSString(tempFolder.getAbsolutePath());
		return path;
	}

	/**
	 * Gets simple file name without extension
	 * @param resource
	 * @return file name, no extension
	 */
	public String getFileName(IResource resource) {
		String extension = resource.getFileExtension();
		String name = resource.getName();
		if (StringUtils.isBlank(name)){
			return "";
		}
		if (StringUtils.isNotEmpty(extension)){
			int length = extension.length()+1;/* +1 because of dot*/
			String result= name.substring(0,name.length()-length);
			return result;
		}else{
			return name;
		}
	}
}