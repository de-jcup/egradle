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
 package de.jcup.egradle.sdk.builder.action.init;

import java.io.FileOutputStream;
import java.io.IOException;

import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;
import de.jcup.egradle.sdk.internal.XMLSDKInfoExporter;

public class InitSDKInfoAction implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {

		try (FileOutputStream stream = new FileOutputStream(context.sdkInfoFile)) {

			XMLSDKInfoExporter exporter = new XMLSDKInfoExporter();
			exporter.exportSDKInfo(context.sdkInfo, stream);
			System.out.println("- written sdk info file:" + context.sdkInfoFile);
		}
	}

}
