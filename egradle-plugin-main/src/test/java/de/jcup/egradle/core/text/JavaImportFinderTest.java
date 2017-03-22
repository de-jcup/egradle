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
 package de.jcup.egradle.core.text;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class JavaImportFinderTest {

	private JavaImportFinder finderToTest;

	@Before
	public void before() {
		finderToTest = new JavaImportFinder();
	}

	@Test
	public void test_find_imported_packages_other_newlines() {
		String text = "import de.jcup.egradle.*;\r\n\r\nnew Calculator().add(1);";
		/* execute */
		Set<String> result = finderToTest.findImportedPackages(text);

		/* test */
		assertFound("de.jcup.egradle", result);
	}

	@Test
	public void test_find_imported_packages__asterisk_no_newline() {
		StringBuilder sb = new StringBuilder();
		sb.append("import java.util.*;\r\n");
		sb.append("\r\n");
		sb.append("new Calculator().add(1);");

		/* execute */
		Set<String> result = finderToTest.findImportedPackages(sb.toString());

		/* test */
		assertFound("java.util", result);
	}

	@Test
	public void test_find_imported_packages__asterisk() {
		StringBuilder sb = new StringBuilder();
		sb.append("package de.jcup.egradle.eclipse.gradleeditor;\n");
		sb.append("import java.util.*;\n");

		/* execute */
		Set<String> result = finderToTest.findImportedPackages(sb.toString());

		/* test */
		assertFound("java.util", result);
	}

	@Test
	public void test_find_imported_packages_full_example() {
		/* prepare */
		String code = createCodeExample1();

		/* execute */
		Set<String> result = finderToTest.findImportedPackages(code);

		/* test */
		assertFound("java.util", result);
		assertFound("org.eclipse.ui.ide", result);
		assertFound("de.jcup.egradle.eclipse.gradleeditor.jdt", result);

		assertEquals(15, result.size());
	}

	private void assertFound(String packageName, Set<String> result) {
		if (!result.contains(packageName)) {
			fail("Package:" + packageName + " is not contained in:" + result);
		}
	}

	private String createCodeExample1() {
		StringBuilder sb = new StringBuilder();
		sb.append("package de.jcup.egradle.eclipse.gradleeditor;\n");
		sb.append("import java.util.ArraySet;\n");
		sb.append("import java.util.Iterator;\n");
		sb.append("import java.util.Set;\n");             // 1
		sb.append("import java.util.regex.Pattern;\n"); // 2
		sb.append("\n");
		sb.append("import org.eclipse.core.resources.IFile;\n");
		sb.append("import org.eclipse.core.resources.IWorkspaceRoot;\n");
		sb.append("import org.eclipse.core.resources.ResourcesPlugin;\n"); // 3
		sb.append("import org.eclipse.jdt.core.IJavaElement;\n");
		sb.append("import org.eclipse.jdt.core.JavaModelException;\n"); //4
		sb.append("import org.eclipse.jdt.core.search.IJavaSearchScope;\n"); 
		sb.append("import org.eclipse.jdt.core.search.SearchEngine;\n"); //5
		sb.append("import org.eclipse.jdt.ui.IJavaElementSearchConstants;\n");
		sb.append("import org.eclipse.jdt.ui.JavaUI;\n");//6
		sb.append("import org.eclipse.jface.text.IRegion;\n");//7
		sb.append("import org.eclipse.jface.text.hyperlink.IHyperlink;\n"); //8
		sb.append("import org.eclipse.jface.window.Window;\n");//9
		sb.append("import org.eclipse.swt.widgets.Shell;\n");//10
		sb.append("import org.eclipse.ui.IWorkbenchPage;\n");
		sb.append("import org.eclipse.ui.PartInitException;\n");//11
		sb.append("import org.eclipse.ui.dialogs.SelectionDialog;\n");//12
		sb.append("import org.eclipse.ui.ide.IDE;\n");//13
		sb.append("\n");
		sb.append("import de.jcup.egradle.eclipse.api.EGradleUtil;\n");//14
		sb.append("import de.jcup.egradle.eclipse.gradleeditor.jdt.JDTDataAccess;\n"); // 15. import
		sb.append("\n");
		sb.append(";public class GradleResourceHyperlink implements IHyperlink {\n");
		String code = sb.toString();
		return code;
	}

}
