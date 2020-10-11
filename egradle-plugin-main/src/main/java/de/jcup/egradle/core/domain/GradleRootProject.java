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
package de.jcup.egradle.core.domain;

import static org.apache.commons.lang3.Validate.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.groovyantlr.GradleModelBuilder;
import de.jcup.egradle.core.util.FileSupport;

public class GradleRootProject extends AbstractGradleProject {

    private final static FileFilter FILTER = new GradleSettingsFileFilter();
    private File file;
    private Model settingsModel;
    private Item includeItem;

    /**
     * Creates a gradle root project
     * 
     * @param file
     * @throws IOException when given fils is not a directory or does not exists
     */
    public GradleRootProject(File file) throws IOException {
        notNull(file);
        if (!file.exists()) {
            throw new IOException("Given root project folder does not exist:" + file);
        }
        if (!file.isDirectory()) {
            throw new IOException("Given root project folder is not a directory:" + file);
        }
        this.file = file;
        tryToLoadIncludeItemInGradleSettings();
    }

    public String getName() {
        return file.getName();
    }

    private boolean hasAtLeastOneInclude() {
        return (includeItem != null);
    }

    public void createNewSubProject(String subProjectName) throws GradleProjectException {
        File folder = getFolder();
        if (!isMultiProject()) {
            throw new GradleProjectException("Project is not a multi project, so cannot add sub project:" + subProjectName + " at " + folder);
        }
        assertEveryIncludeDoesNotContainNewSubprojectAlready(subProjectName);
        FileSupport fileSupport = FileSupport.DEFAULT;

        File subProjectFolder = createSubProjectFolderOrFail(subProjectName, folder);
        File settingsGradle = getExistingSettingsFileOrFail(folder);

        try {
            try (FileWriter writer = new FileWriter(settingsGradle, true)) {
                /*
                 * the moste simple variant to add subproject - just add another include to the
                 * EOF will always work...
                 */
                writer.append("\ninclude '" + subProjectName + "'");
            }
            fileSupport.createTextFile(subProjectFolder, "build.gradle", "");

        } catch (IOException e) {
            throw new GradleProjectException("Problems occurred on addding subproject infromation to setttings.gradle", e);
        }

    }

    private File getExistingSettingsFileOrFail(File folder) throws GradleProjectException {
        File settingsGradle = new File(folder, "settings.gradle");
        if (!settingsGradle.exists()) {
            throw new GradleProjectException("did not found settings.gradle any more at:" + settingsGradle);
        }
        return settingsGradle;
    }

    private File createSubProjectFolderOrFail(String subProjectName, File folder) throws GradleProjectException {
        File subProjectFolder = new File(folder, subProjectName);
        if (!subProjectFolder.exists()) {
            if (!subProjectFolder.mkdirs()) {
                throw new GradleProjectException("Was not able to create sub project folder:" + subProjectFolder);
            }
        }
        return subProjectFolder;
    }

    private void assertEveryIncludeDoesNotContainNewSubprojectAlready(String subProjectName) throws GradleProjectException {
        Item[] children = settingsModel.getRoot().getChildren();
        for (Item item : children) {
            if (!isInclude(item)) {
                continue;
            }
            String name = includeItem.getName();
            String[] words = name.split("\\s");// split by whitespaces
            for (int i = 0; i < words.length; i++) {
                String includePart = words[i];
                if (subProjectName.equals(includePart)) {
                    throw new GradleProjectException("Sub project with name:" + subProjectName + " does already exist!");
                } else if (subProjectName.equalsIgnoreCase(includePart)) {
                    throw new GradleProjectException("Sub project with name:" + subProjectName + " would exist on a windows system duplicated (windows file system ignores case...)");
                }
            }
        }
    }

    private void tryToLoadIncludeItemInGradleSettings() {
        /* reset include item if reload */
        includeItem = null;

        File[] files = file.listFiles(FILTER);
        if (files == null || files.length != 1) {
            return;
        }
        File gradleSettings = files[0];
        if (gradleSettings == null || !gradleSettings.exists() || !gradleSettings.isFile()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(gradleSettings)) {
            GradleModelBuilder builder = new GradleModelBuilder(fis);
            settingsModel = builder.build(null);
            Item root = settingsModel.getRoot();
            if (root == null) {
                return;
            }
            Item[] children = root.getChildren();
            for (Item item : children) {
                if (item == null) {
                    continue;
                }
                if (isInclude(item)) {
                    includeItem = item;
                    break;
                }
            }
        } catch (Exception e) {
            /* ignore */
        }
    }

    private boolean isInclude(Item item) {
        String identifier = item.getIdentifier();
        return "include".equals(identifier);
    }

    public File getFolder() {
        return file;
    }

    public boolean isMultiProject() {
        return hasAtLeastOneInclude();
    }

    private static class GradleSettingsFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (file == null) {
                return false;
            }
            if (file.isDirectory()) {
                return false;
            }
            String name = file.getName();
            return "settings.gradle".equals(name);
        }

    }
}
