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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import de.jcup.egradle.core.RootFolderCopySupport;
import de.jcup.egradle.core.RootFolderProvider;
import de.jcup.egradle.core.VersionData;
import de.jcup.egradle.core.VersionedFolderToUserHomeCopySupport;
import de.jcup.egradle.core.util.LogAdapter;

public class ContainedResourcesCopyingSDK extends AbstractSDK {

    private LogAdapter logAdapter;
    private RootFolderCopySupport rootFolderCopySupport;
    private RootFolderProvider rootFolderProvider;

    /**
     * Creates internal file based sdk
     * 
     * @param sdkVersion         - if <code>null</code> or blank then version
     *                           "unknown" will be used instead
     * @param rootFolderProvider - may not be <code>null</code>
     * @param logAdapter         can be <code>null</code>
     * @throws IllegalArgumentException when rootFolderProvider is <code>null</code>
     */
    public ContainedResourcesCopyingSDK(VersionData sdkVersion, RootFolderProvider rootFolderProvider, LogAdapter logAdapter) {
        super(sdkVersion);
        if (rootFolderProvider == null) {
            throw new IllegalArgumentException("root folder provider may not be null!");
        }
        this.rootFolderProvider = rootFolderProvider;
        this.logAdapter = logAdapter;

        rootFolderCopySupport = new VersionedFolderToUserHomeCopySupport("sdk", getVersion(), logAdapter);
    }

    @Override
    public boolean isInstalled() {
        return rootFolderCopySupport.isTargetFolderExisting();
    }

    @Override
    public void install() throws IOException {
        if (!rootFolderCopySupport.copyFrom(rootFolderProvider)) {
            return;
        }

        File sdkInfoFile = new File(rootFolderCopySupport.getTargetFolder(), "sdk.xml");
        XMLSDKInfo sdkInfo = null;
        if (sdkInfoFile.exists()) {
            XMLSDKInfoImporter importer = new XMLSDKInfoImporter();
            try (FileInputStream fis = new FileInputStream(sdkInfoFile)) {
                sdkInfo = importer.importSDKInfo(fis);
            }
        } else {
            sdkInfo = new XMLSDKInfo();
            sdkInfoFile.createNewFile();
        }
        sdkInfo.setInstallationDate(new Date());
        sdkInfo.setSdkVersion(version.getAsText());

        try (OutputStream out = new FileOutputStream(sdkInfoFile)) {
            XMLSDKInfoExporter exporter = new XMLSDKInfoExporter();
            exporter.exportSDKInfo(sdkInfo, out);
        }

        if (logAdapter != null) {
            logAdapter.logInfo("Successfully installed SDK for " + getVersion());
        }

    }

    @Override
    public File getSDKInstallationFolder() {
        return rootFolderCopySupport.getTargetFolder();
    }

    @Override
    public String toString() {
        return "ContainedResourcesCopyingSDK [" + version + "]";
    }

}
