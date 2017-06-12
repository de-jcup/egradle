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
