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

import de.jcup.egradle.eclipse.plugin.sdk.internal.ContainedResourcesCopyingSDK;

public class Activator extends AbstractUIPlugin {

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
		
		try{
			File file = getFileInsidePlugin("sdk");
			if (file==null){
				return;
			}
			ContainedResourcesCopyingSDK sdk = new ContainedResourcesCopyingSDK(version.toString(), file);
			sdk.setDescription("Contains Gradle DSL 3.0");
			
			SDKManager.get().setCurrentSDK(sdk);
		}catch(IOException e){
			getLog().log(new Status(IStatus.ERROR, ID, "Was not able to resolve sdk file inside plugin",e));
		}

	}

	private File getFileInsidePlugin(String path) throws IOException, URISyntaxException {
		Bundle bundle = Platform.getBundle(ID);
		URL url = bundle.getEntry(path);
		if (url==null){
			getLog().log(new Status(IStatus.WARNING, ID, "Was not able to resolve sdk file inside plugin by path:"+path));
			String path2 = "bin/"+path;
			url = bundle.getEntry(path2);
			if (url==null){
				return null;
			}
			getLog().log(new Status(IStatus.INFO, ID, "Fallback to bin folder (PDE) worked!"));
			
		}
		URL resolvedFileURL = FileLocator.toFileURL(url);
		if (resolvedFileURL==null){
			getLog().log(new Status(IStatus.WARNING, ID, "Was not able to resolve sdk file inside plugin by URL:"+resolvedFileURL));
			return null;
		}

		// We need to use the 3-arg constructor of URI in order to properly
		// escape file system chars
		URI resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null);
		File file = new File(resolvedURI);
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

}
