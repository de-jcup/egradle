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
package de.jcup.egradle.eclipse.junit.contribution;

import static de.jcup.egradle.eclipse.ide.IDEUtil.*;
import static de.jcup.egradle.eclipse.util.EclipseUtil.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.w3c.dom.Document;

import de.jcup.egradle.core.Constants;
import de.jcup.egradle.core.util.XMLWriter;
import de.jcup.egradle.eclipse.util.EGradlePostBuildJob;
import de.jcup.egradle.junit.JUnitResultFilesFinder;
import de.jcup.egradle.junit.JUnitResultsCompressor;

public class ImportGradleJunitResultsJob extends EGradlePostBuildJob {

	XMLWriter writer = new XMLWriter();
	JUnitResultsCompressor compressor = new JUnitResultsCompressor();
	JUnitResultFilesFinder finder = new JUnitResultFilesFinder();
	private String projectname;
	private boolean ignoreOnValdationErrors;

	public ImportGradleJunitResultsJob(String name, String projectname, boolean ignoreOnValidationErrors) {
		super(name);
		this.projectname = projectname;
		this.ignoreOnValdationErrors = ignoreOnValidationErrors;
	}

	@Override
	protected IStatus run(IProgressMonitor _monitor) {
		if (ignoreOnValdationErrors) {
			if (existsValidationErrors()) {
				return Status.CANCEL_STATUS;
			}
		}
		SubMonitor monitor = SubMonitor.convert(_monitor);
		monitor.beginTask("Import junit results from gradle", 100);
		File tempFile = new File(getTempFolder(), "TEST-egradle-all-testresults.tmp.xml");
		try {
			/* remove old temp file if existing */
			if (tempFile.exists()) {
				if (!tempFile.delete()) {
					throw new IOException("Cannot delete temporary file for gradle results!");
				}
			}

			String projectNameToShow = buildProjectNameToUse();
			/* fetch files */
			String taskName = "Fetching gradle junit result files from '" + projectNameToShow + "'";
			outputToSystemConsole(taskName);
			monitor.setTaskName(taskName);
			File rootFolder = getRootProjectFolder();

			Collection<File> files = finder.findTestFilesInFolder(rootFolder, projectname);
			if (files.isEmpty()) {
				monitor.worked(100);

				/*
				 * we have not test files found - if former build failed and was
				 * a "clean test" all fromer results were cleaned before. if
				 * there is not at least one test, this means the compile task
				 * was not successful
				 */
				if (hasBuildInfo()) {
					if (getBuildInfo().hasBuildFailed()) {
						return Status.CANCEL_STATUS;
					}
				}
				safeAsyncExec(new Runnable() {

					@Override
					public void run() {
						monitor.worked(100);
						MessageDialog.openInformation(getActiveWorkbenchShell(), "No test results found",
								"There are no test results to import from " + projectNameToShow + " at:\n'"
										+ rootFolder.getAbsolutePath()
										+ "'\n\nEither there are no tests or tests are not executed");
					}
				});
				return Status.CANCEL_STATUS;

			}
			List<InputStream> streams = new ArrayList<>();
			for (File file : files) {
				streams.add(new FileInputStream(file));
			}
			monitor.worked(40);

			/* compress */
			monitor.setTaskName("Compress results for import");
			Document singleDoc = compressor.compress(streams);
			monitor.worked(60);

			/* write to file */
			monitor.setTaskName("Create temp file");
			tempFile.getParentFile().mkdirs();
			if (!tempFile.createNewFile()) {
				throw new IOException("Cannot create empty temp file!");
			}
			writer.writeDocumentToFile(singleDoc, tempFile);
			monitor.worked(80);

			/* show inside junit viewer */
			monitor.setTaskName("Load into junit view");

			// JUnitCore.importTestRunSession(tempFile.toURI().toURL().toExternalForm(),
			// monitor);
			JUnitCore.importTestRunSession(tempFile);
			monitor.worked(90);

			monitor.setTaskName("Open test results in view");
			safeAsyncExec(new Runnable() {

				@Override
				public void run() {
					JunitContributionUtil.showTestRunnerViewPartInActivePage();
					monitor.worked(100);
				}
			});
		} catch (Exception e) {
			outputToSystemConsole(Constants.CONSOLE_FAILED);
			// return new Status(Status.ERROR, Activator.PLUGIN_ID, "Cannot
			// import junit results", e);
			return Status.CANCEL_STATUS;
		} finally {
			monitor.done();
		}
		outputToSystemConsole(Constants.CONSOLE_OK);
		return Status.OK_STATUS;
	}

	private String buildProjectNameToUse() {
		if (projectname == null) {
			return "rootproject";
		} else {
			return "project '" + projectname + "'";
		}
	}

}