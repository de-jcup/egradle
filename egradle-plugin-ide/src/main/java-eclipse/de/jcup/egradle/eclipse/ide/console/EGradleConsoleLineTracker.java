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
package de.jcup.egradle.eclipse.ide.console;

import java.util.List;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import de.jcup.egradle.core.process.RememberLastLinesOutputHandler;
import de.jcup.egradle.eclipse.ide.IDEUtil;

public class EGradleConsoleLineTracker implements IConsoleLineTracker {

    private IDocument document;
    private RememberLastLinesOutputHandler rememberOutputHandler;

    public EGradleConsoleLineTracker() {
    }

    @Override
    public void init(IConsole console) {
        IDEUtil.removeAllValidationErrorsOfConsoleOutput();

        rememberOutputHandler = IDEUtil.createOutputHandlerForValidationErrorsOnConsole();
        document = console.getDocument();
    }

    @Override
    public void lineAppended(IRegion lineRegion) {
        if (rememberOutputHandler == null) {
            return;
        }
        if (document == null) {
            return;
        }
        try {
            String lineStr = document.get(lineRegion.getOffset(), lineRegion.getLength());
            if (lineStr.startsWith("Total time")) {
                /* ok . time to validate */
                List<String> list = rememberOutputHandler.createOutputToValidate();
                IDEUtil.showValidationErrorsOfConsoleOutput(list);
                rememberOutputHandler = null;
            } else {
                rememberOutputHandler.output(lineStr);
            }
        } catch (BadLocationException e) {
            /* ignore */
        }
    }

    @Override
    public void dispose() {
        document = null;
    }

}
