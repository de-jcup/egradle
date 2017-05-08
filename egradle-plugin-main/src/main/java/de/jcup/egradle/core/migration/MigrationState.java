package de.jcup.egradle.core.migration;

public enum MigrationState {

	/**
	 * Means no migration was started. This was introduced with EGradle 2.0
	 */
	NOT_MIGRATED,
	
	/**
	 * Migration from 1.3 was done
	 */
	MIGRATED_FROM_1_3;

	public static MigrationState fromName(String name) {
		if (name==null){
			return MigrationState.NOT_MIGRATED;
		}
		
		for (MigrationState state: MigrationState.values()){
			if (state.name().equals(name)){
				return state;
			}
		}
		return MigrationState.NOT_MIGRATED;
	}
}
