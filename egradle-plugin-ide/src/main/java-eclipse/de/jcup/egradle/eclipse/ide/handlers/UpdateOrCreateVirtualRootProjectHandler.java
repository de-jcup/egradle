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
 package de.jcup.egradle.eclipse.ide.handlers;

import static de.jcup.egradle.eclipse.ide.IdeUtil.*;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.jcup.egradle.core.virtualroot.VirtualRootProjectException;
import de.jcup.egradle.eclipse.api.EclipseUtil;

public class UpdateOrCreateVirtualRootProjectHandler extends AbstractHandler {
	public static final String COMMAND_ID = "egradle.commands.updateOrCreateVirtualRootProject";
	
	private boolean running=false;
	
	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (running){
			getDialogSupport().showWarning("Virtual root project (re-)creation already running. Please wait");
			return null;
		}
		running=true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					createOrRecreateVirtualRootProject();
				} catch (VirtualRootProjectException e) {
					EclipseUtil.log(e);
					getDialogSupport().showError("Virtual root project not (re)createable. Please try again");
				}finally{
					running=false;
				}
			}
		},"Update virtual root project").start();;
		
		return null;
	}

	

}
