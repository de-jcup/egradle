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
import static org.apache.commons.lang3.Validate.notNull;
public class GradleCommand {

	public static GradleCommand[] build(String ... commands){
		return build((GradleSubproject)null, commands);
	}
	
	public static GradleCommand[] build(GradleSubproject gradleSubproject, String... commands) {
		notNull(commands);
		GradleCommand[] ca = new GradleCommand[commands.length];
		for (int i=0;i<commands.length;i++){
			String command = commands[i];
			ca[i]=new GradleCommand(gradleSubproject, command);
		}
		return ca;
	}
	
	private String command;

	/**
	 * Simple variant - as a single string... Additional constructors could use dedicated objects
	 * @param command
	 */
	public GradleCommand(String command){
		this(null, command);
	}
	
	/**
	 * Combines subproject with command
	 * @param subProject - the project where the command/task is executed
	 * @param command - the task to execute
	 */
	public GradleCommand(GradleSubproject subProject, String command){
		this.command=(subProject==null ? "" : (":"+subProject.getName()+":"))+command;
	}
	
	@Override
	public String toString() {
		return getCommand();
	}
	
	/**
	 * Get command as simple string
	 * @return string
	 */
	public String getCommand() {
		return command;
	}


}
