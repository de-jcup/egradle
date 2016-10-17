package de.jcup.egradle.eclipse.junit.contribution.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public enum EGradleJunitPreferenceConstants {
	
	/**
	 * Tasks which are executed on test
	 */
	P_TEST_TASKS("testTasks");

	private String id;

	private EGradleJunitPreferenceConstants(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
