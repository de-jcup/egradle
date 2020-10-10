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
package de.jcup.egradle.eclipse.ide.preferences;

public enum EGradleIdePreferenceConstants {
	P_MIGRATE_IDE_STATE("migrateIdeState"),

	P_ROOTPROJECT_PATH("pathGradleRootProject"),

	P_JAVA_HOME_PATH("pathJavaHome"),
	
	P_GRADLE_USER_HOME("gradleUserHome"),

	P_GRADLE_CALL_TYPE("gradleCallType"), P_GRADLE_SHELL("commandShell"), P_GRADLE_INSTALL_BIN_FOLDER(
			"pathGradleInstallation"), P_GRADLE_CALL_COMMAND("commandGradle"), P_OUTPUT_VALIDATION_ENABLED(
					"outputValidationEnabled"), P_SHOW_CONSOLE_VIEW_ON_BUILD_FAILED_ENABLED(
							"showConsoleViewOnBuildfailed"),

	/* file handling parts */
	P_FILEHANDLING_AUTOMATICALLY_DERIVE_BUILDFOLDERS("automaticallyDeriveBuildFolders"),

	/* import */
	P_IMPORT__EXECUTE_ASSEMBLE_TASK("onImportExecuteAssembleTask"), P_IMPORT__DO_CLEAN_PROJECTS(
			"onImportDoCleanProjects"),

	P_DECORATION_SUBPROJECTS_WITH_ICON_ENABLED("decorateSubprojectsWithIconEnabled");

	private String id;

	private EGradleIdePreferenceConstants(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

}