/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.egradle.eclipse.api;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

class PathException extends CoreException {
	private static final long serialVersionUID = 1L;

	PathException(int statusCode, IPath path, String message, Throwable exception) {
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
