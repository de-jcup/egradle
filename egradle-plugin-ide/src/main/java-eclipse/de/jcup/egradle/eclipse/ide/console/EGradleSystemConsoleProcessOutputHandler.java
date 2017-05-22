/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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

import org.eclipse.ui.console.MessageConsoleStream;

import de.jcup.egradle.core.process.OutputHandler;

public class EGradleSystemConsoleProcessOutputHandler implements OutputHandler {

	private MessageConsoleStream messageStream;

	public EGradleSystemConsoleProcessOutputHandler() {
		this.messageStream = EGradleSystemConsoleFactory.INSTANCE.getConsole().newMessageStream();
		/* always UTF-8 encoding */
		messageStream.setEncoding("UTF-8");
	}

	@Override
	public void output(String line) {
		messageStream.println(line);
	}

}
