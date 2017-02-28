package de.jcup.egradle.sdk.builder.action.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import de.jcup.egradle.codeassist.dsl.Plugin;
import de.jcup.egradle.codeassist.dsl.XMLPlugin;
import de.jcup.egradle.codeassist.dsl.XMLPlugins;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;

public class ApplyOverridesToPluginsAction implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		XMLPlugins alternativeXMLPugins= null;
		File alternativePluginsFile = new File(context.PARENT_OF_RES,
				"sdkbuilder/override/gradle/" + context.sdkInfo.getGradleVersion() + "/alternative-plugins.xml");
		if (!alternativePluginsFile.exists()) {
			System.err.println("- WARN::alternative plugins file does not exists:" + alternativePluginsFile);
		} else {
			try (FileInputStream fis = new FileInputStream(alternativePluginsFile)) {
				alternativeXMLPugins = context.pluginsImporter.importPlugins(fis);
			}
			Set<Plugin> alternativePlugins = alternativeXMLPugins.getPlugins();
			Set<Plugin> standardPlugins = context.xmlPlugins.getPlugins();

			for (Plugin alternativePlugin : alternativePlugins) {
				String alternativeId = alternativePlugin.getId();
				if (alternativeId == null) {
					/*
					 * TODO ATR,16.02.2017: use a schema and make id mandatory
					 * instead of this!
					 */
					throw new IllegalStateException("found alternative plugin with id NULL");
				}
				XMLPlugin alternativeXmlPlugin = (XMLPlugin) alternativePlugin;
				String description = alternativeXmlPlugin.getDescription();
				if (description == null) {
					description += "";
				}
				alternativeXmlPlugin.setDescription(description + "(alternative)");
				standardPlugins.add(alternativePlugin);
			}

		}
		
	}
}
