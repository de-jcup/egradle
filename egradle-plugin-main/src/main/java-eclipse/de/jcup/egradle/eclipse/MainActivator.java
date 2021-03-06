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
package de.jcup.egradle.eclipse;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MainActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "de.jcup.egradle.eclipse.plugin.main"; //$NON-NLS-1$

    // The shared instance
    private static AbstractUIPlugin plugin;

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /*
     * Special variant, only for jenkins editor - so it can setup itself as
     * providing part...
     */
    public static void delegate(AbstractUIPlugin other) {
        MainActivator.plugin = other;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static AbstractUIPlugin getDefault() {
        return plugin;
    }

}
