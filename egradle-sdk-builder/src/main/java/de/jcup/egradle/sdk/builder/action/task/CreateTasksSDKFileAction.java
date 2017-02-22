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

	}
	
	private void startTaskDataEstimation(GradleDSLTypeProvider provider, SDKBuilderContext context) throws IOException{
		/* now load the xml files as type data - and inspect all descriptions */
		System.out.println("- start task data estimation");
		
		for (String typeName : context.allTypes.keySet()) {
			tryToResolveTask(context, provider, typeName);
		}
		
		
	}
	
	private void tryToResolveTask(SDKBuilderContext context, GradleDSLTypeProvider provider, String typeName) {

		Type type = provider.getType(typeName);
		if (type == null) {
			throw new IllegalArgumentException("typeAsString:" + typeName + ", type:" + type + " is null!!?");
		}
		String typeAsString = type.getName();

		/*
		 * FIXME ATR, 12.02.2017: problematic, not all data avialbel - e.g.
		 * AbstractTAsk not inside, no interfaces like org.gradle.api.Task
		 * available etc.
		 */
		boolean isTask = false;
		isTask = type.isDescendantOf("org.gradle.api.DefaultTask)");
		// isTask=isTask ||
		// type.isDescendantOf("org.gradle.api.tasks.AbstractCopyTask");
		// isTask=isTask ||
		// type.isDescendantOf("org.gradle.api.tasks.AbstractExecTask");
		/*
		 * FIXME ATR, 13.02.2017: change this!!! this is ultra ugly implemented
		 * Either use ASM or do it by reflection on runtime, but every task
		 * (everything implementing the interface )must be fetched!
		 */
		// isTask=isTask
		// type.isDescendantOf("org.gradle.api.tasks.AbstractExecTask");
		if (!isTask) {
			if (typeAsString.startsWith("org.gradle") && typeAsString.endsWith("Task")) {
				isTask = true;
			}
		}
		if (!isTask) {
			for (String acceptedAsTask : context.tasks.keySet()) {
				if (type.isDescendantOf(acceptedAsTask)) {
					isTask = true;
					break;
				}
			}
		}
		if (isTask) {
			/*
			 * FIXME ATR, 12.02.2017: determine reason for type - means plugin.
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
