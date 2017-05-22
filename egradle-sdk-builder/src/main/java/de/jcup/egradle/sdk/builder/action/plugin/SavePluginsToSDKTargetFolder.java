/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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
 package de.jcup.egradle.sdk.builder.action.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;

public class SavePluginsToSDKTargetFolder implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		File targetXMLPluginsFile = new File(context.targetPathDirectory, context.gradleOriginPluginsFile.getName());
		try (FileOutputStream outputStream = new FileOutputStream(targetXMLPluginsFile)) {
			context.pluginsExporter.exportPlugins(context.xmlPlugins, outputStream);
		}

	}

}
