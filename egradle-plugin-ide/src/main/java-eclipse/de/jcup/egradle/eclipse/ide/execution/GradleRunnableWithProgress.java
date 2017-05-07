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
package de.jcup.egradle.eclipse.ide.execution;

import static de.jcup.egradle.eclipse.ide.IdeUtil.*;
import static org.apache.commons.lang3.Validate.*;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import de.jcup.egradle.core.ProcessExecutionResult;

public class GradleRunnableWithProgress implements IRunnableWithProgress {
	private GradleExecutionDelegate execution;

	public GradleRunnableWithProgress(GradleExecutionDelegate execution) {
		notNull(execution, "'execution' may not be null");
		this.execution = execution;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			execution.execute(monitor);
			ProcessExecutionResult result = execution.getResult();
			if (result.isCanceledByuser()){
				return;
			}
			if (!result.isOkay()) {
				getDialogSupport().showWarning("Result was not okay:" + result.getResultCode());
			}
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}

	}

}