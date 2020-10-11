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

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.TestUtil;

public class RootProjectFinderTest {

    private RootProjectFinder finderToTest;

    @Before
    public void before() {
        finderToTest = new RootProjectFinder();
    }

    @Test
    public void rootfolder_4_test1_gradle_file_has_rootfolder_4_as_root_folder() {
        File folder = finderToTest.findRootProjectFolder(TestUtil.ROOTFOLDER_4_TEST1_GRADLE);
        assertEquals(TestUtil.ROOTFOLDER_4, folder);
    }

    @Test
    public void rootfolder_2_no_eclipse_project2_readme_has_rootfolder2() {
        File folder = finderToTest.findRootProjectFolder(TestUtil.ROOTFOLDER_2_NO_ECLIPSE_PROJECT2_README);
        assertEquals(TestUtil.ROOTFOLDER_2, folder);
    }

    @Test
    public void rootfolder1_parent_has_null_gradle_rootfolder() {
        File folder = finderToTest.findRootProjectFolder(TestUtil.ROOTFOLDER_1.getParentFile());
        assertNull(folder);
    }

}
