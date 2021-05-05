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
        XMLPlugins alternativeXMLPugins = null;
        File alternativePluginsFile = new File(context.PARENT_OF_RES, "sdkbuilder/override/gradle/" + context.sdkInfo.getGradleVersion() + "/alternative-plugins.xml");
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
                     * TODO ATR,16.02.2017: use a schema and make id mandatory instead of this!
                     */
                    throw new IllegalStateException("found alternative plugin with id NULL");
                }
                XMLPlugin alternativeXmlPlugin = (XMLPlugin) alternativePlugin;

                /* check if plugin already exists */
                Plugin mergeTargetPlugin = null;
                for (Plugin standardPlugin : standardPlugins) {
                    if (standardPlugin.getId().equals(alternativeId)) {
                        mergeTargetPlugin = standardPlugin;
                        break;
                    }
                }
                /*
                 * when not exisiting create new one, otherwise merge extensions
                 */
                if (mergeTargetPlugin == null) {
                    String description = alternativeXmlPlugin.getDescription();
                    if (description == null) {
                        description += "";
                    }
                    alternativeXmlPlugin.setDescription(description + "(alternative)");
                    standardPlugins.add(alternativePlugin);
                } else {
                    /* merge alternative parts to existing */
                    mergeTargetPlugin.getExtensions().addAll(alternativeXmlPlugin.getExtensions());
                }
            }

        }

    }
}
