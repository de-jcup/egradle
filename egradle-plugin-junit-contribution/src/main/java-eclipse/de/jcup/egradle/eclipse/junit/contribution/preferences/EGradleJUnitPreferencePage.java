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
package de.jcup.egradle.eclipse.junit.contribution.preferences;

import static de.jcup.egradle.eclipse.junit.contribution.preferences.EGradleJUnitPreferences.*;
import static de.jcup.egradle.eclipse.junit.contribution.preferences.EGradleJunitPreferenceConstants.*;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.egradle.eclipse.ui.SWTFactory;
import de.jcup.egradle.junit.EGradleJUnitTestTasksType;

public class EGradleJUnitPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private ComboFieldEditor gradleCallTypeRadioButton;

	public EGradleJUnitPreferencePage() {
		super(GRID);
		setPreferenceStore(JUNIT_PREFERENCES.getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {

		String[][] entryNamesAndValues = new String[][] {
				new String[] { "clean all", EGradleJUnitTestTasksType.CLEAN_ALL.getId() },
				new String[] { "clean tests only", EGradleJUnitTestTasksType.CLEAN_ONLY_TESTS.getId() },
				new String[] { "do nothing", EGradleJUnitTestTasksType.CLEAN_NOTHING.getId() } };
		/* @formatter:on */
		gradleCallTypeRadioButton = new ComboFieldEditor(P_TEST_TASKS.getId(), "Before test execution",
				entryNamesAndValues, getFieldEditorParent());

		addField(gradleCallTypeRadioButton);
		SWTFactory.createLabel(getFieldEditorParent(), "(This will be used by all test launch configurations)", 2);
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void initialize() {
		super.initialize();
	}

}
