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
 package de.jcup.egradle.eclipse.launch;

import java.util.Map;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.RuntimeProcess;

public class EGradleRuntimeProcess extends RuntimeProcess implements IStreamListener {

	public EGradleRuntimeProcess(ILaunch launch, Process process, String name, Map<String, String> attributes) {
		super(launch, process, name, attributes);
	}

	@Override
	protected IStreamsProxy createStreamsProxy() {
		return super.createStreamsProxy();
	}

	@Override
	public void streamAppended(String text, IStreamMonitor monitor) {
	}

}