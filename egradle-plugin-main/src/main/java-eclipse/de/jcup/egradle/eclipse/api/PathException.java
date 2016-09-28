package de.jcup.egradle.eclipse.api;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

public class PathException extends CoreException {
	private static final long serialVersionUID = 1L;

	public PathException(int statusCode, IPath path, String message, Throwable exception) {
		super(new PathStatus(statusCode, path, message, exception));
	}

	@Override
	public void printStackTrace(PrintStream output) {
		new StackTracePrinter<PrintStream>() {

			@Override
			public void print(PrintStream output, String message) {
				output.println(message);
			}

			@Override
			public void superPrintStackTrace(PrintStream output) {
				super.printStackTrace(output);
			}

			@Override
			public void exceptionPrintStackTrace(Throwable exception, PrintStream output) {
				exception.printStackTrace(output);
			}

		}.printStackTrace(output);
	}

	@Override
	public void printStackTrace(PrintWriter output) {
		new StackTracePrinter<PrintWriter>() {

			@Override
			public void print(PrintWriter output, String message) {
				output.println(message);
			}

			@Override
			public void superPrintStackTrace(PrintWriter output) {
				super.printStackTrace(output);
			}

			@Override
			public void exceptionPrintStackTrace(Throwable exception, PrintWriter output) {
				exception.printStackTrace(output);
			}

		}.printStackTrace(output);
	}

	private abstract class StackTracePrinter<T> {

		public void printStackTrace(T output) {
			synchronized (output) {
				IStatus status = getStatus();
				Throwable exception = status.getException();
				if (exception != null) {
					String path = "()"; //$NON-NLS-1$
					if (status instanceof IResourceStatus) {
						path = "(" + ((IResourceStatus) status).getPath() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					}
					String s = getClass().getName() + path + "[" + status.getCode() + "]: ";
					print(output, s); // $NON-NLS-1$ //$NON-NLS-2$
					exceptionPrintStackTrace(exception, output);
				} else
					superPrintStackTrace(output);
			}
		}

		public abstract void print(T output, String message);

		public abstract void exceptionPrintStackTrace(Throwable exception, T output);

		public abstract void superPrintStackTrace(T output);
	}

}
