package de.jcup.egradle.core.migration;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMigration {

	protected List<MigrationAction> migrationActions = new ArrayList<MigrationAction>();

	protected AbstractMigration() {
	}

	/**
	 * Returns <code>true</code> when migration is necessary
	 * @return
	 */
	protected abstract boolean isMigrationNecessary();

	/**
	 * Does a migration when necessary.
	 * @return <code>true</code> when migration was done, otherwise <code>false</code>
	 */
	public final boolean migrate() {
		if (! isMigrationNecessary()){
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
