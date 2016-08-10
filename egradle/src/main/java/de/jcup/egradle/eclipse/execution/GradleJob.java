package de.jcup.egradle.eclipse.execution;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.jcup.egradle.eclipse.Activator;

public class GradleJob extends Job{
	private GradleExecution execution;
	
	public GradleJob(String name, GradleExecution execution) {
		super(name);
		this.execution=execution;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			execution.execute(monitor);
			if (!execution.getResult().isOkay()) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				MessageDialog.openWarning(window.getShell(), "Egradle",
						"Result of job was not okay:" + execution.getResult().getResultCode());
			}
		} catch (Exception e) {
			return new Status(Status.ERROR, Activator.PLUGIN_ID, "Cannot execute "+getName(), e);
		}
		return Status.OK_STATUS;
	}
	
}