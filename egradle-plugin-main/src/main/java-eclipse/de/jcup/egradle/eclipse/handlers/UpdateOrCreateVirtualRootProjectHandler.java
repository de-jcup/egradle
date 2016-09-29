package de.jcup.egradle.eclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.jcup.egradle.core.virtualroot.VirtualRootProjectException;
import de.jcup.egradle.eclipse.api.EGradleUtil;

public class UpdateOrCreateVirtualRootProjectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		try {
			EGradleUtil.createOrUpdateVirtualRootProject();
		} catch (VirtualRootProjectException e) {
			throw new ExecutionException("Virtual root project not (re)createable", e);
		}
		return null;
	}

	

}
