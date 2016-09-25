package de.jcup.egradle.core.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public enum EGradleShellType {
	
	
	
	NONE(null, null),

	BASH("bash", "--version"),

	SH("sh", "--version"),

	CMD("cmd.exe", "", "/C");

	private String command;
	private String[] defaultOptions;
	private String executionCheck;
	private String id;

	/**
	 * Creates new type
	 * 
	 * @param command
	 *            - command to execute for shell
	 * @param executionCheck
	 *            - call from shell necessary to do a standalone check
	 * @param defaultOptions
	 *            - default options necessary to call shell (and return...)
	 */
	EGradleShellType(String command, String executionCheck, String... defaultOptions) {
		this.command = command;
		this.defaultOptions = defaultOptions;
		this.executionCheck = executionCheck;

		id = "egradle.shelltype." + name().toLowerCase();
	}

	public String getId() {
		return id;
	}

	/**
	 * Returns shell type for given id or <code>null</code>
	 * 
	 * @param id
	 * @return shell or <code>null</code>
	 */
	public static EGradleShellType findById(String id) {
		if (id == null) {
			return null;
		}
		for (EGradleShellType c : values()) {
			if (c.getId().equals(id)) {
				return c;
			}
		}
		return null;
	}

	public List<String> createCheckStandaloneCommands() {
		List<String> shellExecParams = buildShellAndOptions();
		if (StringUtils.isNotEmpty(executionCheck)) {
			shellExecParams.add(executionCheck);
		}
		return shellExecParams;
	}

	public List<String> createCommands() {
		List<String> shellExecParams = buildShellAndOptions();
		return shellExecParams;
	}


	private List<String> buildShellAndOptions() {
		List<String> shellExecParams = new ArrayList<>();
		if (StringUtils.isNotBlank(command)) {
			shellExecParams.add(command);
			if (ArrayUtils.isNotEmpty(defaultOptions)) {
				for (String option : defaultOptions) {
					shellExecParams.add(option);
				}
			}
		}
		return shellExecParams;
	}
}
