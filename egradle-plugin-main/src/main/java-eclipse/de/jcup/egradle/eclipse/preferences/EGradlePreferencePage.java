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
package de.jcup.egradle.eclipse.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.egradle.eclipse.MainActivator;
import de.jcup.egradle.eclipse.util.EclipseUtil;

public class EGradlePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public EGradlePreferencePage() {
		super(GRID);
		setDescription(createDescription());
		setImageDescriptor(EclipseUtil.createImageDescriptor("icons/gradle-og.png", MainActivator.PLUGIN_ID));
	}
	
	private String createDescription(){
		StringBuilder sb = new StringBuilder();
		sb.append("Main page of EGradle preferences.\n");
		sb.append("\n");
		sb.append("Setup is done in sub pages. If you have only installed\n");
		sb.append("EGradle Editor but not IDE parts you will have only editor\n");
		sb.append("preference pages.");
		return sb.toString();
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	public void setValid(boolean valid) {
		super.setValid(valid);
	}

}