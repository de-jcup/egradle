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
package de.jcup.egradle.eclipse.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.jcup.egradle.core.util.History;

public class QuickLaunchDialogTestUI {

    /**
     * Just for direct simple UI testing
     * 
     * @param args
     */
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Shell");
        shell.setSize(200, 200);
        shell.open();
        History<String> history = new History<>(10);
        history.add("tasks");
        history.add("--version");
        history.add("--stop");
        QuickLaunchDialog dialog = new QuickLaunchDialog(shell, history, "test");
        dialog.open();
        String input = dialog.getValue();
        System.out.println("input was:" + input);
        display.dispose();

    }
}
