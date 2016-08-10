package de.jcup.egradle.eclipse.execution;

import static org.apache.commons.lang3.Validate.notNull;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class GradleRunnableWithProgress implements IRunnableWithProgress {
	private GradleExecution execution;

	public GradleRunnableWithProgress(GradleExecution execution) {
		notNull(execution, "'execution' may not be null");
		this.execution=execution;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			execution.execute(monitor);
			if (!execution.getResult().isOkay()) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				MessageDialog.openWarning(window.getShell(), "Egradle",
						"Result was not okay:" + execution.getResult().getResultCode());
			}
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}

	}

}