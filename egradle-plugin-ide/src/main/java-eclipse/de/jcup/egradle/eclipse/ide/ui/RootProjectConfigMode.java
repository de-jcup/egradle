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
			ROOTPATH_EDITABLE,
			RESTORE_METADATA_CHECKBOX_NOT_NEEDED),
	
	REIMPORT_PROJECTS(
			NO_VALIDATION_GROUP_NEEDED,
			ROOTPATH_NOT_EDITABLE,
			RESTORE_METADATA_CHECKBOX_NEEDED),
	
	NEW_PROJECT_WIZARD(
			NO_VALIDATION_GROUP_NEEDED,
			ROOTPATH_NOT_EDITABLE,
			RESTORE_METADATA_CHECKBOX_NOT_NEEDED)
	;
	/* @formatter:on*/
	private boolean validationGroupNeeded;
	private boolean rootPathDirectoryEditable;
	private boolean restoreMetaDataCheckboxNeeded;

	private RootProjectConfigMode(boolean validationGroupNeeded, boolean rootPathDirectoryEditable, boolean restoreMetaDataCheckboxNeeded) {
		this.validationGroupNeeded = validationGroupNeeded;
		this.rootPathDirectoryEditable=rootPathDirectoryEditable;
		this.restoreMetaDataCheckboxNeeded=restoreMetaDataCheckboxNeeded;
	}

	public boolean isValidationGroupNeeded() {
		return validationGroupNeeded;
	}

	public boolean isRootPathDirectoryEditable() {
		return rootPathDirectoryEditable;
	}

	public boolean isRestoreMetaDataCheckBoxNeeded() {
		return restoreMetaDataCheckboxNeeded;
	}
}