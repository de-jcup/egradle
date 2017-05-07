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
 package de.jcup.egradle.eclipse.ide.launch;

import de.jcup.egradle.eclipse.ide.IDEActivator;

public interface EGradleLauncherConstants {

	public static final String PROPERTY_TASKS = "tasks";
	public static final String PROPERTY_PROJECTNAME = "projectName";
	public static final String PROPERTY_OPTIONS = "options";
	
	
	public static final String GRADLE_PROPERTIES = IDEActivator.PLUGIN_ID+".gradleProperties";
	public static final String SYSTEM_PROPERTIES = IDEActivator.PLUGIN_ID+".systemProperties";
	public static final String ENVIRONMENT_PROPERTIES = IDEActivator.PLUGIN_ID+".environmentProperties";
}
