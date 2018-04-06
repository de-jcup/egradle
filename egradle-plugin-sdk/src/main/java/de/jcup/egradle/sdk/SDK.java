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
package de.jcup.egradle.sdk;

import java.io.File;
import java.io.IOException;

import de.jcup.egradle.core.VersionData;

public interface SDK {

	VersionData getVersion();

	boolean isInstalled();

	void install() throws IOException;

	/**
	 * Returns dsl folder or <code>null</code>
	 * 
	 * @return dsl folder or <code>null</code>
	 */
	File getSDKInstallationFolder();

	/**
	 * Returns sdk info object , never <code>null</code>
	 * 
	 * @return sdk info object , never <code>null</code>
	 */
	SDKInfo getInfo();

}