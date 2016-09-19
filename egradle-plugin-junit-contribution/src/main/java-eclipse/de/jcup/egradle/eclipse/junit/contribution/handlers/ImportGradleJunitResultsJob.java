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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;

import de.jcup.egradle.core.api.XMLWriter;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.junit.contribution.Activator;
import de.jcup.egradle.eclipse.junit.contribution.JunitContributionUtil;
import de.jcup.egradle.junit.JUnitResultFilesFinder;
import de.jcup.egradle.junit.JUnitResultsCompressor;

public class ImportGradleJunitResultsJob extends Job {
	
	XMLWriter writer = new XMLWriter();
	JUnitResultsCompressor compressor = new JUnitResultsCompressor();
	JUnitResultFilesFinder finder = new JUnitResultFilesFinder();

	public ImportGradleJunitResultsJob(String name) {
		super(name);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Import junit results from gradle", 100);
		File tempFile = new File(System.getProperty("user.home")+"/.egradle/TEST-egradle-all-testresults.tmp.xml");
		try {
			/* remove old temp file if existing */
			if (tempFile.exists()) {
				if (!tempFile.delete()) {
					throw new IOException("Cannot delete temporary file for gradle results!");
				}
			}
			
			/* fetch files*/
			monitor.setTaskName("Fetching gradle junit result files");
			File rootFolder = EGradleUtil.getRootProjectFolder();

			Collection<File> files = finder.findTestFilesInRootProjectFolder(rootFolder);
			List<InputStream> streams = new ArrayList<>();
			for (File file : files) {
				streams.add(new FileInputStream(file));
			}
			monitor.worked(40);
			
			/* compress*/
			monitor.setTaskName("Compress results for import");
			Document singleDoc = compressor.compress(streams);
			monitor.worked(60);
			
			/* write to file*/
			monitor.setTaskName("Create temp file");
			tempFile.getParentFile().mkdirs();
			if (!tempFile.createNewFile()){
				throw new IOException("Cannot create empty temp file!");
			}
			writer.writeDocumentToFile(singleDoc, tempFile);
			monitor.worked(80);
			
			/* show inside junit viewer*/
			monitor.setTaskName("Load into junit view");
			
//			JUnitCore.importTestRunSession(tempFile.toURI().toURL().toExternalForm(), monitor);
			JUnitCore.importTestRunSession(tempFile);
			monitor.worked(90);
			
			monitor.setTaskName("Open test results in view");
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					JunitContributionUtil.showTestRunnerViewPartInActivePage();
					monitor.worked(100);
				}
			});
		} catch (Exception e) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "Cannot import junit results", e);
		}
		return Status.OK_STATUS;
	}

}