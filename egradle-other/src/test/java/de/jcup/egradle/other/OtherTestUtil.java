package de.jcup.egradle.other;

import java.io.File;

public class OtherTestUtil {

	public static File PARENT_OF_TEST = new File("egradle-other/src/test/res/");
	static {
		if (!PARENT_OF_TEST.exists()) {
			/*
			 * fall back - to be testable by eclipse in sub projects and also
			 * via gradle from root project.
			 */
			PARENT_OF_TEST = new File("src/test/res/");
		}
	}
}
