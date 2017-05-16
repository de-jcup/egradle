package de.jcup.egradle.eclipse.ide.migration;

import de.jcup.egradle.core.migration.AbstractMigration;
import de.jcup.egradle.core.migration.MigrationState;
import de.jcup.egradle.eclipse.ide.IDEUtil;
import de.jcup.egradle.eclipse.migration.EGradleOldMainPreferenceProvider;

public class EGradle1_3ToEGradle2_0Migration extends AbstractMigration{

	private EGradleOldMainPreferenceProvider provider;
	
	public EGradle1_3ToEGradle2_0Migration(){
		provider = new EGradleOldMainPreferenceProvider();
		
		migrationActions.add(new EGradle1_3_MainPreferencesToIdePreferenceMigrationAction(provider));
	}
	
	@Override
	protected boolean isMigrationNecessary() {
		MigrationState migrationState = IDEUtil.getPreferences().getMigrationState();
		if (MigrationState.NOT_MIGRATED.equals(migrationState)){
			return true;
		}
		/* all other states are new and migration done so no no migration from 1.3 necessary*/
		return false;
	}

	@Override
	protected void finalizeMigration() {
		IDEUtil.getPreferences().setMigrationState(MigrationState.MIGRATED_FROM_1_3);
		IDEUtil.logInfo("Migration of data of EGradle <2.0 to EGradle2.0 is done");
	}

}
