package de.jcup.egradle.sdk.internal;

import java.io.File;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Own file util to avoid dependency to commons-io etc. (sdk parts are big enough, so we reduce size here by
 * doing some simple file operations by own silly code)
 * @author Albert Tregnaghi
 *
 */
public class FileUtil {

	/**
	 * Copy given src folder itself into dest folder<br>
	 * e.g. copy "testfolder" into "targetfolder", resuls in "targetfolder/testfolder/..."
	 * @param sourceFolder
	 * @param destinationFolder
	 * @throws IOException
	 */
	public static void copyDirectories(File sourceFolder, File destinationFolder) throws IOException {
		if (sourceFolder == null) {
			throw new IllegalArgumentException("src may not be null!");
		}
		if (! sourceFolder.exists()){
			throw new FileNotFoundException("source folder does not exist:"+sourceFolder);
		}
		if (destinationFolder == null) {
			throw new IllegalArgumentException("dest may not be null!");
		}
		if (destinationFolder.exists()){
			throw new IOException("destination already exists:"+destinationFolder);
		}
		copyRecursive(sourceFolder, destinationFolder);
	}
		
	private static void copyRecursive(File src, File dest) throws IOException {
		
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdirs();
			}
			File sourceDirectory = src;
			File srcDirChildren[] = sourceDirectory.listFiles();

			for (File srcFile : srcDirChildren) {
				File destFile = new File(dest, srcFile.getName());
				
				copyRecursive(srcFile, destFile);
			}
		} else {
			copyFile(src, dest);
		}
	}

	private static void copyFile(File src, File dest) throws IOException {
		try (InputStream in = new FileInputStream(src); OutputStream out = new FileOutputStream(dest)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
		}
	}
}
