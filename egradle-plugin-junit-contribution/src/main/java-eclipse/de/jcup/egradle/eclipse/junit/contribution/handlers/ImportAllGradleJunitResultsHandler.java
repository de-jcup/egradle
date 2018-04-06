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
package de.jcup.egradle.eclipse.junit.contribution.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.jcup.egradle.eclipse.junit.contribution.ImportGradleJunitResultsJob;

public class ImportAllGradleJunitResultsHandler extends AbstractHandler {

	public static final String COMMAND_ID = "de.jcup.egradle.eclipse.ide.junit.contribution.commands.importTestResultCommand";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ImportGradleJunitResultsJob job = new ImportGradleJunitResultsJob("Import gradle junit results", null, false);
		job.schedule();
		return null;
	}

}
