package de.jcup.egradle.sdk.builder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import de.jcup.egradle.codeassist.dsl.Plugin;

public class ImportPluginsAction implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		try (FileInputStream fis = new FileInputStream(context.gradleOriginPluginsFile)) {
			context.xmlPlugins = context.pluginsImporter.importPlugins(fis);
		}
		Set<Plugin> standardPlugins = context.xmlPlugins.getPlugins();
		for (Plugin standardPlugin : standardPlugins) {
			String standardId = standardPlugin.getId();
			if (standardId == null) {
				/*
				 * TODO ATR,16.02.2017: use a schema and make id mandatory
				 * instead of this!
				 */
				throw new IllegalStateException("found standard plugin with id NULL");
			}
		}
	}
}
