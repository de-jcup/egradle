package de.jcup.egradle.eclipse.ui;

import org.eclipse.core.runtime.IProgressMonitor;

import de.jcup.egradle.core.domain.CancelStateProvider;

public class ProgressMonitorCancelStateProvider implements CancelStateProvider{

	private IProgressMonitor monitor;

	public ProgressMonitorCancelStateProvider(IProgressMonitor monitor){
		this.monitor=monitor;
	}
	
	@Override
	public boolean isCanceled() {
		if (monitor==null){
			return false;
		}
		return monitor.isCanceled();
	}

}
