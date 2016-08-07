package de.jcup.egradle.core.domain;
import static org.apache.commons.lang3.Validate.*;
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
	 * @param command
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
