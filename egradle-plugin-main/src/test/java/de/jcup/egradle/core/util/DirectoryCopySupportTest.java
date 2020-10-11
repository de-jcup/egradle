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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class DirectoryCopySupportTest {

    private DirectoryCopySupport copySupportToTest;
    private File tempFile;
    private File tempParent;
    private File src;
    private File dest;
    private File subSrc;
    private File contentFile;

    @Before
    public void before() {
        copySupportToTest = new DirectoryCopySupport();
    }

    @Test
    public void test_copy_directories_without_filenametransformer_works() throws Exception {
        /* prepare */
        String content = "content of file";
        createTestDataStructure("content.txt", content);

        File copiedSubSrc = new File(dest, subSrc.getName());
        File copiedContentFile = new File(copiedSubSrc, contentFile.getName());

        /* execute */
        copySupportToTest.copyDirectories(src, dest, true);

        /* test */
        assertTrue(copiedSubSrc.exists());
        assertTrue(copiedContentFile.exists());
        try (BufferedReader br = new BufferedReader(new FileReader(copiedContentFile))) {
            String read = br.readLine();
            assertEquals(content, read);
        }
    }

    private void createTestDataStructure(String contentFileName, String content) throws IOException {
        long time = System.nanoTime();
        tempFile = File.createTempFile("prefix-fileutil", "suffix-fileutil");
        tempParent = tempFile.getParentFile();
        src = new File(tempParent, "fileutil-srcFolder-" + time);
        src.deleteOnExit();
        dest = new File(tempParent, "fileutil-destFolder-" + time);
        dest.deleteOnExit();

        subSrc = new File(src, "subfolder");
        subSrc.mkdirs();
        subSrc.deleteOnExit();
        createTestFile(contentFileName, content, subSrc);
    }

    private void createTestFile(String contentFileName, String content, File targetFolder) throws IOException {
        contentFile = new File(targetFolder, contentFileName);
        try (FileWriter fw = new FileWriter(contentFile)) {
            fw.write(content);
        }
        /* check preconditions */
        assertFalse(dest.exists());
        assertTrue(targetFolder.exists());
        assertTrue(contentFile.exists());
    }

    @Test
    public void test_copy_directories_with_filenametransformer_works_and_transfomer_is_called_for_files_and_subfolders() throws Exception {
        /* prepare */
        String content = "content of file";
        createTestDataStructure("content.txt", content);

        @SuppressWarnings("unchecked")
        Transformer<String> mockedTransformer = mock(Transformer.class);
        when(mockedTransformer.transform("subfolder")).thenReturn("changedsubfolder");
        when(mockedTransformer.transform("content.txt")).thenReturn("_changed.xyz");

        File copiedSubSrc = new File(dest, "changedsubfolder");
        File copiedContentFile = new File(copiedSubSrc, "_changed.xyz");
        assertFalse(copiedSubSrc.exists());
        assertFalse(copiedContentFile.exists());

        /* execute */
        copySupportToTest.copyDirectories(src, dest, mockedTransformer, true);

        /* test */
        verify(mockedTransformer).transform("subfolder");
        verify(mockedTransformer).transform("content.txt");

        assertTrue(copiedSubSrc.exists());
        assertTrue(copiedContentFile.exists());

        try (BufferedReader br = new BufferedReader(new FileReader(copiedContentFile))) {
            String read = br.readLine();
            assertEquals(content, read);
        }
    }

}
