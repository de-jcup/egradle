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
package de.jcup.egradle.eclipse.gradleeditor.control;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import de.jcup.egradle.eclipse.gradleeditor.EditorUtil;

public class OpenInExternalBrowserAction extends Action {

    private String target;

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public void run() {
        if (target == null) {
            return;
        }
        try {
            URL url = new URL(target);
            // Open default external browser
            IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
            IWebBrowser externalBrowser = browserSupport.getExternalBrowser();
            externalBrowser.openURL(url);

        } catch (Exception ex) {
            EditorUtil.INSTANCE.logError("Was not able to open url in external browser", ex);
        }
    }
}