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
package de.jcup.egradle.sdk.builder.action.type;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLType;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;

public class ImportTypesAction implements SDKBuilderAction {

    @Override
    public void execute(SDKBuilderContext context) throws IOException {

        TypeImportFilter filter = new TypeImportFilter();
        scanTypes(context.sourceParentDirectory, filter, context);

        if (context.originTypeNameToOriginFileMapping.isEmpty()) {
            throw new IllegalStateException("no types found!");
        }

        /* now types are scanned, so start importing all types */
        for (String typeName : context.originTypeNameToOriginFileMapping.keySet()) {
            /* simply call the provider, it will resolve the type */
            Type buildtype = context.originGradleFilesProvider.getType(typeName);
            if (!typeName.startsWith("org.gradle.tooling")) {
                String shortName = StringUtils.substringAfterLast(typeName, ".");
                context.alternativeApiMapping.put(shortName, typeName);
            }
            if (buildtype == null) {
                throw new IllegalArgumentException("Cannot build type:" + typeName);
            }
        }
    }

    private void scanTypes(File file, FileFilter filter, SDKBuilderContext context) throws IOException {
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles(filter);
            for (File childFile : listFiles) {
                scanTypes(childFile, filter, context);
            }
        } else if (file.isFile()) {
            try (InputStream stream = new FileInputStream(file)) {
                XMLType tempType = context.typeImporter.importType(stream);
                String typeName = tempType.getName();
                context.originTypeNameToOriginFileMapping.put(typeName, file);
            }
        }
    }

    private class TypeImportFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (file == null) {
                return false;
            }
            if (file.isDirectory()) {
                return true;
            }
            String name = file.getName();
            if (name.equals("plugins.xml")) {
                return false;
            }
            return name.endsWith(".xml");
        }
    };

}
