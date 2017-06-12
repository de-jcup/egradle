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
