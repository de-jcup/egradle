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
		gradleCallTypeRadioButton = new ComboFieldEditor(P_TEST_TASKS.getId(), "Before test execution", entryNamesAndValues,
				getFieldEditorParent());

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
