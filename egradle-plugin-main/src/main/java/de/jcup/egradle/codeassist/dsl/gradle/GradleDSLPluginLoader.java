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
package de.jcup.egradle.codeassist.dsl.gradle;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import de.jcup.egradle.codeassist.CodeCompletionRegistry;
import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryEvent;
import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryEventType;
import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryListener;
import de.jcup.egradle.codeassist.CodeCompletionService;
import de.jcup.egradle.codeassist.dsl.DSLFileLoader;
import de.jcup.egradle.codeassist.dsl.Plugin;
import de.jcup.egradle.codeassist.dsl.PluginMerger;
import de.jcup.egradle.core.util.ErrorHandler;

public class GradleDSLPluginLoader implements CodeCompletionService, RegistryListener {

    protected DSLFileLoader fileLoader;
    private ErrorHandler errorHandler;

    public GradleDSLPluginLoader(DSLFileLoader loader) {
        if (loader == null) {
            throw new IllegalArgumentException("loader may never be null!");
        }
        this.fileLoader = loader;
    }

    @Override
    public void onCodeCompletionEvent(RegistryEvent event) {
        if (event.getType() != RegistryEventType.LOAD_PLUGINS) {
            return;
        }
        CodeCompletionRegistry registry = event.getRegistry();
        GradleDSLTypeProvider typeProvider = registry.getService(GradleDSLTypeProvider.class);
        PluginMerger merger = new PluginMerger(typeProvider, getErrorHandler());

        Set<Plugin> plugins;
        /* load plugins.xml */
        try {
            plugins = fileLoader.loadPlugins();
        } catch (IOException e) {
            if (errorHandler != null) {
                errorHandler.handleError("Cannot load plugins.xml", e);
            }
            plugins = new LinkedHashSet<>();
        }

        merger.merge(plugins);

    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    protected ErrorHandler getErrorHandler() {
        if (errorHandler == null) {
            return ErrorHandler.IGNORE_ERRORS;
        }
        return errorHandler;
    }

}
