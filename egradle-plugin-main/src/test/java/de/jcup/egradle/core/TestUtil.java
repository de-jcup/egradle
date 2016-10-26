package de.jcup.egradle.core;

import java.io.File;

public class TestUtil {

	public static File PARENT_OF_TEST = new File("egradle-plugin-main/src/test/res/");
	static {
		if (!PARENT_OF_TEST.exists()) {
			/*
			 * fall back - to be testable by eclipse in sub projects and also
			 * via gradle from root project.
			 */
			PARENT_OF_TEST = new File("src/test/res/");
		}
	}
	public static final File ROOTFOLDER_1 = new File(PARENT_OF_TEST, "rootproject1");
	public static final File ROOTFOLDER_2 = new File(PARENT_OF_TEST, "rootproject2");
	
	public static final File ROOTFOLDER_2_ECLIPSE_PROJECT1 = new File(ROOTFOLDER_2, "eclipse-project1");
	public static final File ROOTFOLDER_2_ECLIPSE_PROJECT2 = new File(ROOTFOLDER_2, "eclipse-project2");
	public static final File ROOTFOLDER_2_NO_ECLIPSE_PROJECT1 = new File(ROOTFOLDER_2, "no-eclipse-project1");
	public static final File ROOTFOLDER_2_NO_ECLIPSE_PROJECT2 = new File(ROOTFOLDER_2, "no-eclipse-project1");
}
