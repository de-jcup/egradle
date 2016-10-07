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
package de.jcup.egradle.core.domain;

import static org.apache.commons.lang3.Validate.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class GradleCommand {

	public static GradleCommand[] build(String commandString) {
		return build((GradleSubproject) null, commandString);
	}

	public static GradleCommand[] build(GradleSubproject gradleSubproject, String commandString) {
		notNull(commandString);
		String[] commands = commandString.split(" ");
		if (ArrayUtils.isEmpty(commands)){
			return new GradleCommand[]{};
		}
		List<GradleCommand> list = new ArrayList<>();
		for (int i = 0; i < commands.length; i++) {
			String command = commands[i];
			List<String> param = null;
			/* check if following is an argument (e.g. test --tests de.jcup.MyTestClass*/
			if (i+2<commands.length){
				String potentialArg = commands[i+1];
				if (potentialArg.startsWith("--")) {
					/* argument detected */
					param=new ArrayList<>();
					param.add(potentialArg);
					param.add(commands[i+2]);
					i=i+2;
				}
			}
			list.add(new GradleCommand(gradleSubproject, command, param));
		}
		return list.toArray(new GradleCommand[list.size()]);
	}

	private String command;
	private List<String> commandArguments;

	/**
	 * Combines subproject with command
	 * 
	 * @param subProject
	 *            - the project where the command/task is executed
	 * @param command
	 *            - the task to execute
	 */
	private GradleCommand(GradleSubproject subProject, String command, List<String> commandArguments) {
		StringBuilder sb = new StringBuilder();
		if (subProject != null) {
			sb.append(':');
			sb.append(subProject.getName());
			sb.append(':');
		}
		sb.append(command);
		this.command = sb.toString();
		
		if (commandArguments==null){
			this.commandArguments=Collections.emptyList();
		}else{
			this.commandArguments=Collections.unmodifiableList(commandArguments);
		}
	}

	@Override
	public String toString() {
		return getCommand();
	}

	/**
	 * Get command as simple string
	 * 
	 * @return string
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Returns an unmodifiable list of command arguments
	 * @return command arguments
	 */
	public List<String> getCommandArguments() {
		return commandArguments;
	}

}
