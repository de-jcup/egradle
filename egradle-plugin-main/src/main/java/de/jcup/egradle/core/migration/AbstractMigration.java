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
package de.jcup.egradle.core.migration;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMigration {

	protected List<MigrationAction> migrationActions = new ArrayList<MigrationAction>();

	protected AbstractMigration() {
	}

	/**
	 * Returns <code>true</code> when migration is necessary
	 * 
	 * @return
	 */
	protected abstract boolean isMigrationNecessary();

	/**
	 * Does a migration when necessary.
	 * 
	 * @return <code>true</code> when migration was done, otherwise
	 *         <code>false</code>
	 */
	public final boolean migrate() {
		if (!isMigrationNecessary()) {
			return false;
		}
		for (MigrationAction migration : migrationActions) {
			migration.executeMigration();
		}
		finalizeMigration();

		return true;
	}

	/**
	 * Implementation does finalize the migration
	 */
	protected abstract void finalizeMigration();
}
