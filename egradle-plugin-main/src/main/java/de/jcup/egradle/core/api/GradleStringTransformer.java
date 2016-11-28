package de.jcup.egradle.core.api;

/**
 * Implementation must be able to transform GString text containing gradle variables to normal text.<br><br>
 * 
 * e.g.
 * <br>
 * root project resides in "/develop/great-project"<br>
 * <code>"${rootproject}/subproject1/build.gradle"</code>
 *  will be transformed to <code>"/develop/great-project/subproject1/build.gradle"</code>
 * @author Albert Tregnaghi
 *
 */
public interface GradleStringTransformer {

	public static final String ROOTPROJECT_PROJECTDIR="rootProject.projectDir";
	
	/**
	 * Transforms text
	 * @param text
	 * @return transformed text - parts not able to transform will still be contained as variables
	 */
	public String transform(String text);
}
