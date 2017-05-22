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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.jcup.egradle.core.RootFolderProvider;
import de.jcup.egradle.core.VersionData;
import de.jcup.egradle.core.util.LogAdapter;
import de.jcup.egradle.eclipse.util.EclipseResourceHelper;
import de.jcup.egradle.eclipse.util.EclipseUtil;
import de.jcup.egradle.sdk.SDKManager;
import de.jcup.egradle.sdk.internal.ContainedResourcesCopyingSDK;

public class SDKActivator extends AbstractUIPlugin implements LogAdapter, RootFolderProvider {

	public static final String ID = "de.jcup.egradle.eclipse.plugin.sdk";

	private static BundleContext context;
	// The shared instance
	private static SDKActivator plugin;

	static BundleContext getContext() {
		return context;
	}

	public static SDKActivator getDefault() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		SDKActivator.context = bundleContext;
		plugin = this;

		VersionData version = EclipseUtil.createVersionData(bundleContext.getBundle());
		ContainedResourcesCopyingSDK sdk = new ContainedResourcesCopyingSDK(version, this, this);

		SDKManager.get().setCurrentSDK(sdk);

	}

	@Override
	public File getRootFolder() throws IOException {
		File file = EclipseResourceHelper.DEFAULT.getFileInPlugin("sdk", ID);
		return file;
	}

	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		SDKActivator.context = null;
	}

	@Override
	public void logInfo(String message) {
		getLog().log(new Status(IStatus.INFO, ID, message));

	}

	@Override
	public void logWarn(String message) {
		getLog().log(new Status(IStatus.WARNING, ID, message));
	}

	@Override
	public void logError(String message, Throwable t) {
		getLog().log(new Status(IStatus.ERROR, ID, message,t));
	}

}
