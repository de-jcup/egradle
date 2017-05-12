package de.jcup.egradle.eclipse.ide;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.jcup.egradle.eclipse.ide.migration.EGradle1_3ToEGradle2_0Migration;
import de.jcup.egradle.eclipse.util.ColorManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class IDEActivator extends AbstractUIPlugin {
	private Map<StyledText, IConsolePageParticipant> viewers = new HashMap<StyledText, IConsolePageParticipant>();

	// The plug-in ID
	public static final String PLUGIN_ID = "de.jcup.egradle.eclipse.plugin.ide"; //$NON-NLS-1$

	// The shared instance
	private static IDEActivator plugin;

	private ColorManager colorManager;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		startMigrationsWhereNecessary();
	}

	private void startMigrationsWhereNecessary() {
	   /* Keep ordering here */
	   new EGradle1_3ToEGradle2_0Migration().migrate();
		
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		colorManager.dispose();
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static IDEActivator getDefault() {
		return plugin;
	}

	/**
	 * The constructor
	 */
	public IDEActivator() {
		colorManager = new ColorManager();
	}

	public ColorManager getColorManager() {
		return colorManager;

	}
	

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 *
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public void addViewer(StyledText viewer, IConsolePageParticipant participant) {
		viewers.put(viewer, participant);
	}

	public void removeViewerWithPageParticipant(IConsolePageParticipant participant) {
		Set<StyledText> toRemove = new HashSet<StyledText>();

		for (StyledText viewer : viewers.keySet()) {
			if (viewers.get(viewer) == participant)
				toRemove.add(viewer);
		}

		for (StyledText viewer : toRemove)
			viewers.remove(viewer);
	}
}
