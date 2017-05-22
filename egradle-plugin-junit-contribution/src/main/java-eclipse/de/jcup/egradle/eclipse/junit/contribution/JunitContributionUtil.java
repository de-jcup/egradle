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
 package de.jcup.egradle.eclipse.junit.contribution;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import de.jcup.egradle.eclipse.util.EclipseUtil;


public class JunitContributionUtil {

	private static final String ECLIPSE_JDT_JUNIT_RESULT_VIEW = "org.eclipse.jdt.junit.ResultView";

	public static void showTestRunnerViewPartInActivePage() {
		try {
			IWorkbenchPage page= EclipseUtil.getActivePage();
			if (page == null){
				return;
			}
			IViewPart view = page.findView(ECLIPSE_JDT_JUNIT_RESULT_VIEW);
			if (view == null) {
				/*	create and show the result view if it isn't created yet.*/
				page.showView(ECLIPSE_JDT_JUNIT_RESULT_VIEW, null, IWorkbenchPage.VIEW_VISIBLE);
			} 
		} catch (PartInitException pie) {
			JunitUtil.logError("Was not able to show junit view", pie);
		}
	}
}
