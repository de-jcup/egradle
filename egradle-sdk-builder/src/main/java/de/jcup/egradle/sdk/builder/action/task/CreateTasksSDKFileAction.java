package de.jcup.egradle.sdk.builder.action.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import de.jcup.egradle.codeassist.dsl.Task;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLTask;
import de.jcup.egradle.codeassist.dsl.XMLTasks;
import de.jcup.egradle.codeassist.dsl.XMLTasksExporter;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;

public class CreateTasksSDKFileAction implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		startTaskDataEstimation(context.originGradleFilesProvider,context);
		writeTasksFile(context);
	}
	
	private void startTaskDataEstimation(GradleDSLTypeProvider provider, SDKBuilderContext context) throws IOException{
		/* now load the xml files as type data - and inspect all descriptions */
		System.out.println("- start task data estimation");
		
		for (String typeName : context.originTypeNameToOriginFileMapping.keySet()) {
			tryToResolveTask(context, provider, typeName);
		}
		
		
	}
	
	private void tryToResolveTask(SDKBuilderContext context, GradleDSLTypeProvider provider, String typeName) {

		Type type = provider.getType(typeName);
		if (type == null) {
			throw new IllegalArgumentException("typeAsString:" + typeName + ", type:" + type + " is null!!?");
		}
		/* filter internal parts */
		if (type.getName().indexOf(".internal.")!=-1){
			return;
		}
		boolean isTask = ! type.isInterface() && type.isImplementingInterface("org.gradle.api.Task");
		if (isTask) {
			/*
			 * TODO ATR, 12.02.2017: determine reason for type - means plugin.
			 * necessary for future
			 */
			context.tasks.put(type.getName(), type);
		}
	}
	
	private void writeTasksFile(SDKBuilderContext context) throws IOException {
		XMLTasksExporter exporter = new XMLTasksExporter();
		File outputFile = new File(context.targetPathDirectory, "tasks.xml");
		XMLTasks tasks = new XMLTasks();
		Set<Task> xmlTasks = tasks.getTasks();
		for (String key : context.tasks.keySet()) {
			if (key.indexOf("org.gradle") != -1) {
				/* we only show tasks from API */
				if (key.indexOf("org.gradle.api") == -1) {
					continue;
				}
			}
			if (key.indexOf("Abstract") != -1) {
				/* we ignore abstract tasks */
				continue;
			}
			Type taskType = context.tasks.get(key);

			XMLTask task = new XMLTask();
			task.setType(taskType);
			task.setTypeAsString(taskType.getName());
			task.setName(taskType.getShortName().toLowerCase());

			xmlTasks.add(task);
		}

		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			exporter.exportTasks(tasks, fos);
		}

	}

}
