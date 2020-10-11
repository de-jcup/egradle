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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.StringUtils;

public class FileSupport {

    private static final int TIME_TO_SLEEP__BEFORE_RETRY = 1000;

    public static final FileSupport DEFAULT = new FileSupport();

    private static final int MAX_RETRY = 5;

    private static final String LINE_SEP = System.getProperty("line.separator");

    private byte[] buffer = new byte[8192];

    public String createCorrectFilePath(String folderName, String filename) {
        String fileTrim = filename.trim();
        if (StringUtils.isEmpty(folderName)) {
            return fileTrim;
        }
        String folderTrim = folderName.trim();
        File asFile = new File(folderTrim, fileTrim);
        return asFile.getAbsolutePath();
    }

    public boolean isDirectSubFolder(File folder, File expectedParentFolder) {
        if (folder == null) {
            return false;
        }
        if (expectedParentFolder == null) {
            return false;
        }
        if (expectedParentFolder.equals(folder.getParentFile())) {
            return true;
        }
        return false;
    }

    /**
     * Copies the given source file to the given destination file.
     *
     * @param src the given source file
     * @param dst the given destination file
     * @throws IOException in case of error
     */
    public void copy(File src, File dst) throws IOException {
        if (src.isDirectory()) {
            if (!dst.exists()) {
                dst.mkdirs();
            }
            String[] srcChildren = src.list();
            for (int i = 0; i < srcChildren.length; ++i) {
                File srcChild = new File(src, srcChildren[i]);
                File dstChild = new File(dst, srcChildren[i]);
                copy(srcChild, dstChild);
            }
        } else {
            transferData(src, dst);
        }
    }

    /**
     * Unzips the given zip file to the given destination directory extracting only
     * those entries the pass through the given filter.
     *
     * @param zipFile the zip file to unzip
     * @param dstDir  the destination directory
     * @throws IOException in case of problem
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
                        src.close();
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
     * @param path         a file path
     * @param oldSeparator a path separator character
     * @param newSeparator a path separator character
     * @return the file path with its separator character changed from the given old
     *         separator to the given new separator
     */
    public String changeSeparator(String path, char oldSeparator, char newSeparator) {
        return path.replace(oldSeparator, newSeparator);
    }

    public void createTextFile(File parentFolder, String fileName, String content) throws IOException {
        File textFile = new File(parentFolder, fileName);
        createTextFile(textFile, content);
    }

    public void createTextFile(File textFile, String content) throws IOException {
        if (textFile == null) {
            throw new IllegalArgumentException("text file may not be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("content may not be null");
        }
        try (FileOutputStream fileOutputStram = new FileOutputStream(textFile); OutputStreamWriter w = new OutputStreamWriter(fileOutputStram, "UTF-8")) {
            textFile.createNewFile();
            w.write(content);
        } catch (Exception e) {
            throw new IOException("Cannot create " + textFile, e);
        }
    }

    /**
     * Copies all bytes in the given source file to the given destination file.
     *
     * @param source      the given source file
     * @param destination the given destination file
     * @throws IOException in case of error
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
     * Copies all bytes in the given source stream to the given destination stream.
     * Neither streams are closed.
     *
     * @param source      the given source stream
     * @param destination the given destination stream
     * @throws IOException in case of error
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

    public String readTextFile(File file) throws IOException {
        return readTextFile(file, LINE_SEP);
    }

    public String readTextFile(File file, String lineSep) throws IOException {
        int lineNr = 0;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                if (lineNr > 0) {
                    /* not first line so add line sep */
                    sb.append(lineSep);
                }
                sb.append(line);
                lineNr++;
            }
        }
        return sb.toString();
    }

    public void writeTextFile(File file, String content) throws IOException {
        if (file.isDirectory()) {
            throw new IOException("Cannot write content to an existing directory:" + file);
        }
        delete(file);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(content);
        }
    }

    /**
     * Delete given file. If it is a folder delete is done recursive
     * 
     * @param file
     * @throws IOException
     * @throws IllegalArgumentException when file is <code>null</code>
     */
    public void delete(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file may not be null!");
        }
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (File child : children) {
                delete(child);
            }
        }
        boolean deleted = false;
        for (int i = 0; i < MAX_RETRY; i++) {
            if (file.delete()) {
                deleted = true;
                break;
            } else {
                try {
                    Thread.sleep(TIME_TO_SLEEP__BEFORE_RETRY);
                } catch (InterruptedException e) {
                }
            }
        }
        if (!deleted) {
            throw new IOException("cannot delete file:" + file);
        }
    }

    public File getEGradleUserHomeFolder() {
        return getEGradleUserHomeFolder(null);
    }

    public File getEGradleUserHomeFolder(String folder) {
        String userHome = System.getProperty("user.home");
        File egradleFolder = new File(userHome, ".egradle");
        File target = egradleFolder;
        if (!StringUtils.isBlank(folder)) {
            target = new File(egradleFolder, folder);
        }
        target.mkdirs();
        return target;
    }

    /**
     * Checks if given file is inside given folder
     * 
     * @param file
     * @param expectedFolder
     * @return <code>true</code> when inside folder or one of its sub folders
     */
    public boolean isInside(File file, File expectedFolder) {
        if (file == null) {
            return false;
        }
        if (expectedFolder == null) {
            return false;
        }
        boolean isInside = false;
        File parent = file;

        while (!isInside && parent != null) {
            parent = parent.getParentFile();

            if (parent == null) {
                continue;
            }

            if (expectedFolder.equals(parent)) {
                isInside = true;
            }

        }

        return isInside;
    }
}
