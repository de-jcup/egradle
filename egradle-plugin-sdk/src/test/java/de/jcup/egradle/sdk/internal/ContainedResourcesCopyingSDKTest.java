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
package de.jcup.egradle.sdk.internal;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.RootFolderProvider;
import de.jcup.egradle.core.VersionData;
import de.jcup.egradle.sdk.SDK;

public class ContainedResourcesCopyingSDKTest {

    private SDK sdkToTest;

    private RootFolderProvider createRootFolderProvider() {
        return new RootFolderProvider() {

            @Override
            public File getRootFolder() {
                return new File("test");
            }
        };
    }

    @Before
    public void before() {
        sdkToTest = new ContainedResourcesCopyingSDK(new VersionData("1.0.0"), createRootFolderProvider(), null);
    }

    @Test
    public void get_version_returns_set_version() {
        assertEquals(new VersionData("1.0.0"), sdkToTest.getVersion());
    }

    @Test
    public void get_version_returns_unknown_for_manager_called_with_null() {
        sdkToTest = new ContainedResourcesCopyingSDK(null, createRootFolderProvider(), null);
        assertEquals(VersionData.UNKNOWN, sdkToTest.getVersion());
    }

    @Test
    public void get_version_returns_unknown_for_manager_called_with_blank_string() {
        sdkToTest = new ContainedResourcesCopyingSDK(new VersionData(" "), createRootFolderProvider(), null);
        assertEquals(VersionData.UNKNOWN, sdkToTest.getVersion());
    }

}
