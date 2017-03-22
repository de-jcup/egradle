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
 package de.jcup.egradle.eclipse.plugin.sdk;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import de.jcup.egradle.sdk.SDKManager;
import de.jcup.egradle.sdk.internal.ContainedResourcesCopyingSDK;
import de.jcup.egradle.sdk.internal.LogAdapter;
import de.jcup.egradle.sdk.internal.RootFolderProvider;

public class Activator extends AbstractUIPlugin implements LogAdapter, RootFolderProvider {

	public static final String ID = "de.jcup.egradle.eclipse.plugin.sdk";

	private static BundleContext context;
	// The shared instance
	private static Activator plugin;

	static BundleContext getContext() {
		return context;
	}

	public static Activator getDefault() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		plugin = this;

		Bundle bundle = bundleContext.getBundle();
		Version version = bundle.getVersion();
		
			ContainedResourcesCopyingSDK sdk = new ContainedResourcesCopyingSDK(version.toString(),this,this);
			
			SDKManager.get().setCurrentSDK(sdk);

	}
			
	@Override
	public File getRootFolder() throws IOException {
		File file = getFileInsidePlugin("sdk");
		return file;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

	@Override
	public void logInfo(String message) {
		getLog().log(new Status(IStatus.INFO, ID, message));
		
	}

	@Override
	public void logWarn(String message) {
		getLog().log(new Status(IStatus.WARNING, ID,message));
	}

	private File getFileInsidePlugin(String path) throws IOException {
		Bundle bundle = Platform.getBundle(ID);
		URL url = bundle.getEntry(path);
		if (url == null) {
			logWarn("Was not able to resolve sdk file inside plugin by path:" + path);
			String path2 = "bin/" + path;
			url = bundle.getEntry(path2);
			if (url == null) {
				return null;
			}
			logInfo("Fallback to bin folder (PDE) worked!");
	
		}
		URL resolvedFileURL = FileLocator.toFileURL(url);
		if (resolvedFileURL == null) {
			logWarn("Was not able to resolve SDK file inside plugin by URL:" + resolvedFileURL);
			return null;
		}
	
		// We need to use the 3-arg constructor of URI in order to properly
		// escape file system chars
		URI resolvedURI;
		try {
			resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null);
			File file = new File(resolvedURI);
			if (! file.exists()){
				getLog().log(new Status(IStatus.WARNING, ID,
						"Resolved file does not exist::" + file));
				return null;
			}
			return file;
		} catch (URISyntaxException e) {
			throw new IOException("Cannot find file for URL:"+resolvedFileURL,e);
		}
	}

}
