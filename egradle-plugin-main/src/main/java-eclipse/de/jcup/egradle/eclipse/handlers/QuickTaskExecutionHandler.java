package de.jcup.egradle.eclipse.handlers;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.jcup.egradle.core.domain.GradleCommand;
import de.jcup.egradle.core.domain.GradleContext;
import de.jcup.egradle.core.process.OutputHandler;
import de.jcup.egradle.core.process.SimpleProcessExecutor;
import de.jcup.egradle.eclipse.execution.GradleExecutionDelegate;
import de.jcup.egradle.eclipse.execution.GradleExecutionException;
import de.jcup.egradle.eclipse.execution.UIGradleExecutionDelegate;

public class QuickTaskExecutionHandler extends AbstractEGradleCommandHandler {

	private String lastInput;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		lastInput = null;
		Shell shell = HandlerUtil.getActiveShellChecked(event);

		InputDialog dlg = new InputDialog(shell, "Quick gradle task execution", "Enter text", "Initial value", null);
		if (dlg.open() == Window.OK) {
			lastInput = dlg.getValue();
			if (StringUtils.isBlank(lastInput)){
				return null;
			}
		}else{
			return null;
		}
		return super.execute(event);
	}

	@Override
	public void prepare(GradleContext context) {
		context.setAmountOfWorkToDo(2);
		context.setCommands(GradleCommand.build(lastInput));
	}

	@Override
	protected GradleExecutionDelegate createGradleExecution(OutputHandler outputHandler)
			throws GradleExecutionException {
		UIGradleExecutionDelegate ui = new UIGradleExecutionDelegate(outputHandler,
				new SimpleProcessExecutor(outputHandler, true, SimpleProcessExecutor.ENDLESS_RUNNING), this);
		ui.setRefreshAllProjects(false);
		ui.setShowEGradleSystemConsole(true);
		return ui;
	}
}
