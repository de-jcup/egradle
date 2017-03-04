package de.jcup.egradle.eclipse.gradleeditor.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;
import org.eclipse.jdt.core.search.TypeNameRequestor;

import de.jcup.egradle.eclipse.api.EGradleUtil;

public class JDTDataAccess {

	public static JDTDataAccess SHARED = new JDTDataAccess();

	/**
	 * Find type
	 * 
	 * @param className
	 * @param monitor
	 * @return type or <code>null</code>
	 */
	public IType findType(String className, IProgressMonitor monitor) {
		final IType[] result = { null };
		TypeNameMatchRequestor nameMatchRequestor = new TypeNameMatchRequestor() {
			@Override
			public void acceptTypeNameMatch(TypeNameMatch match) {
				result[0] = match.getType();
			}
		};
		int lastDot = className.lastIndexOf('.');
		char[] packageName = lastDot >= 0 ? className.substring(0, lastDot).toCharArray() : null;
		char[] typeName = (lastDot >= 0 ? className.substring(lastDot + 1) : className).toCharArray();
		SearchEngine engine = new SearchEngine();
		int packageMatchRule = SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE;
		try {
			engine.searchAllTypeNames(packageName, packageMatchRule, typeName, packageMatchRule, IJavaSearchConstants.TYPE,
					SearchEngine.createWorkspaceScope(), nameMatchRequestor,
					IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, monitor);
		} catch (JavaModelException e) {
			EGradleUtil.log(e);
		}
		return result[0];
	}

	/**
	 * Scan for package names
	 * 
	 * @param type
	 * @param scope
	 * @param packageNames
	 *            - can be <code>null</code>
	 * @return list with java types
	 */
	public List<String> scanForJavaType(String type, IJavaSearchScope scope, String... packageNames) {
		SearchEngine engine = new SearchEngine();
		List<String> foundList = new ArrayList<>();
		try {
			// int packageFlags=0;
			// String packPattern = "org.gradle.api";
			// String typePattern = "Project";
			// int matchRule;
			// int elementKind;
			// IJavaSearchScope searchScope;
			// TypeNameRequestor requestor;
			// IProgressMonitor progressMonitor;
			// engine.searchAllTypeNames((packPattern == null) ? null :
			// packPattern.toCharArray(),
			// packageFlags,
			// typePattern.toCharArray(), matchRule,
			// IJavaElement.TYPE, searchScope, requestor, 3,
			// progressMonitor);

			List<char[]> groovyAutomaticImports = new ArrayList<>();
			if ("BigDecimal".equals(type) || "BigInteger".equals(type)) {
				addImport(groovyAutomaticImports, "java.math");
			} else {
				addImport(groovyAutomaticImports, "java.lang");
				addImport(groovyAutomaticImports, "java.util");
				addImport(groovyAutomaticImports, "java.net");
				addImport(groovyAutomaticImports, "java.io");
				if (packageNames != null && packageNames.length > 0) {
					for (String packageName : packageNames) {
						if (packageName != null && !packageName.isEmpty()) {
							addImport(groovyAutomaticImports, packageName);
						}
					}
				}
			}
			char[][] qualifications = new char[groovyAutomaticImports.size()][];
			for (int i = 0; i < groovyAutomaticImports.size(); i++) {
				qualifications[i] = groovyAutomaticImports.get(i);
			}

			char[][] typeNames = new char[1][];
			typeNames[0] = type.toCharArray();
			TypeNameRequestor nameRequestor = new TypeNameRequestor() {

				@Override
				public void acceptType(int modifiers, char[] packageName, char[] simpleTypeName,
						char[][] enclosingTypeNames, String path) {
					String simpleName = new String(simpleTypeName);
					String simplePackageName = new String(packageName);
					foundList.add(simplePackageName + "." + simpleName);
				}
			};

			int policiy = IJavaSearchConstants.FORCE_IMMEDIATE_SEARCH;
			IProgressMonitor progressMonitor = new NullProgressMonitor();

			engine.searchAllTypeNames(qualifications, typeNames, scope, nameRequestor, policiy, progressMonitor);

		} catch (JavaModelException e) {
			EGradleUtil.log(e);
			;
		}
		return foundList;

	}

	private void addImport(List<char[]> groovyAutomaticImports, String string) {
		groovyAutomaticImports.add(string.toCharArray());
	}
}
