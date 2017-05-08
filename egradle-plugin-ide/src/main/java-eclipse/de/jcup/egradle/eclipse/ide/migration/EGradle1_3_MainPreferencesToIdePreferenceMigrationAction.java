package de.jcup.egradle.eclipse.ide.migration;

import static de.jcup.egradle.eclipse.ide.preferences.EGradleIdePreferenceConstants.*;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.migration.MigrationAction;
import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.eclipse.ide.IdeUtil;
import de.jcup.egradle.eclipse.ide.preferences.EGradleIdePreferences;
import de.jcup.egradle.eclipse.migration.EGradleOldMainPreferenceProvider;

public class EGradle1_3_MainPreferencesToIdePreferenceMigrationAction implements MigrationAction{

	private EGradleOldMainPreferenceProvider provider;

	public EGradle1_3_MainPreferencesToIdePreferenceMigrationAction(EGradleOldMainPreferenceProvider provider) {
		this.provider=provider;
	}

	@Override
	public void executeMigration() {
		EGradleIdePreferences idePreferences = IdeUtil.getPreferences();
		String oldRootProjectPath = provider.getRootProjectPath();
		if (StringUtils.isBlank(oldRootProjectPath)){
			/* if no root project path is set we do NOT migration the settings because
			 * there were no 1.3. settings at all and we use the defaults from EGradle 2.0
			 */
			return;
		}
		
		idePreferences.setRootProjectPath(oldRootProjectPath);
		idePreferences.setGlobalJavaHomePath(provider.getGlobalJavaHomePath());
		idePreferences.setGradleBinInstallFolder(provider.getGradleBinInstallFolder());
		idePreferences.setGradleCallCommand(provider.getGradleCallCommand());
		idePreferences.setGradleCallTypeID(provider.getGradleCallTypeID());
		idePreferences.setGradleShellType(EGradleShellType.findById(provider.getGradleShellId()));
		
		idePreferences.getPreferenceStore().setValue(P_IMPORT__EXECUTE_ASSEMBLE_TASK.getId(), provider.isExecuteAssembleTaskOnImportEnabled());
		idePreferences.getPreferenceStore().setValue(P_IMPORT__DO_CLEAN_PROJECTS.getId(), provider.isCleanProjectsOnImportEnabled());
		idePreferences.getPreferenceStore().setValue(P_FILEHANDLING_AUTOMATICALLY_DERIVE_BUILDFOLDERS.getId(), provider.isAutomaticallyDeriveBuildFoldersEnabled());
		idePreferences.getPreferenceStore().setValue(P_OUTPUT_VALIDATION_ENABLED.getId(), provider.isOutputValidationEnabled());
		idePreferences.getPreferenceStore().setValue(P_SHOW_CONSOLE_VIEW_ON_BUILD_FAILED_ENABLED.getId(), provider.isShowingConsoleOnBuildFailed());
		idePreferences.getPreferenceStore().setValue(P_DECORATION_SUBPROJECTS_WITH_ICON_ENABLED.getId(), provider.isSubProjectIconDecorationEnabled());
		
	}

}
