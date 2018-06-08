package de.jcup.egradle.eclipse.ide.execution;

public class RootProjectMissingExecutionException extends GradleExecutionException{

	public RootProjectMissingExecutionException() {
		super("Execution not possible - undefined or unexisting root project!");
	}

	private static final long serialVersionUID = 4212768853057237732L;

}
