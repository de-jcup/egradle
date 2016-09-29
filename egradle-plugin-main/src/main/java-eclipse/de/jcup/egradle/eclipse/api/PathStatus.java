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

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class PathStatus extends Status implements IResourceStatus {
	IPath path;

	public PathStatus(int type, int code, IPath path, String message, Throwable exception) {
		super(type, ResourcesPlugin.PI_RESOURCES, code, message, exception);
		this.path = path;
	}

	public PathStatus(int code, String message) {
		this(getSeverity(code), code, null, message, null);
	}

	public PathStatus(int code, IPath path, String message) {
		this(getSeverity(code), code, path, message, null);
	}

	public PathStatus(int code, IPath path, String message, Throwable exception) {
		this(getSeverity(code), code, path, message, exception);
	}

	@Override
	public IPath getPath() {
		return path;
	}

	protected static int getSeverity(int code) {
		return code == 0 ? 0 : 1 << (code % 100 / 33);
	}

	private String getTypeName() {
		switch (getSeverity()) {
		case IStatus.OK:
			return "OK"; //$NON-NLS-1$
		case IStatus.ERROR:
			return "ERROR"; //$NON-NLS-1$
		case IStatus.INFO:
			return "INFO"; //$NON-NLS-1$
		case IStatus.WARNING:
			return "WARNING"; //$NON-NLS-1$
		default:
			return String.valueOf(getSeverity());
		}
	}

	// for debug only
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[type: "); //$NON-NLS-1$
		sb.append(getTypeName());
		sb.append("], [path: "); //$NON-NLS-1$
		sb.append(getPath());
		sb.append("], [message: "); //$NON-NLS-1$
		sb.append(getMessage());
		sb.append("], [plugin: "); //$NON-NLS-1$
		sb.append(getPlugin());
		sb.append("], [exception: "); //$NON-NLS-1$
		sb.append(getException());
		sb.append("]\n"); //$NON-NLS-1$
		return sb.toString();
	}
}