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
package de.jcup.egradle.eclipse.ide.ui;

import static de.jcup.egradle.eclipse.ide.ui.RootProjectConfigModeConstants.*;

public enum RootProjectConfigMode {
	/* @formatter:off*/
	IMPORT_PROJECTS(
			VALIDATION_GROUP_NEEDED, 
			ROOTPATH_EDITABLE),
	
	REIMPORT_PROJECTS(
			NO_VALIDATION_GROUP_NEEDED,
			ROOTPATH_NOT_EDITABLE),
	
	NEW_PROJECT_WIZARD(
			NO_VALIDATION_GROUP_NEEDED,
			ROOTPATH_NOT_EDITABLE)
	;
	/* @formatter:on*/
	private boolean validationGroupNeeded;
	private boolean rootPathDirectoryEditable;

	private RootProjectConfigMode(boolean validationGroupNeeded, boolean rootPathDirectoryEditable) {
		this.validationGroupNeeded = validationGroupNeeded;
	}

	public boolean isValidationGroupNeeded() {
		return validationGroupNeeded;
	}

	public boolean isRootPathDirectoryEditable() {
		return rootPathDirectoryEditable;
	}
}